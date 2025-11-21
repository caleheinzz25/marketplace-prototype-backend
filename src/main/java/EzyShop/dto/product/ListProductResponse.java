package EzyShop.dto.product;

import java.util.List;

import lombok.Data;

@Data
public class ListProductResponse {
    private List<ProductDto> products;
    private long total;
    private int skip;
    private int limit;
}