package nure.ua.volunteering_ua.service.event;

import nure.ua.volunteering_ua.dto.event.EventPageResponse;
import nure.ua.volunteering_ua.dto.event.EventParticipateResponse;
import nure.ua.volunteering_ua.dto.location.LocationDto;
import nure.ua.volunteering_ua.dto.event.EventCreateDto;
import nure.ua.volunteering_ua.dto.event.EventGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.CustomerMapper;
import nure.ua.volunteering_ua.mapper.EventMapper;
import nure.ua.volunteering_ua.mapper.EventPageMapper;
import nure.ua.volunteering_ua.model.Event;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.Location;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.repository.event.EventRepository;
import nure.ua.volunteering_ua.repository.location.LocationRepository;
import nure.ua.volunteering_ua.service.customer.CustomerService;
import nure.ua.volunteering_ua.service.notification.NotificationService;
import nure.ua.volunteering_ua.service.organization.OrganizationService;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final OrganizationService organizationService;
    private final NotificationService notificationService;
    private final CustomerService customerService;
    private final EventMapper eventMapper;

    private final EventPageMapper eventPageMapper;

    private final UserServiceSCRT userServiceSCRT;
    private final CustomerMapper customerMapper;

    @Autowired
    public EventService(EventRepository eventRepository, LocationRepository locationRepository, OrganizationService organizationService, NotificationService notificationService, CustomerService customerService, EventMapper eventMapper, EventPageMapper eventPageMapper, UserServiceSCRT userServiceSCRT, CustomerMapper customerMapper) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.organizationService = organizationService;
        this.notificationService = notificationService;
        this.customerService = customerService;
        this.eventMapper = eventMapper;
        this.eventPageMapper = eventPageMapper;
        this.userServiceSCRT = userServiceSCRT;
        this.customerMapper = customerMapper;
    }

    public EventGetDto createEvent(EventCreateDto eventCreateDto) {
        Event event = new Event(eventCreateDto);
        String message = "A new event " + event.getName() + " comes.\n" +
                "Description: " + event.getDescription() +
                "\n Dates: " + event.getStartDate() + " - " + event.getEndDate() +
                "\n Capacity: " + event.getCapacity();

        String topic = "New event comes.";
        Organization eventOrganization = getEventOrganization(userServiceSCRT.getCurrentLoggedInUser().getOrganization().getName());
        event.setLocation(checkLocation(eventCreateDto.getLocation(),eventOrganization.getLocation().getId()));
        event.setOrganization(eventOrganization);
        notificationService.createEventNotification(eventOrganization.getSubscribers(), message, topic);
        return eventMapper.apply(eventRepository.save(event));
    }

    public EventGetDto updateEvent(Long eventID, EventCreateDto eventCreateDto) {

        Organization organization = userServiceSCRT.getCurrentLoggedInUser().getOrganization();
        Long idOfLocationOfOrganization = organization.getLocation().getId();
        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new CustomException("There is no event with specified Id", HttpStatus.NOT_FOUND));
        event.setLocation(checkLocation(eventCreateDto.getLocation(),idOfLocationOfOrganization ));
        event.setOrganization(getEventOrganization(userServiceSCRT.getCurrentLoggedInUser().getOrganization().getName()));
        event.setDescription(eventCreateDto.getDescription());
        event.setStartDate(eventCreateDto.getStartDate());
        event.setEndDate(eventCreateDto.getEndDate());
        event.setName(eventCreateDto.getName());
        String message = "An event " + event.getName() + " has changed.\n" +
                "Description: " + event.getDescription() +
                "\n Dates: " + event.getStartDate() + " - " + event.getEndDate() +
                "\n Capacity: " + event.getCapacity();
        String topic = "Event data changed.";
        notificationService.createEventNotification(event.getOrganization().getSubscribers(), message, topic);
        return eventMapper.apply(eventRepository.save(event));
    }



    public Location checkLocation(LocationDto locationDto, Long idOfLocationOfOrganization) {
        Optional<Location> locationsByAddress = locationRepository.getLocationsByAddress(locationDto.getAddress())
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    return Optional.of(new Location(locationDto));
                } );

        return locationsByAddress.filter(loc -> loc.getId() == idOfLocationOfOrganization)
                .orElseGet(() -> {
                    Location newLocation = new Location(locationDto);
                    return locationRepository.save(newLocation);
                });
    }



    public Organization getEventOrganization(String organizationName) {
        return organizationService.getOrganizationByNameInternalUsage(organizationName);
    }

    public EventParticipateResponse participate(String customerName, Long eventId) {
        Customer customer = customerService.getCustomerByNameInternal(customerName);
        Event event = getEventByIdInternal(eventId);
        if (participationCheck(customer, event) || event.getCapacity() < 0) {
            throw new CustomException("Provided customer is already participating.", HttpStatus.BAD_REQUEST);
        } else {
            if(event.getCapacity()==0){
                throw new CustomException("There is no place left", HttpStatus.BAD_REQUEST);
            }
            else {
                event.addParticipant(customer);
                event.setCapacity(event.getCapacity() - 1);
                eventRepository.participate(customer.getId(), event.getId());
                eventRepository.save(event);
                return new EventParticipateResponse( customerMapper.apply(customer), event.getCapacity());
            }
        }
    }

    public EventParticipateResponse unParticipate(String customerName, Long eventId) {
        Customer customer = customerService.getCustomerByNameInternal(customerName);
        Event event = getEventByIdInternal(eventId);
        if (!participationCheck(customer, event)) {
            throw new CustomException("Provided customer is not a participant.", HttpStatus.BAD_REQUEST);
        } else {
            event.removeParticipant(customer);
            event.setCapacity(event.getCapacity() + 1);
            eventRepository.unparticipate(customer.getId(),event.getId());
            eventMapper.apply(eventRepository.save(event));
            return new EventParticipateResponse(customerMapper.apply(customer), event.getCapacity());
        }
    }

    private boolean participationCheck(Customer customer, Event event) {

        return event.getCustomers()
                .stream()
                .anyMatch(c -> c.getUser().getId().equals(customer.getUser().getId()));
    }

    public Event getEventByIdInternal(Long eventId) {
        return eventRepository.findEventById(eventId)
                .orElseThrow(() -> new CustomException("There is no event with provided ID", HttpStatus.NOT_FOUND));
    }

    public List<Event> getAllEventsInternal() {
        return eventRepository.getAllEvents();
    }

    public EventPageResponse getAllEvents(int pageNumber, int sizeOfPage, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        return eventPageMapper.apply(eventRepository.getAllEventsPage(pageable));
    }

    public EventPageResponse getAllEventsByOrganization(int pageNumber, int sizeOfPage, String organizationName) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage);
        return eventPageMapper.apply(eventRepository.getAllByOrganization_Name(pageable, organizationName));
    }

    public EventPageResponse getAllEventsByCustomerId(int pageNumber, int sizeOfPage, Long customerId) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage);
        return eventPageMapper.apply(eventRepository.getAllByCustomerId(pageable, customerId));
    }

    public EventGetDto getEventById(Long id){
        return eventMapper.apply(getEventByIdInternal(id));
    }


}
