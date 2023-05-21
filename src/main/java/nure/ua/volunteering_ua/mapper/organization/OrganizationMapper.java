package nure.ua.volunteering_ua.mapper.organization;

import nure.ua.volunteering_ua.dto.organization.OrganizationGetDto;
import nure.ua.volunteering_ua.mapper.*;
import nure.ua.volunteering_ua.model.Feedback;
import nure.ua.volunteering_ua.model.Statistic;
import nure.ua.volunteering_ua.model.user.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrganizationMapper implements Function<Organization, OrganizationGetDto> {

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private FeedBackMapper feedBackMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private VolunteeringMapper volunteeringMapper;
    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private LocationMapper locationMapper;

    private final Statistic statistic = new Statistic();

    @Override
    public OrganizationGetDto apply(Organization organization) {
        return new OrganizationGetDto(
                organization.getName(),
                organization.getImageURL() != null ? organization.getImageURL() : "",
//                Optional.ofNullable(organization.getImageURL()),
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
