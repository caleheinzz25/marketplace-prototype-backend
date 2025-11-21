package EzyShop.dto.order;

import EzyShop.dto.store.StoreDto;
import EzyShop.model.orders.ChannelCode;
import EzyShop.model.orders.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String referenceId;
    private String storeName;
    private ChannelCode channelCode;
    private OrderStatus status;

    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;

    private Instant paymentReceivedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private StoreDto store;
    private String userFullName;
    private String userEmail;
    private String userImage;
    private List<OrderItemDto> items;
}
