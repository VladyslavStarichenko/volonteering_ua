package nure.ua.volunteering_ua.service.notification;

import nure.ua.volunteering_ua.dto.notification.NotificationCreateDto;
import nure.ua.volunteering_ua.dto.notification.NotificationCreateDto2;
import nure.ua.volunteering_ua.dto.notification.NotificationDto;
import nure.ua.volunteering_ua.dto.notification.NotificationPageResponse;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.notification.NotificationMapper;
import nure.ua.volunteering_ua.mapper.notification.NotificationPageMapper;
import nure.ua.volunteering_ua.model.Notification;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.model.user.User;
import nure.ua.volunteering_ua.repository.notification.NotificationRepository;
import nure.ua.volunteering_ua.service.customer.CustomerService;
import nure.ua.volunteering_ua.service.organization.OrganizationService;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import nure.ua.volunteering_ua.service.volunteer.VolunteerService;
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
    private final VolunteerService volunteerService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, CustomerService customerService, NotificationMapper notificationMapper, NotificationPageMapper notificationPageMapper, OrganizationService organizationService, UserServiceSCRT userServiceSCRT, VolunteerService volunteerService) {
        this.notificationRepository = notificationRepository;
        this.customerService = customerService;
        this.notificationMapper = notificationMapper;
        this.notificationPageMapper = notificationPageMapper;
        this.organizationService = organizationService;
        this.userServiceSCRT = userServiceSCRT;
        this.volunteerService = volunteerService;
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
        User currentLoggedInUser = userServiceSCRT.getCurrentLoggedInUser();
        String role = currentLoggedInUser.getRole().getName();
        Organization organization = currentLoggedInUser.getOrganization();
        if(role.equals("ROLE_ORGANIZATION_ADMIN")){
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
        else if(role.equals("ROLE_VOLUNTEER")){
            volunteerService.getAllVolunteersInternalUsage(organizationName)
                    .stream()
                    .filter(volunteer -> volunteer.getName().equals(currentLoggedInUser.getUserName()))
                    .findAny()
                    .orElseGet(() -> {
                        List<Customer> customers = organizationService.getOrganizationByNameInternalUsage(organizationName).getSubscribers();
                        customers.forEach(customer -> {
                            Notification notification = new Notification(customer, notificationCreateDto.getMessage(), notificationCreateDto.getTitle());
                            notificationRepository.save(notification);
                        });
                        throw new CustomException("You're not volunteering in that organization", HttpStatus.FORBIDDEN);
                    });
        }
        else {
            throw new CustomException("You're not allowed to create notification",HttpStatus.FORBIDDEN);
        }


    }

    public void createNotification(NotificationCreateDto2 notificationDto) {
        Customer customer = customerService.getCustomerByNameInternal(notificationDto.getCustomerUsername());
        List<Organization> subscriptions = customer.getSubscriptions();
        User currentLoggedInUser = userServiceSCRT.getCurrentLoggedInUser();
        Organization organization = currentLoggedInUser.getOrganization();
        String role = currentLoggedInUser.getRole().getName();

        subscriptions.stream()
                .filter(org -> org.getName().equals(notificationDto.getOrganizationName()))
                .findAny()
                .ifPresentOrElse(
                        org -> {
                            if (role.equals("ROLE_ORGANIZATION_ADMIN")) {
                                if (organization != null) {
                                    if (organization.getName().equals(notificationDto.getOrganizationName())) {
                                        Notification notification = new Notification(customer, notificationDto.getMessage(), notificationDto.getTitle());
                                        notificationRepository.save(notification);
                                    } else {
                                        throw new CustomException("You're not an admin of this organization", HttpStatus.BAD_REQUEST);
                                    }
                                }
                            } else if (role.equals("ROLE_VOLUNTEER")) {
                                volunteerService.getAllVolunteersInternalUsage(notificationDto.getOrganizationName())
                                        .stream()
                                        .filter(volunteer -> volunteer.getName().equals(currentLoggedInUser.getUserName()))
                                        .findAny()
                                        .orElseGet(() -> {
                                            Notification notification = new Notification(customer, notificationDto.getMessage(), notificationDto.getTitle());
                                            notificationRepository.save(notification);
                                            throw new CustomException("You're not volunteering in that organization", HttpStatus.FORBIDDEN);
                                        });
                            } else {
                                throw new CustomException("You're not allowed to create notification", HttpStatus.FORBIDDEN);
                            }
                        },
                        () -> {
                            throw new CustomException("This user is a subscriber of organization", HttpStatus.BAD_REQUEST);
                        }
                );




    }
}
