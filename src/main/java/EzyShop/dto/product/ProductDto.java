package EzyShop.dto.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import EzyShop.model.products.AvailabilityStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Rating must be non-negative")
    private BigDecimal rating;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private Set<String> tags;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    private BigDecimal weight;

    private DimensionDto dimensions;

    private String warrantyInformation;
    private String shippingInformation;

    private AvailabilityStatus availabilityStatus;

    private List<ReviewDto> reviews;

    private String returnPolicy;

    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minimumOrderQuantity;

    private MetaDto meta;

    @NotEmpty(message = "At least one image URL is required")
    private List<String> images;

    @NotBlank(message = "Thumbnail URL is required")
    private String thumbnail;   

    private boolean enabled;

    // Additional fields
    @NotNull(message = "Store ID is required")
    private Long storeId;

    private Instant createdAt;
    private Instant updateAt;

    public boolean getEnabled() {
        return this.enabled;
    }
}