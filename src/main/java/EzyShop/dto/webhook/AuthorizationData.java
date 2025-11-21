package EzyShop.dto.webhook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthorizationData {
    private String cvnVerificationResult;
    private String addressVerificationResult;
    private String networkResponseCode;
    private String networkResponseCodeDescriptor;
    private String authorizationCode;
    private String retrievalReferenceNumber;
    private String acquirerMerchantId;
    private String reconciliationId;
    private String networkTransactionId;
}
