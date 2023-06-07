package nure.ua.volunteering_ua.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductGetDto {

    private Long id;
    private Long product_id;
    private String product_name;
    private int amount;
    private String description;
    private String image;
    private String event_name;


}
