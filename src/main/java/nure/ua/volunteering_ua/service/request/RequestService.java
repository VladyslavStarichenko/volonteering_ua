package nure.ua.volunteering_ua.service.request;


import nure.ua.volunteering_ua.dto.request.AidRequestPageResponse;
import nure.ua.volunteering_ua.dto.request.RequestCreateDto;
import nure.ua.volunteering_ua.dto.request.RequestGetDto;
import nure.ua.volunteering_ua.dto.volunteer.VolunteerGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.RequestMapper;
import nure.ua.volunteering_ua.mapper.RequestPageMapper;
import nure.ua.volunteering_ua.model.Aid_Request;
import nure.ua.volunteering_ua.model.Queue;
import nure.ua.volunteering_ua.model.RequestDeliveryType;
import nure.ua.volunteering_ua.model.Request_Status;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.model.user.Volunteer;
import nure.ua.volunteering_ua.repository.customer.CustomerRepository;
import nure.ua.volunteering_ua.repository.organization.OrganizationRepository;
import nure.ua.volunteering_ua.repository.request.Aid_Request_Repository;
import nure.ua.volunteering_ua.service.email.EmailSenderService;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import nure.ua.volunteering_ua.service.volunteer.VolunteerService;
import org.apache.commons.lang3.tuple.Pair;
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

    private final VolunteerService volunteerService;

    private final Queue queue = new Queue();

    @Autowired
    public RequestService(Aid_Request_Repository aidRequestRepository, EmailSenderService emailSenderService, UserServiceSCRT userServiceSCRT, CustomerRepository customerRepository, OrganizationRepository organizationRepository, RequestPageMapper requestPageMapper, RequestMapper requestMapper, VolunteerService volunteerService) {
        this.aidRequestRepository = aidRequestRepository;
        this.emailSenderService = emailSenderService;
        this.userServiceSCRT = userServiceSCRT;
        this.customerRepository = customerRepository;
        this.organizationRepository = organizationRepository;
        this.requestPageMapper = requestPageMapper;
        this.requestMapper = requestMapper;
        this.volunteerService = volunteerService;
    }

    public RequestGetDto createRequest(RequestCreateDto request) {
        Customer customer = customerRepository.findByUser(userServiceSCRT.getCurrentLoggedInUser())
                .orElseThrow(() -> new CustomException("You're not a customer of the service", HttpStatus.BAD_REQUEST));

        Organization organization = organizationRepository.getOrganizationByName(request.getOrganizationName())
                .orElseThrow(() -> new CustomException("There is no organization with provided name: ", HttpStatus.BAD_REQUEST));

        List<Aid_Request> requests = organization.getRequests()
                .stream().filter(request1 -> request1.getQueueNumber() != -1)
                .collect(Collectors.toList());

        String deliveryAddress = getDeliveryAddress(request, organization, customer);

        Aid_Request aid_request = new Aid_Request(
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
                requests
        );
        aid_request.setQueueNumber(queueNumber.getKey());

        queueNumber.getValue()
                .forEach(r -> aidRequestRepository.updateQueue(r.getQueueNumber(), r.getId()));

        return requestMapper.apply(aidRequestRepository.save(aid_request));
    }

    public RequestGetDto approve(Long id) {
        Aid_Request request = getRequestById2InternalUse(id);
        Volunteer volunteerByName = volunteerService.getVolunteerByNameInternal(userServiceSCRT.getCurrentLoggedInUser().getUserName());
        if (volunteerByName.getVolunteering_area()
                .stream()
                .anyMatch(organization -> organization.getName().equals(request.getOrganization().getName()))) {
            request.setRequestStatus(Request_Status.APPROVED);
            String confirmationCode = generateCode();
            int code = Integer.parseInt(confirmationCode);
            request.setConfirmationCode(Integer.parseInt(confirmationCode));
            String SUBJECT = "It's a mail to confirm your request\n" +
                    "Conformation code of receiving aid ";
            String emailFrom = userServiceSCRT.getCurrentLoggedInUser().getEmail();
            aidRequestRepository.update(code, request.getId());
            emailSenderService.sendMail(request.getCustomer().getUser().getEmail(), SUBJECT, confirmationCode, emailFrom);
            return requestMapper.apply(request);
        } else {
            throw new CustomException("You're not able to change the status of this request", HttpStatus.BAD_REQUEST);
        }
    }

    public RequestGetDto finish(Long requestId, int conformationCode) {
        System.out.println(requestId);
        Aid_Request request = getRequestById2InternalUse(requestId);
        Organization organizationDb = organizationRepository.getOrganizationByName(request.getOrganization().getName())
                .orElseThrow(() -> new CustomException("There is no organization with provided name: ", HttpStatus.BAD_REQUEST));
        if (conformationCode == request.getConfirmationCode()) {
            request.setRequestStatus(Request_Status.DELIVERED);
            List<Aid_Request> requests = organizationDb.getRequests()
                    .stream().filter(request1 -> request1.getQueueNumber() != -1)
                    .collect(Collectors.toList());

            queue.updateQueueNumberAfterComplete(requests,request.getQueueNumber());
            return requestMapper.apply(aidRequestRepository.save(request));
        }
        throw new CustomException("Confirmation code is not valid", HttpStatus.BAD_REQUEST);
    }

    private Aid_Request getRequestById2InternalUse(Long id) {
        return aidRequestRepository.findById(id)
                .orElseThrow(() -> new CustomException("There is no request exists with specified id: " + id, HttpStatus.BAD_REQUEST));
    }

    public RequestGetDto getRequestById(Long id) {
        return requestMapper.apply(aidRequestRepository.findById(id)
                .orElseThrow(() -> new CustomException("There is no request exists with specified id: " + id, HttpStatus.BAD_REQUEST)));
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

    public AidRequestPageResponse getAllRequestsByOrganization(int pageNumber, int sizeOfPage, String sortBy, String organizationName) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        Page<Aid_Request> pageResponse = aidRequestRepository.getAid_RequestByOrganization_Name(pageable, organizationName);
        return requestPageMapper.apply(pageResponse);
    }

    public AidRequestPageResponse getAllRequestsByVolunteer(Long id, int pageNumber, int sizeOfPage, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        Page<Aid_Request> pageResponse = aidRequestRepository.getAid_RequestByVolunteer(pageable, id);
        return requestPageMapper.apply(pageResponse);
    }
    public AidRequestPageResponse getAllRequests(int pageNumber, int sizeOfPage) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage);
        Page<Aid_Request> allRequests = aidRequestRepository.getAll(pageable);
        return requestPageMapper.apply(allRequests);
    }

    public AidRequestPageResponse getAllRequestsByCustomerId(Long customerId, int pageNumber, int sizeOfPage) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage);
        Page<Aid_Request> allRequests = aidRequestRepository.getAid_RequestByCustomer_Id(pageable, customerId);
        return requestPageMapper.apply(allRequests);

    }
}
