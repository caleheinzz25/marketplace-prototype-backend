package EzyShop.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import EzyShop.dto.order.OrderDto;
import EzyShop.dto.order.OrderItemDto;
import EzyShop.dto.order.TransactionDto;
import EzyShop.dto.payment.PaymentResponse;
import EzyShop.model.User;
import EzyShop.model.orders.Order;
import EzyShop.model.orders.OrderItem;
import EzyShop.model.orders.OrderStatus;
import EzyShop.model.orders.PaymentStatus;
import EzyShop.model.orders.Transaction;
import EzyShop.utils.XenditUtil;

public class OrderMapper {

    public static void applyPaymentResponse(Transaction transaction, PaymentResponse dto, XenditUtil xenditUtil) {
        // === Set nilai pada entitas Transaction ===
        transaction.setReferenceId(dto.getReferenceId());
        transaction.setActionValue(dto.getFirstActionValue());
        transaction.setChannelCode(dto.getChannelCode());
        transaction.setStatus(dto.getStatus());
        transaction.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        transaction.setPaymentCreatedAt(dto.getCreated());
        transaction.setPaymentUpdatedAt(dto.getUpdated());
        // === Map ke status Order (tanpa sentuh field yang tidak ada di Order) ===
        // Map status ke Order
        OrderStatus orderStatus = mapPaymentStatusToOrderStatus(dto.getStatus());

        for (Order order : transaction.getOrders()) {
            order.setStatus(orderStatus);
            order.setReferenceId(dto.getReferenceId());

            if (dto.getStatus() == PaymentStatus.SUCCEEDED) {
                order.setPaymentReceivedAt(transaction.getPaymentReceivedAt());
            }
        }

        // Hitung ulang total jika ada perhitungan dinamis
        transaction.calculateTotalAmount();
    }

    public static OrderItemDto toItemDTO(OrderItem item) {
        if (item == null)
            return null;

        return OrderItemDto.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .brand(item.getBrand())
                .productSku(item.getProductSku())
                .productImage(item.getProductImage())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    public static TransactionDto toTransactionDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .referenceId(transaction.getReferenceId())
                .userId(transaction.getUser() != null ? transaction.getUser().getId() : null)

                .orders(transaction.getOrders() != null
                        ? transaction.getOrders().stream()
                                .map(OrderMapper::toDto)
                                .collect(Collectors.toList())
                        : null)

                .status(transaction.getStatus())
                .channelCode(transaction.getChannelCode())
                .shippingAddress(transaction.getShippingInfo())
                .totalAmount(transaction.getTotalAmount())
                .shippingCost(transaction.getShippingCost())
                .tax(transaction.getTax())
                .actionValue(transaction.getActionValue())
                .subTotal(transaction.getSubTotal())
                .expiresAt(transaction.getExpiresAt())
                .paymentCreatedAt(transaction.getPaymentCreatedAt())
                .paymentReceivedAt(transaction.getPaymentReceivedAt())
                .paymentUpdatedAt(transaction.getPaymentUpdatedAt())
                .status(transaction.getStatus())
                .actions(transaction.getActions())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    public static List<OrderItemDto> toItemDTOList(List<OrderItem> items) {
        if (items == null)
            return Collections.emptyList();
        return items.stream().map(OrderMapper::toItemDTO).toList();
    }

    public static OrderDto toDto(Order order) {
        if (order == null)
            return null;
        User user = order.getUser() != null ? order.getUser() : null;

        return OrderDto.builder()
                .id(order.getId())
                .referenceId(order.getReferenceId())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .shippingCost(order.getShippingCost())
                .totalAmount(order.getTotalAmount())
                .paymentReceivedAt(order.getPaymentReceivedAt())
                .channelCode(order.getChannelCode())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .userEmail(user.getEmail())
                .userFullName(user.getFullName())
                .userImage(user.getUserImage())
                .storeName(order.getStore() != null ? order.getStore().getStoreName() : null)
                .items(toItemDTOList(order.getItems()))
                .build();
    }

    public static OrderStatus mapPaymentStatusToOrderStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case SUCCEEDED -> OrderStatus.PAID;
            case CANCELED -> OrderStatus.CANCELED;
            case FAILED, EXPIRED, PENDING, REQUIRES_ACTION -> OrderStatus.PENDING_PAYMENT;
        };
    }
}
