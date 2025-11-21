package EzyShop.dto.product;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaDto {
    private Instant createdAt;
    private Instant updatedAt;
    private String barcode;
    private String qrCode;
}