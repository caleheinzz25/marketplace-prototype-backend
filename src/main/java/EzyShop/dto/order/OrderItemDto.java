package EzyShop.dto.order;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private String brand;
    private String productSku;
    private String productImage;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
