package EzyShop.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private BigDecimal rating;
    private Integer stock;
    private List<String> tags;
    private String brand;
    private String sku;
    private BigDecimal weight;
    private DimensionDto dimensions;
    private String warrantyInformation;
    private String shippingInformation;
    private String availableStatus;
    private List<ReviewDto> reviews;
    private String returnPolicy;
    private Integer minimumOrderQuantity;
    private MetaDto meta;
    private List<String> images;
    private String thumbnail;
}
