package nure.ua.volunteering_ua.mapper.organization;

import nure.ua.volunteering_ua.dto.organization.OrganizationGetDto;
import nure.ua.volunteering_ua.mapper.*;
import nure.ua.volunteering_ua.model.Statistic;
import nure.ua.volunteering_ua.model.user.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

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

    private final Statistic statistic = new Statistic();

    @Override
    public OrganizationGetDto apply(Organization organization) {
        return new OrganizationGetDto(
                organization.getName(),
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
                organization.getLocation(),
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
                statistic.getStatistic(organization)

        );
    }
}
