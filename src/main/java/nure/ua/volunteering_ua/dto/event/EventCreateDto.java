package nure.ua.volunteering_ua.dto.event;

import lombok.Data;
import nure.ua.volunteering_ua.dto.location.LocationDto;

import java.util.Date;


@Data
public class EventCreateDto {
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private LocationDto location;
    private int capacity;
}
