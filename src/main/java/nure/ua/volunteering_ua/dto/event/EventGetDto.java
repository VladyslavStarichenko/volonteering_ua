package nure.ua.volunteering_ua.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.dto.LocationDto;
import nure.ua.volunteering_ua.dto.product.ProductGetDto;
import nure.ua.volunteering_ua.model.user.Location;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventGetDto {

    private long id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private List<ProductGetDto> products;
    private LocationDto location;

}
