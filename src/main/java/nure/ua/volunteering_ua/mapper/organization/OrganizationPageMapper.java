package nure.ua.volunteering_ua.mapper.organization;

import nure.ua.volunteering_ua.dto.organization.OrganizationPageResponse;
import nure.ua.volunteering_ua.model.user.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrganizationPageMapper implements Function<Page<Organization>, OrganizationPageResponse> {

    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    public OrganizationPageResponse apply(Page<Organization> organizations) {
        return new OrganizationPageResponse(
                organizations.getContent()
                        .stream()
                        .map(organizationMapper)
                        .collect(Collectors.toList()),
                organizations.getNumber(),
                organizations.getContent().size(),
                organizations.getTotalElements(),
                organizations.getTotalPages()
        );
    }
}
