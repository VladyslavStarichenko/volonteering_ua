package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.customer.CustomerGetDto;
import nure.ua.volunteering_ua.dto.event.EventCreateDto;
import nure.ua.volunteering_ua.dto.event.EventGetDto;
import nure.ua.volunteering_ua.dto.event.EventPageResponse;
import nure.ua.volunteering_ua.service.event.EventService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "Operations with events")
@RequestMapping(value = "/events/")
@Slf4j
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @ApiOperation(value = "Get events by organization")
    @GetMapping("/organization-events/{pageNumber}/{pageSize}/{organizationName}")
    public ResponseEntity<EventPageResponse> getCustomerByName(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Organization Name") @PathVariable String organizationName
    ) {
        return new ResponseEntity<>(
                eventService.getAllEventsByOrganization(pageNumber, pageSize, organizationName),
                HttpStatus.OK
        );
    }

    @ApiOperation(value = "Create an event")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<EventGetDto> createEvent(
            @ApiParam(value = "Event object to create the event", required = true)
            @RequestBody EventCreateDto eventCreateDto) {
        return new ResponseEntity<>(eventService.createEvent(eventCreateDto), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update the event")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_ADMIN')")
    @PutMapping("/update/{eventId}")
    public ResponseEntity<EventGetDto> updateEvent(
            @ApiParam(value = "Event id to update") @PathVariable Long eventId,
            @ApiParam(value = "Event object to update the event", required = true)
            @RequestBody EventCreateDto eventCreateDto) {
        return new ResponseEntity<>(eventService.updateEvent(eventId, eventCreateDto), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Participate the event")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @PutMapping("/participate/{customerName}/event/{eventId}")
    public ResponseEntity<Pair<CustomerGetDto, Integer>> participate(
            @ApiParam(value = "Customer name") @PathVariable String customerName,
            @ApiParam(value = "Event id") @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.participate(customerName, eventId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Unparticipate the event")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @PutMapping("/un-participate/{customerName}/event/{eventId}")
    public ResponseEntity<Pair<CustomerGetDto, Integer>> unParticipate(
            @ApiParam(value = "Customer name") @PathVariable String customerName,
            @ApiParam(value = "Event id") @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.unParticipate(customerName, eventId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get all events")
    @GetMapping("/all-events/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<EventPageResponse> getAllEvents(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by") @PathVariable String sortBy

    ) {
        if (sortBy.isBlank()) {
            sortBy = "start_date";
        }
        return new ResponseEntity<>(eventService
                .getAllEvents(pageNumber, pageSize, sortBy), HttpStatus.OK);
    }

    @ApiOperation(value = "Get all participating events by customerId")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_CUSTOMER')")
    @GetMapping("/all-events/customer/{customerId}/{pageNumber}/{pageSize}")
    public ResponseEntity<EventPageResponse> getAllEventsByCustomerId(
            @ApiParam(value = "CustomerId") @PathVariable Long customerId,
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize
    ) {
        return new ResponseEntity<>(eventService
                .getAllEventsByCustomerId(pageNumber, pageSize, customerId), HttpStatus.OK);
    }

    @ApiOperation(value = "Get the event by id")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventGetDto> getEventById(
            @ApiParam(value = "Event id") @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService
                .getEventById(eventId), HttpStatus.OK);
    }

}
