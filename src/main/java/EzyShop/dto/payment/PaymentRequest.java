package EzyShop.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import EzyShop.dto.cart.CartDto;
import EzyShop.model.orders.ChannelCode;
import EzyShop.model.orders.ShippingInfoSnapshot;
import EzyShop.model.orders.ShippingType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentRequest {
    private String referenceId;
    private Pay type; // e.g., "PAY"
    private String country; // e.g., "PH"
    private String currency; // e.g., "PHP"
    private BigDecimal requestAmount; // e.g., 10000.01
    private String captureMethod; // e.g., "AUTOMATIC"
    private ChannelCode channelCode; // e.g., "GCASH"
    private ChannelProperties channelProperties;
    private List<CartDto> carts;
    private BigDecimal tax;
    private ShippingType shippingType;
    private String accountBalance;
    private String accountName;
    private String accountPointBalance;
    private String accountNumber;
    private ShippingInfoSnapshot shippingAddress;
    private String description; // Optional description field
    private Map<String, Object> metadata; // Flexible structure
}
