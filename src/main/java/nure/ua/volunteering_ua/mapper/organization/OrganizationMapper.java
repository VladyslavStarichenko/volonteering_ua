package nure.ua.volunteering_ua.mapper.organization;

import nure.ua.volunteering_ua.dto.organization.OrganizationGetDto;
import nure.ua.volunteering_ua.mapper.customer.CustomerMapper;
import nure.ua.volunteering_ua.mapper.event.EventMapper;
import nure.ua.volunteering_ua.mapper.feedback.FeedBackMapper;
import nure.ua.volunteering_ua.mapper.location.LocationMapper;
import nure.ua.volunteering_ua.mapper.product.ProductMapper;
import nure.ua.volunteering_ua.mapper.request.RequestMapper;
import nure.ua.volunteering_ua.mapper.volunteer.VolunteeringMapper;
import nure.ua.volunteering_ua.model.Feedback;
import nure.ua.volunteering_ua.model.Statistic;
import nure.ua.volunteering_ua.model.user.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrganizationMapper implements Function<Organization, OrganizationGetDto> {

    private final EventMapper eventMapper;
    private final ProductMapper productMapper;
    private final FeedBackMapper feedBackMapper;
    private final CustomerMapper customerMapper;
    private final VolunteeringMapper volunteeringMapper;
    private final RequestMapper requestMapper;
    private final LocationMapper locationMapper;
    private final Statistic statistic = new Statistic();
    @Autowired
    public OrganizationMapper(EventMapper eventMapper, ProductMapper productMapper, FeedBackMapper feedBackMapper, CustomerMapper customerMapper, VolunteeringMapper volunteeringMapper, RequestMapper requestMapper, LocationMapper locationMapper) {
        this.eventMapper = eventMapper;
        this.productMapper = productMapper;
        this.feedBackMapper = feedBackMapper;
        this.customerMapper = customerMapper;
        this.volunteeringMapper = volunteeringMapper;
        this.requestMapper = requestMapper;
        this.locationMapper = locationMapper;
    }

    @Override
    public OrganizationGetDto apply(Organization organization) {
        return new OrganizationGetDto(
                organization.getId(),
                organization.getName(),
                organization.getImageURL() != null ? organization.getImageURL() : "",
                organization.getDescription(),
                organization.getOrganization_admin().getUserName(),
                organization.getVolunteeringType().name(),
                organization.getSubscribers()
                        .stream()
                        .map(customerMapper)
                        .collect(Collectors.toList()),
                organization.getVolunteers()
                        .stream()
                        .map(volunteeringMapper)
                        .collect(Collectors.toList()),
                organization.getRequests()
                        .stream()
                        .map(requestMapper)
                        .collect(Collectors.toList()),
                Stream.of(organization.getLocation())
                        .map(locationMapper)
                        .findAny()
                        .get(),
                organization.getEvents()
                        .stream()
                        .map(eventMapper)
                        .collect(Collectors.toList()),
                organization.getProducts()
                        .stream()
                        .map(productMapper)
                        .collect(Collectors.toList()),
                organization.getFeedbacks()
                        .stream()
                        .map(feedBackMapper)
                        .collect(Collectors.toList()),
                statistic.getStatistic(organization),
                organization.getFeedbacks()
                        .stream()
                        .map(Feedback::getRating)
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0)

        );
    }
}
