package nure.ua.volunteering_ua.service.organization;

import nure.ua.volunteering_ua.dto.organization.OrganizationCreateDto;
import nure.ua.volunteering_ua.dto.organization.OrganizationGetDto;
import nure.ua.volunteering_ua.dto.organization.OrganizationPageResponse;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.organization.OrganizationMapper;
import nure.ua.volunteering_ua.mapper.organization.OrganizationPageMapper;
import nure.ua.volunteering_ua.model.Statistic;
import nure.ua.volunteering_ua.model.user.*;
import nure.ua.volunteering_ua.repository.location.LocationRepository;
import nure.ua.volunteering_ua.repository.organization.OrganizationRepository;
import nure.ua.volunteering_ua.service.customer.CustomerService;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final LocationRepository locationRepository;

    private final CustomerService customerService;


    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, UserServiceSCRT userServiceSCRT, OrganizationMapper organizationMapper, OrganizationPageMapper organizationPageMapper, LocationRepository locationRepository, CustomerService customerService) {
        this.organizationRepository = organizationRepository;
        this.userServiceSCRT = userServiceSCRT;
        this.organizationMapper = organizationMapper;
        this.organizationPageMapper = organizationPageMapper;
        this.locationRepository = locationRepository;
        this.customerService = customerService;
    }

    public OrganizationGetDto createOrganization(OrganizationCreateDto organizationCreateDto) {
        Location location = new Location(organizationCreateDto.getLocation());
        Location locationDb = locationRepository.save(location);
        User admin = getOrganizationAdmin();
        Organization organization = new Organization(
                organizationCreateDto.getName(),
                organizationCreateDto.getDescription(),
                organizationCreateDto.getVolunteeringType(),
                admin,
                locationDb
        );
        checkOrganization(organization);
        Organization organizationDb = organizationRepository.save(organization);
        OrganizationGetDto organizationGetDto = organizationMapper
                .apply(organizationDb);
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

    public OrganizationPageResponse searchOrganizationByName(String organizationName, int pageNumber, int sizeOfPage, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        Page<Organization> organizations = organizationRepository.searchOrganizationByNameIsIgnoreCase(pageable, organizationName);
        return organizationPageMapper.apply(organizations);

    }

    public Organization getOrganizationByNameInternalUsage(String name) {
        return organizationRepository.getOrganizationByName(name)
                .orElseThrow(() -> new CustomException(
                        "Organization with name " + name + "is not found",
                        HttpStatus.NOT_FOUND)
                );
    }

    public List<OrganizationGetDto> getOrganizationByType(VolunteeringType type) {
        List<Organization> organizations = organizationRepository.getAllByVolunteering_type(type.toString());
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

    public OrganizationPageResponse getAllOrganizations(int pageNumber, int sizeOfPage, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        return organizationPageMapper.apply(organizationRepository.findAll(pageable));
    }

    public List<OrganizationGetDto> getOrganizationByVolunteer() {
        return organizationRepository
                .getAllByVolunteer(userServiceSCRT.getCurrentLoggedInUser().getId())
                .stream()
                .map(organizationMapper)
                .collect(Collectors.toList());
    }

    public List<OrganizationGetDto> getOrganizationByCustomer() {
        return organizationRepository
                .getAllByCustomer(userServiceSCRT.getCurrentLoggedInUser().getId())
                .stream()
                .map(organizationMapper)
                .collect(Collectors.toList());
    }


    public List<OrganizationGetDto> getOrganizationByCustomerName(String customerUserName) {
        Customer customer = customerService.getCustomerByNameInternal(customerUserName);
        return organizationRepository
                .getAllByCustomer(customer.getUser().getId())
                .stream()
                .map(organizationMapper)
                .collect(Collectors.toList());
    }

    public OrganizationGetDto updateOrganization(OrganizationCreateDto organizationCreateDto) {
        Optional<Organization> organization2Update = organizationRepository
                .findAllByOrg_admin(userServiceSCRT.getCurrentLoggedInUser().getId());
        Organization organization = organization2Update.map(org -> {
            Location location = locationRepository.getLocationsByAddress(organizationCreateDto.getLocation().getAddress())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new CustomException("There is no location found", HttpStatus.BAD_REQUEST))
                    .orElseGet(() -> {
                        Location newLocation = new Location(organizationCreateDto.getLocation());
                        locationRepository.save(newLocation);
                        return newLocation;
                    });
            org.setName(organizationCreateDto.getName());
            org.setLocation(location);
            org.setDescription(organizationCreateDto.getDescription());
            org.setImageURL(organizationCreateDto.getImageURL());
            org.setVolunteeringType(organizationCreateDto.getVolunteeringType());
            return organizationRepository.save(org);
        }).orElseThrow(() -> new CustomException("Error during updating the organization", HttpStatus.BAD_REQUEST));
        return organizationMapper.apply(organization);
    }


}
