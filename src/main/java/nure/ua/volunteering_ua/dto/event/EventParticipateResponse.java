package nure.ua.volunteering_ua.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.dto.customer.CustomerGetDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipateResponse {
    CustomerGetDto customer;
    int capacity;

}
