package nure.ua.volunteering_ua.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.dto.LocationDto;
import nure.ua.volunteering_ua.dto.customer.CustomerGetDto;
import nure.ua.volunteering_ua.dto.event.EventGetDto;
import nure.ua.volunteering_ua.dto.feedback.FeedBackGetDto;
import nure.ua.volunteering_ua.dto.product.ProductGetDto;
import nure.ua.volunteering_ua.dto.request.RequestGetDto;
import nure.ua.volunteering_ua.dto.volunteer.VolunteerGetDto;
import nure.ua.volunteering_ua.model.Statistic;
import nure.ua.volunteering_ua.model.user.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationGetDto {

    private String name;
    private String admin;
    private String type;
    private List<CustomerGetDto> subscribers;
    private List<VolunteerGetDto> volunteers;
    private List<RequestGetDto> requests;
    private LocationDto location;
    private List<EventGetDto> events;
    private List<ProductGetDto> products;
    private List<FeedBackGetDto> feedbacks;
    private Statistic statistic;
}
