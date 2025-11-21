package EzyShop.dto.store;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto {

    private Long id;

    @NotBlank(message = "Store name is required")
    @Size(max = 100, message = "Store name must be at most 100 characters")
    private String storeName;

    @Size(max = 255, message = "Logo URL must be at most 255 characters")
    private String logUrl;


    private String storeNo;
    @Size(max = 255, message = "Store email must be at most 255 characters")
    private String storeEmail;

    @Size(max = 14, message = "Contact phone must be at most 14 characters")
    private String contactPhone;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @NotBlank(message = "Store type is required")
    private String storeType;
    private boolean enabled;
    private BigDecimal saldo;

    private String ownerUsername; // Optional: no validation
}
