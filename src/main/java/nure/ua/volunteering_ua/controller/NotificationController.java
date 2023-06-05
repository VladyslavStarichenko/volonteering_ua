package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.notification.NotificationCreateDto;
import nure.ua.volunteering_ua.dto.notification.NotificationDto;
import nure.ua.volunteering_ua.dto.notification.NotificationPageResponse;
import nure.ua.volunteering_ua.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "Operations with notifications")
@RequestMapping(value = "/notifications/")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ApiOperation(value = "Get all notifications by customer")
    @GetMapping("/{pageNumber}/{pageSize}/{customerName}/{sortBy}")
    public ResponseEntity<NotificationPageResponse> getAllCustomerNotifications(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Customer Name") @PathVariable String customerName,
            @ApiParam(value = "SortBy") @PathVariable String sortBy
    ) {
        if (sortBy.isBlank()) {
            sortBy = "createdAt";
        }
        return new ResponseEntity<>(
                notificationService.getAllCustomerNotifications(pageNumber, pageSize,sortBy, customerName),
                HttpStatus.OK
        );
    }

    @ApiOperation(value = "Create notification")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_ORGANIZATION_ADMIN')")
    @PostMapping("/create-multiple/{organizationName}")
    public ResponseEntity<String> createNotificationForSubscribers(
            @ApiParam(value = "Notification object to create", required = true)
            @RequestBody NotificationCreateDto notificationCreateDto,
            @ApiParam(value = "Organization name") @PathVariable String organizationName
    ) {
        notificationService.createNotification(notificationCreateDto, organizationName);
        return new ResponseEntity<>(
                "Notifications was sent to organization " + organizationName + "subscribers",
                HttpStatus.CREATED
        );
    }


    @ApiOperation(value = "Create notification")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_ORGANIZATION_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createNotification(
            @ApiParam(value = "Notification object to create", required = true)
            @RequestBody NotificationDto notificationDto
    ) {
        notificationService.createNotification(notificationDto);
        return new ResponseEntity<>(
                "Notification was sent to the customer " + notificationDto.getCustomerUsername(),
                HttpStatus.CREATED
        );
    }
}
