package EzyShop.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import EzyShop.model.orders.ChannelCode;
import EzyShop.model.orders.PaymentStatus;
import EzyShop.model.orders.ShippingInfoSnapshot;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentResponse {

    private String paymentRequestId;
    private String country;
    private String currency;
    private String businessId;
    private String referenceId;
    private Instant created;
    private Instant updated;
    private BigDecimal totalAmount;
    private BigDecimal subTotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private PaymentStatus status;
    private String captureMethod;
    private ChannelCode channelCode;
    private BigDecimal requestAmount;
    private ChannelProperties channelProperties;
    private String type;
    private List<ActionDto> actions;
    private String description;
    private Map<String, Object> metadata;

    public String getFirstActionType() {
        return (actions != null && !actions.isEmpty()) ? actions.get(0).getType() : null;
    }

    public String getFirstActionValue() {
        return (actions != null && !actions.isEmpty()) ? actions.get(0).getValue() : null;
    }

    public String getFirstActionDescriptor() {
        return (actions != null && !actions.isEmpty()) ? actions.get(0).getDescriptor() : null;
    }
}
