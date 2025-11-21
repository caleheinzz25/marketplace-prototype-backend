package EzyShop.dto.order;

import EzyShop.model.orders.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderChartDto {
    private Long orderId;
    private String referenceId;
    private String storeName; // from Store
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private Instant paymentReceivedAt;
}
