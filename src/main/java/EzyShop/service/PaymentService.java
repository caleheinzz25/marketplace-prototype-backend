package EzyShop.service;

import EzyShop.dto.cart.CartDto;
import EzyShop.dto.payment.PaymentRequest;
import EzyShop.dto.payment.PaymentResponse;
import EzyShop.exception.BusinessException;
import EzyShop.exception.ResourceNotFoundException;
import EzyShop.mapper.MapperBank;
import EzyShop.mapper.MapperEWallet;
import EzyShop.model.User;
import EzyShop.model.orders.*;
import EzyShop.model.products.Product;
import EzyShop.model.store.Store;
import EzyShop.repository.OrderRepository;
import EzyShop.repository.ProductRepository;
import EzyShop.repository.TransactionRepository;
import EzyShop.utils.XenditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RestTemplate restTemplate;
    private final XenditUtil xenditUtil;
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    private static final String XENDIT_API_URL = "https://api.xendit.co";
    private static final String PAYMENT_ENDPOINT = "/v3/payment_requests";

    private PaymentResponse handlePayment(
            PaymentRequest paymentRequestDto,
            Long userId,
            Function<PaymentRequest, PaymentRequest> mapper) {
        HttpHeaders headers = xenditUtil.buildHeaders();
        PaymentRequest requestBody = mapper.apply(paymentRequestDto);
        log.info("Payment Request: {}", requestBody);
        HttpEntity<String> entity = new HttpEntity<>(xenditUtil.toJson(requestBody), headers);
        ResponseEntity<PaymentResponse> response;
        try {
            response = restTemplate.exchange(
                    XENDIT_API_URL + PAYMENT_ENDPOINT,
                    HttpMethod.POST,
                    entity,
                    PaymentResponse.class);
        } catch (Exception ex) {
            log.error("Error while calling Xendit API", ex);
            throw new BusinessException("Gagal memproses pembayaran ke Xendit: " + ex.getMessage(),
                    HttpStatus.BAD_GATEWAY);
        }
        log.info("Payment Request: {}", response.getBody());
        PaymentResponse paymentResponse = response.getBody();
        if (paymentResponse == null) {
            throw new BusinessException("Payment is Empty", HttpStatus.BAD_REQUEST);
        }

        String actionValue = Optional.ofNullable(paymentResponse.getActions())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).getValue())
                .orElse(null);

        Transaction transaction = Transaction.builder()
                .referenceId(paymentResponse.getReferenceId())
                .user(User.builder().id(userId).build())
                .status(paymentResponse.getStatus())
                .tax(paymentRequestDto.getTax())
                .channelCode(paymentResponse.getChannelCode())
                .actionValue(actionValue)
                .expiresAt(paymentResponse.getChannelProperties().getExpiresAt())
                .paymentCreatedAt(paymentResponse.getCreated())
                .paymentUpdatedAt(paymentResponse.getUpdated())
                .actions(paymentResponse.getActions())
                .shippingInfo(paymentRequestDto.getShippingAddress())
                .build();

        for (CartDto cart : paymentRequestDto.getCarts()) {
            Product product = productRepository.findById(cart.getItems().getFirst().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            Store store = product.getStore();
            if (store == null) {
                throw new ResourceNotFoundException("Store not found");
            }

            List<OrderItem> items = cart.getItems().stream().map(cartItem -> OrderItem.builder()
                    .productId(cartItem.getId())
                    .productName(cartItem.getProductTitle())
                    .productImage(cartItem.getProductThumbnail())
                    .brand(cartItem.getBrand())
                    .price(cartItem.getPrice())
                    .productSku(cartItem.getProductSku())
                    .quantity(cartItem.getQuantity())
                    .build())
                    .toList();

            Order order = Order.builder()
                    .referenceId(transaction.getReferenceId())
                    .store(store)
                    .channelCode(transaction.getChannelCode())
                    .user(User.builder().id(userId).build())
                    .status(OrderStatus.PENDING_PAYMENT)
                    .paymentReceivedAt(paymentResponse.getCreated())
                    .shippingType(paymentRequestDto.getShippingType())
                    .shippingCost(paymentRequestDto.getShippingType().getCost())
                    .build();

            items.forEach(order::addItem);
            order.calculateAndSetTotal();
            transaction.addOrder(order);
        }

        transaction.calculateTotalAmount();
        transaction.calculateSubTotal();
        transaction.calculateShippingCost();

        transactionRepository.save(transaction);

        paymentResponse.setSubTotal(transaction.getSubTotal());
        paymentResponse.setShippingCost(transaction.getShippingCost());
        paymentResponse.setTax(transaction.getTax());
        paymentResponse.setTotalAmount(transaction.getTotalAmount());
        return paymentResponse;
    }

    public PaymentResponse createDANAPayment(PaymentRequest dto, Long userId) {
        return handlePayment(dto, userId, MapperEWallet::DanaRequestToDto);
    }

    public PaymentResponse createGopayPayment(PaymentRequest dto, Long userId) {
        return handlePayment(dto, userId, MapperEWallet::GopayRequestToDto);
    }

    public PaymentResponse createQrisPayment(PaymentRequest dto, Long userId) {
        return handlePayment(dto, userId, MapperEWallet::QrisRequestToDto);
    }

    public PaymentResponse createBcaVaPayment(PaymentRequest dto, Long userId) {
        return handlePayment(dto, userId, MapperBank::mapToBcaVaPaymentRequest);
    }
}
