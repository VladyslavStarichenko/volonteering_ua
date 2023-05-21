package nure.ua.volunteering_ua.service.request;


import nure.ua.volunteering_ua.dto.request.AidRequestPageResponse;
import nure.ua.volunteering_ua.dto.request.RequestCreateDto;
import nure.ua.volunteering_ua.dto.request.RequestGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.RequestMapper;
import nure.ua.volunteering_ua.mapper.RequestPageMapper;
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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Service
public class RequestService {

    private final Aid_Request_Repository aidRequestRepository;
    private final EmailSenderService emailSenderService;
    private final UserServiceSCRT userServiceSCRT;
    private final CustomerRepository customerRepository;
    private final OrganizationRepository organizationRepository;
    private final RequestPageMapper requestPageMapper;
    private final RequestMapper requestMapper;

    @Autowired
    public RequestService(Aid_Request_Repository aidRequestRepository, EmailSenderService emailSenderService, UserServiceSCRT userServiceSCRT, CustomerRepository customerRepository, OrganizationRepository organizationRepository, RequestPageMapper requestPageMapper, RequestMapper requestMapper) {
        this.aidRequestRepository = aidRequestRepository;
        this.emailSenderService = emailSenderService;
        this.userServiceSCRT = userServiceSCRT;
        this.customerRepository = customerRepository;
        this.organizationRepository = organizationRepository;
        this.requestPageMapper = requestPageMapper;
        this.requestMapper = requestMapper;
    }

    public RequestGetDto createRequest(RequestCreateDto request) {

        Optional<Customer> customerDb = customerRepository
                .findByUser(userServiceSCRT.getCurrentLoggedInUser());
        Optional<Organization> organizationByName = organizationRepository
                .getOrganizationByName(request.getOrganizationName());

        Queue queue = new Queue();
        Aid_Request aid_request;
        if (customerDb.isPresent() && organizationByName.isPresent()) {
            Organization organization = organizationByName.get();
            Customer customer = customerDb.get();
            String deliveryAddress = getDeliveryAddress(request, organization, customer);
            aid_request = new Aid_Request(
                    request.getTitle(),
                    request.getDescription(),
                    request.getAmount(),
                    organization,
                    customer,
                    request.getVolunteering_type(),
                    Request_Status.VERIFICATION,
                    deliveryAddress
            );

            Pair<Integer, List<Aid_Request>> queueNumber = queue.getQueueNumber(
                    customer.getUser().getSocialCategory(),
                    request.getVolunteering_type(),
                    aidRequestRepository.getAllByOrganization_Name(organization.getName())
            );
            aid_request.setQueueNumber(queueNumber.getKey());

            queueNumber.getValue()
                    .forEach(r -> aidRequestRepository.updateQueue(r.getQueueNumber(), r.getId()));
            return requestMapper.apply(aidRequestRepository.save(aid_request));
        }
        throw new CustomException("Error during creating request: " + request.toString(), HttpStatus.BAD_REQUEST);
    }

    private String generateCode() {
        final int idLength = 6;
        return random(idLength, false, true);
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


    public AidRequestPageResponse getAllRequestsByOrganization(int pageNumber, int sizeOfPage, String organizationName) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage);
        Page<Aid_Request> pageResponse = aidRequestRepository.getAid_RequestByOrganization_Name(pageable, organizationName);
        return requestPageMapper.apply(pageResponse);
    }





}
