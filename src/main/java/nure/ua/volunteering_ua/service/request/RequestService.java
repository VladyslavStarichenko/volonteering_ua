package nure.ua.volunteering_ua.service.request;


import nure.ua.volunteering_ua.dto.request.RequestCreateDto;
import nure.ua.volunteering_ua.dto.request.RequestGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.model.Aid_Request;
import nure.ua.volunteering_ua.model.Queue;
import nure.ua.volunteering_ua.model.RequestDeliveryType;
import nure.ua.volunteering_ua.model.Request_Status;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.repository.customer.CustomerRepository;
import nure.ua.volunteering_ua.repository.organization.OrganizationRepository;
import nure.ua.volunteering_ua.repository.request.Aid_Request_Repository;
import nure.ua.volunteering_ua.service.email.EmailSenderService;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestService {

    private final Aid_Request_Repository aidRequestRepository;
    private final EmailSenderService emailSenderService;
    private final UserServiceSCRT userServiceSCRT;
    private final CustomerRepository customerRepository;
    private final OrganizationRepository organizationRepository;

    @Autowired
    public RequestService(Aid_Request_Repository aidRequestRepository, EmailSenderService emailSenderService, UserServiceSCRT userServiceSCRT, CustomerRepository customerRepository, OrganizationRepository organizationRepository) {
        this.aidRequestRepository = aidRequestRepository;
        this.emailSenderService = emailSenderService;
        this.userServiceSCRT = userServiceSCRT;
        this.customerRepository = customerRepository;
        this.organizationRepository = organizationRepository;
    }

    public RequestGetDto createRequest(RequestCreateDto request) {

        Optional<Customer> customerDb = customerRepository
                .findByUser(userServiceSCRT.getCurrentLoggedInUser());
        Optional<Organization> organizationByName = organizationRepository
                .getOrganizationByName(request.getOrganizationName());


        if (customerDb.isPresent() && organizationByName.isPresent()) {
            Organization organization = organizationByName.get();
            Customer customer = customerDb.get();
            String deliveryAddress = getDeliveryAddress(request, organization, customer);
            Aid_Request aid_request = new Aid_Request();
            aid_request.setRequestStatus(Request_Status.VERIFICATION);
            aid_request.setAmount(request.getAmount());
            aid_request.setCustomer(customer);
            aid_request.setTitle(request.getTitle());
            aid_request.setDescription(request.getDescription());
            aid_request.setOrganization(organization);
            aid_request.setVolunteeringType(request.getVolunteering_type());
            aid_request.setReceivingAddress(deliveryAddress);
            aid_request.setQueueNumber(0);
        }
        return null;
    }

    private String getDeliveryAddress(RequestCreateDto request, Organization organization, Customer customer) {
        return Optional.ofNullable(request.getDeliveryType())
                .filter(deliveryType -> deliveryType == RequestDeliveryType.HOME_DELIVERY)
                .map(deliveryType -> customer.getAddress())
                .orElseGet(() -> Optional.ofNullable(request.getDeliveryType())
                        .filter(deliveryType -> deliveryType == RequestDeliveryType.SELF_SERVICE_DELIVERY)
                        .map(deliveryType -> organization.getLocation().getAddress())
                        .orElseThrow(() -> new CustomException("Error address provided", HttpStatus.BAD_REQUEST)));
    }





}
