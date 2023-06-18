package nure.ua.volunteering_ua.service.notification;

import nure.ua.volunteering_ua.dto.notification.NotificationCreateDto;
import nure.ua.volunteering_ua.dto.notification.NotificationDto;
import nure.ua.volunteering_ua.dto.notification.NotificationPageResponse;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.notification.NotificationMapper;
import nure.ua.volunteering_ua.mapper.notification.NotificationPageMapper;
import nure.ua.volunteering_ua.model.Notification;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.repository.notification.NotificationRepository;
import nure.ua.volunteering_ua.service.customer.CustomerService;
import nure.ua.volunteering_ua.service.organization.OrganizationService;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerService customerService;
    private final NotificationMapper notificationMapper;
    private final NotificationPageMapper notificationPageMapper;
    private final OrganizationService organizationService;
    private final UserServiceSCRT userServiceSCRT;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, CustomerService customerService, NotificationMapper notificationMapper, NotificationPageMapper notificationPageMapper, OrganizationService organizationService, UserServiceSCRT userServiceSCRT) {
        this.notificationRepository = notificationRepository;
        this.customerService = customerService;
        this.notificationMapper = notificationMapper;
        this.notificationPageMapper = notificationPageMapper;
        this.organizationService = organizationService;
        this.userServiceSCRT = userServiceSCRT;
    }

    public List<Notification> getAllCustomerNotificationsInternal() {
        return Optional.of(customerService.getCurrentLoggedInCustomerInternal())
                .map(notificationRepository::getNotificationByCustomer)
                .orElse(new ArrayList<>());
    }

    public void createEventNotification(List<Customer> customers, String message, String topic) {
        customers
                .forEach(customer -> {
                    Notification notification = new Notification(customer, message, topic);
                    notificationRepository.save(notification);
                });
    }

    public List<NotificationDto> getAllCustomerNotifications() {
        List<Notification> allCustomerNotifications = getAllCustomerNotificationsInternal();
        if (allCustomerNotifications.isEmpty()) {
            return new ArrayList<>();
        } else {
            return allCustomerNotifications.stream()
                    .map(notificationMapper)
                    .collect(Collectors.toList());
        }
    }

    public NotificationPageResponse getAllCustomerNotifications(int pageNumber, int sizeOfPage, String sortBy, String customerName) {
        Customer customer = customerService.getCustomerByNameInternal(customerName);
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        return notificationPageMapper.apply(notificationRepository.findAllByCustomer(pageable, customer));
    }

    public void createNotification(NotificationCreateDto notificationCreateDto, String organizationName) {
        Organization organization = userServiceSCRT.getCurrentLoggedInUser().getOrganization();
        if(organization != null){
            if(organization.getName().equals(organizationName)){
                List<Customer> customers = organizationService.getOrganizationByNameInternalUsage(organizationName).getSubscribers();
                customers
                        .forEach(customer -> {
                            Notification notification = new Notification(customer, notificationCreateDto.getMessage(), notificationCreateDto.getTitle());
                            notificationRepository.save(notification);
                        });
            }
            else {
                throw new CustomException("You're not an admin of this organization", HttpStatus.BAD_REQUEST);
            }
        }

    }

    public void createNotification(NotificationDto notificationDto) {
        Customer customer = customerService.getCustomerByNameInternal(notificationDto.getCustomerUsername());
        Notification notification = new Notification(customer, notificationDto.getMessage(), notificationDto.getTitle());
        notificationRepository.save(notification);
    }
}
