package EzyShop.model.orders;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingInfoSnapshot {

    private String recipientFirstName;
    private String recipientLastName;
    private String phoneNumber;

    private String streetAddress;
    private String apartment;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
}
