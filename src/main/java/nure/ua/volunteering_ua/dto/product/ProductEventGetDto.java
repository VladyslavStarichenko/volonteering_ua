package nure.ua.volunteering_ua.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEventGetDto {

    private long id;
    private String name;
    private String description;
    private Integer amount;
    private String image;
    private String eventName;
    public ProductEventGetDto(Long id, String name, String description, Integer amount, String image, String eventName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.image = image;
        this.eventName = eventName;
    }
}
