package EzyShop.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WebhookResponse {
    private String event;
    private String businessId;
    private ZonedDateTime created;
    private WebhookData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class WebhookData {
        private String paymentId;
        private String businessId;
        private String referenceId;
        private String paymentRequestId;
        private String type;
        private String country;
        private String currency;
        private BigDecimal requestAmount;
        private String captureMethod;
        private String channelCode;
        private ChannelProperties channelProperties;
        private List<Capture> captures;
        private String status;
        private PaymentDetails paymentDetails;
        private Map<String, Object> metadata;
        private ZonedDateTime created;
        private ZonedDateTime updated;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Capture {
            private ZonedDateTime captureTimestamp;
            private String captureId;
            private BigDecimal captureAmount;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class PaymentDetails {
            private String remark;
        }
    }
}
