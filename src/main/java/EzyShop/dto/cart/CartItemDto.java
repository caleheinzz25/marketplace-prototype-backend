package EzyShop.dto.cart;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productThumbnail;
    private String productSku;
    private BigDecimal price;
    private String brand;
    private Integer quantity;
    private BigDecimal subtotal;
    private Integer maxStock;
    private String storeName; // opsional jika produk multistore
}