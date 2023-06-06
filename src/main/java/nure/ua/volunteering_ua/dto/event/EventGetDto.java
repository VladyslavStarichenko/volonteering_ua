package nure.ua.volunteering_ua.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.dto.customer.CustomerGetDto;
import nure.ua.volunteering_ua.dto.location.LocationDto;
import nure.ua.volunteering_ua.dto.product.EventProductGetDto;

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
    private List<EventProductGetDto> products;
    private List<CustomerGetDto> customers;
    private LocationDto location;
    private int capacity;
    private String organization;

}
