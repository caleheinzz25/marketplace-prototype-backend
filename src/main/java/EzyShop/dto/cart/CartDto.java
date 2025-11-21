package EzyShop.dto.cart;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDto {
    private Long id;
    private Long userId;
    private Long storeId;
    private List<CartItemDto> items;
    private BigDecimal total;
    private boolean checkedOut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
