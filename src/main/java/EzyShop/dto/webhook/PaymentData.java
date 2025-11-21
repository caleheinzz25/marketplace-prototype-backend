package EzyShop.dto.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import EzyShop.dto.payment.ChannelProperties;
import EzyShop.model.orders.ChannelCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentData {
    private String paymentId;
    private String businessId;
    private String referenceId;
    private String paymentRequestId;
    private String customerId;
    private String type;
    private String country;
    private String currency;
    private long requestAmount;
    private String captureMethod;
    private ChannelCode channelCode;
    private String status;
    private List<CaptureInfo> captures;
    private PaymentDetails paymentDetails;
    private ZonedDateTime created;
    private ZonedDateTime updated;
    private String description;
    private ChannelProperties channelProperties;
    private List<Object> actions; // kosong / tidak diketahui strukturnya
    private String paymentTokenId;
    private TokenDetails tokenDetails;
}
