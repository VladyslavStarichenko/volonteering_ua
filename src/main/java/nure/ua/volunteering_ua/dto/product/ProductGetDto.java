package nure.ua.volunteering_ua.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductGetDto {

    private String name;
    private String description;
    private Integer amount;
    private String image;
    private String organizationName;
    private String eventName;

}
