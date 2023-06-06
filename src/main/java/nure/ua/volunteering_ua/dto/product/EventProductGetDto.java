package nure.ua.volunteering_ua.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductGetDto {

    private Long id;
    private Long productId;
    private Long eventId;
    private int amount;

}
