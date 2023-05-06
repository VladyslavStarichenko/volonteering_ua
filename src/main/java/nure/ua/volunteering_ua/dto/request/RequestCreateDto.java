package nure.ua.volunteering_ua.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.model.RequestDeliveryType;
import nure.ua.volunteering_ua.model.user.VolunteeringType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreateDto {

    private String title;
    private String description;
    private int amount;
    private String organizationName;
    private VolunteeringType volunteering_type;
    private RequestDeliveryType deliveryType;
}
