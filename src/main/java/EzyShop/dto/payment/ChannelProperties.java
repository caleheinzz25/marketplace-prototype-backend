package EzyShop.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChannelProperties {

    private String successReturnUrl;
    private String failureReturnUrl;
    private String cancelReturnUrl;
    private String pendingReturnUrl;

    private Instant expiresAt;
    private String payerName;
    private String displayName;
    private String paymentCode;
    private String virtualAccountNumber;
    private Integer suggestedAmount;
    private String cashTag;

    private CardDetails cardDetails;
    private BillingInformation billingInformation;
    private String statementDescriptor;
    private RecurringConfiguration recurringConfiguration;

    private String accountEmail;
    private String accountMobileNumber;
    private String cardLastFour;
    private String cardExpiry;
    private Boolean enableOtp;
    private List<String> allowedPaymentOptions;
    private String redeemPoints;
    private String deviceType;
    private String payerIpAddress;
    private Boolean skipThreeDs;
    private String cardOnFileType;

    private InstallmentConfiguration installmentConfiguration;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CardDetails {
        private String cvn;
        private String cardNumber;
        private String expiryYear;
        private String expiryMonth;
        private String cardholderName;
        private String maskedCardNumber;
        private String fingerprint;
        private String type; // e.g. "CREDIT"
        private String network;
        private String country;
        private String issuer;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class BillingInformation {
        private String city;
        private String country;
        private String postalCode;
        private String streetLine1;
        private String streetLine2;
        private String provinceState;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecurringConfiguration {
        private String recurringExpiry; // Format: YYYY-MM-DD
        private Integer recurringFrequency; // in days
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class InstallmentConfiguration {
        private List<Integer> terms; // e.g. [3,6,12]
        private String interval; // e.g. "MONTH"
    }
}
