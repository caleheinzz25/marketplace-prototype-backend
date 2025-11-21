package EzyShop.service;

import EzyShop.dto.webhook.XenditPaymentWebhook;
import EzyShop.model.orders.*;
import EzyShop.repository.InvoiceRepository;
import EzyShop.repository.OrderRepository;
import EzyShop.repository.ProductRepository;
import EzyShop.repository.TransactionRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final InvoiceRepository invoiceRepository;
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void handlePaymentWebhook(XenditPaymentWebhook webhook) {
        if (webhook == null || webhook.getData() == null) {
            log.warn("Empty webhook received");
            return;
        }

        String event = webhook.getEvent();
        String referenceId = webhook.getData().getReferenceId();

        if (referenceId == null) {
            log.warn("Missing referenceId in webhook");
            return;
        }

        Optional<Transaction> optionalTransaction = transactionRepository.findByReferenceId(referenceId);
        if (optionalTransaction.isEmpty()) {
            log.warn("Transaction not found for referenceId: {}", referenceId);
            return;
        }

        Transaction transaction = optionalTransaction.get();
        log.info("Handling webhook event [{}] for transaction ID {}", event, transaction.getId());

        switch (event) {
            case "payment.capture" -> handleCapture(transaction, webhook);
            case "payment.failure" -> handleFailure(transaction, webhook);
            case "payment.expired" -> handleExpired(transaction, webhook);
            default -> {
                log.warn("Unhandled event type: {}", event);
                return;
            }
        }

        transactionRepository.save(transaction);
    }

    private void handleCapture(Transaction transaction, XenditPaymentWebhook webhook) {
        var data = webhook.getData();

        if (transaction.getStatus() == PaymentStatus.SUCCEEDED) {
            log.info("Transaction {} already marked as SUCCEEDED. Skipping processing.", transaction.getId());
            return;
        }

        transaction.setStatus(PaymentStatus.SUCCEEDED);
        transaction.setPaymentCreatedAt(toInstant(data.getCreated()));
        transaction.setPaymentUpdatedAt(toInstant(data.getUpdated()));
        transaction.setPaymentReceivedAt(data.getCaptures().stream()
                .findFirst().map(c -> toInstant(c.getCaptureTimestamp())).orElse(null));
        transaction.setChannelCode(data.getChannelCode());

        for (Order order : transaction.getOrders()) {
            if (order.getStatus() == OrderStatus.PAID)
                continue;

            order.setStatus(OrderStatus.PAID);
            order.setChannelCode(data.getChannelCode());

            // Hitung ulang total
            order.calculateAndSetTotal();

            // Kurangi stok
            order.getItems().forEach(orderItem -> {
                productRepository.findById(orderItem.getProductId()).ifPresent(product -> {
                    int newStock = product.getStock() - orderItem.getQuantity();
                    if (newStock < 0) {
                        throw new IllegalStateException("Stok produk " + product.getTitle() + " tidak mencukupi!");
                    }
                    product.setStock(newStock);
                    productRepository.save(product);
                });
            });

            // Buat invoice
            String invoiceNumber = generateInvoiceNumber(order.getCreatedAt());

            Invoice invoice = Invoice.builder()
                    .buyer(order.getUser())
                    .order(order)
                    .status(OrderStatus.PAID)
                    .invoiceNumber(invoiceNumber)
                    .paymentMethod(order.getChannelCode())
                    .totalAmount(order.getTotalAmount())
                    .issuedAt(Instant.now())
                    .paidAt(Instant.now())
                    .build();

            List<InvoiceItem> invoiceItems = order.getItems().stream()
                    .map(orderItem -> InvoiceItem.builder()
                            .productId(orderItem.getProductId())
                            .productName(orderItem.getProductName())
                            .productImage(orderItem.getProductImage())
                            .quantity(orderItem.getQuantity())
                            .price(orderItem.getPrice())
                            .subtotal(orderItem.getSubtotal())
                            .invoice(invoice)
                            .build())
                    .toList();

            invoice.setItems(invoiceItems);
            invoiceRepository.save(invoice);
        }
    }

    private void handleFailure(Transaction transaction, XenditPaymentWebhook webhook) {
        transaction.setStatus(PaymentStatus.FAILED);
        transaction.setPaymentUpdatedAt(toInstant(webhook.getCreated()));

        for (Order order : transaction.getOrders()) {
            order.setStatus(OrderStatus.CANCELED);
        }
    }

    private void handleExpired(Transaction transaction, XenditPaymentWebhook webhook) {
        transaction.setStatus(PaymentStatus.EXPIRED);
        transaction.setPaymentUpdatedAt(toInstant(webhook.getCreated()));

        for (Order order : transaction.getOrders()) {
            order.setStatus(OrderStatus.EXPIRED);
        }
    }

    private Instant toInstant(ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? zonedDateTime.toInstant() : null;
    }

    private String generateInvoiceNumber(Instant createdAt) {
        String prefix = "INV";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(ZoneId.of("Asia/Jakarta")); // zona lokal untuk kode invoice
        String timestamp = formatter.format(createdAt);
        String randomPart = String.valueOf((int) (Math.random() * 9000) + 1000); // 4-digit acak

        return prefix + "-" + timestamp + "-" + randomPart;
    }

}
