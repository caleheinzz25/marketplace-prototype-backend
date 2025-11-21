package EzyShop.dto.cart;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartItemRequest {
    private Long itemId;
    private int quantity;
}
