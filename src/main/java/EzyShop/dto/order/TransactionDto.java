package EzyShop.dto.order;

import EzyShop.dto.payment.ActionDto;
import EzyShop.model.orders.ChannelCode;
import EzyShop.model.orders.PaymentStatus;
import EzyShop.model.orders.ShippingInfoSnapshot;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class TransactionDto {

    private Long id;
    private String referenceId;

    private Long userId;

    private List<OrderDto> orders;

    private PaymentStatus   status;
    private ChannelCode channelCode;
    private BigDecimal subTotal;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private BigDecimal tax;
    private String userFullName;
    private String userEmail;
    private String actionValue;
    private List<ActionDto> actions;
    private Instant expiresAt;
    private Instant paymentCreatedAt;
    private Instant paymentReceivedAt;
    private Instant paymentUpdatedAt;
    private ShippingInfoSnapshot shippingAddress;
    private Instant createdAt;
    private Instant updatedAt;
}
