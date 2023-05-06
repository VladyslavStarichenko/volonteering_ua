package nure.ua.volunteering_ua.service.organization;

import nure.ua.volunteering_ua.dto.organization.OrganizationCreateDto;
import nure.ua.volunteering_ua.dto.organization.OrganizationGetDto;
import nure.ua.volunteering_ua.dto.organization.OrganizationPageResponse;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.organization.OrganizationMapper;
import nure.ua.volunteering_ua.mapper.organization.OrganizationPageMapper;
import nure.ua.volunteering_ua.model.Statistic;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.model.user.User;
import nure.ua.volunteering_ua.model.user.VolunteeringType;
import nure.ua.volunteering_ua.repository.organization.OrganizationRepository;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserServiceSCRT userServiceSCRT;
    private final OrganizationMapper organizationMapper;
    private final OrganizationPageMapper organizationPageMapper;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, UserServiceSCRT userServiceSCRT, OrganizationMapper organizationMapper, OrganizationPageMapper organizationPageMapper) {
        this.organizationRepository = organizationRepository;
        this.userServiceSCRT = userServiceSCRT;
        this.organizationMapper = organizationMapper;
        this.organizationPageMapper = organizationPageMapper;
    }

    public OrganizationGetDto createOrganization(OrganizationCreateDto organizationCreateDto) {
        User admin = getOrganizationAdmin();
        Organization organization = new Organization(
                organizationCreateDto.getName(),
                organizationCreateDto.getVolunteeringType(),
                admin,
                organizationCreateDto.getLocation()
        );
        checkOrganization(organization);
        OrganizationGetDto organizationGetDto = organizationMapper
                .apply(organizationRepository.save(organization));
        organizationGetDto.setStatistic(new Statistic());
        return organizationGetDto;
    }

    private void checkOrganization(Organization organization) {
        if (organizationRepository.findAllByOrg_admin(organization.getOrganization_admin().getId()).isPresent()) {
            throw new CustomException("You're already an organization", HttpStatus.BAD_REQUEST);
        }
        if (organizationRepository.existsByName(organization.getName())) {
            throw new CustomException("Organization with same name is already exists", HttpStatus.IM_USED);
        }
    }

    private User getOrganizationAdmin() {
        User admin = userServiceSCRT.getCurrentLoggedInUser();
        if (!admin.getRole().getName().equals("ROLE_ORGANIZATION_ADMIN")) {
            throw new CustomException("Only admin of organizations can create an organization",
                    HttpStatus.FORBIDDEN);
        }
        return admin;
    }


    public OrganizationGetDto getOrganizationByName(String name) {
        return organizationRepository.getOrganizationByName(name)
                .map(organizationMapper)
                .orElseThrow(() -> new CustomException(
                        "Organization with name " + name + "is not found",
                        HttpStatus.NOT_FOUND)
                );
    }

    public List<OrganizationGetDto> getOrganizationByType(VolunteeringType type) {
        List<Organization> organizations = organizationRepository.getAllByVolunteering_type(type.ordinal());
        return Optional.ofNullable(organizations)
                .filter(organizationsDb -> !organizationsDb.isEmpty())
                .map(organizationsDb -> organizationsDb
                        .stream()
                        .map(organizationMapper)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new CustomException(
                        "There is no organizations with type provided",
                        HttpStatus.NO_CONTENT)
                );
    }



    public boolean delete() {
        return organizationRepository
                .findAllByOrg_admin(userServiceSCRT.getCurrentLoggedInUser().getId())
                .map(org -> {
                    organizationRepository.delete(org);
                    return true;
                })
                .orElse(false);
    }

    public OrganizationGetDto getMyOrganization() {
        User organizationAdmin = getOrganizationAdmin();
        return organizationRepository.findAllByOrg_admin(organizationAdmin.getId())
                .map(organizationMapper)
                .orElseThrow(() -> new CustomException(
                        "Organization not found for admin with id " + organizationAdmin.getId(), HttpStatus.NOT_FOUND));
    }

    public OrganizationPageResponse getAllOrganizations(int pageNumber, int sizeOfPage) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage);
        return organizationPageMapper.apply(organizationRepository.findAll(pageable));
    }

    public List<OrganizationGetDto> getOrganizationByVolunteer() {
        return organizationRepository
                .getAllByVolunteer(userServiceSCRT.getCurrentLoggedInUser().getId())
                .stream()
                .map(organizationMapper)
                .collect(Collectors.toList());
    }

}
