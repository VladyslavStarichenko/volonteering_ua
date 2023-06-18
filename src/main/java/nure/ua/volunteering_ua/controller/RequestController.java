package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.request.AidReqConfirm;
import nure.ua.volunteering_ua.dto.request.AidRequestPageResponse;
import nure.ua.volunteering_ua.dto.request.RequestCreateDto;
import nure.ua.volunteering_ua.dto.request.RequestGetDto;
import nure.ua.volunteering_ua.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(value = "Operations with organization")
@RequestMapping(value = "/request/")
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @ApiOperation(value = "Create request")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping
    public ResponseEntity<RequestGetDto> createRequest(@ApiParam(value = "Request object to create") @RequestBody RequestCreateDto requestCreateDto) {
        return new ResponseEntity<>(requestService.createRequest(requestCreateDto), HttpStatus.OK);
    }

    @ApiOperation(value = "Approve request")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    @PutMapping("approve/{requestId}")
    public ResponseEntity<RequestGetDto> approve(@PathVariable Long requestId) {
        return new ResponseEntity<>(requestService.approve(requestId), HttpStatus.OK);
    }

    @ApiOperation(value = "Deliver request")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("/delivered")
    public ResponseEntity<RequestGetDto> finish(@ApiParam(name = "Object to confirm") @RequestBody AidReqConfirm reqConfirm) {
        return new ResponseEntity<>(requestService.finish(reqConfirm.getId(), reqConfirm.getCode()), HttpStatus.OK);
    }

    @ApiOperation(value = "Get all requests")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_ADMIN', 'ROLE_ORGANIZATION_ADMIN')")
    @GetMapping("/allRequests/{pageNumber}/{pageSize}")
    public ResponseEntity<AidRequestPageResponse> getAllRequests(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize
    ) {
        return new ResponseEntity<>(requestService.getAllRequests(pageNumber, pageSize), HttpStatus.OK);
    }

    @ApiOperation(value = "Get all all requests by customerId")
    @GetMapping("/allRequests/{customerId}/{pageNumber}/{pageSize}")
    public ResponseEntity<AidRequestPageResponse> getAllRequestsByCustomerId(
            @ApiParam(value = "Customer id") @PathVariable Long customerId,
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize
    ) {
        return new ResponseEntity<>(requestService.getAllRequestsByCustomerId(customerId, pageNumber, pageSize), HttpStatus.OK);
    }

    @ApiOperation(value = "Get all approved requests by organization")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_ORGANIZATION_ADMIN')")
    @GetMapping("/organization/allRequests/{organizationName}/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<AidRequestPageResponse> getAllRequestsOrganization(
            @ApiParam(value = "Organization name") @PathVariable String organizationName,
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by") @PathVariable String sortBy
    ) {
        if (sortBy.isBlank()) {
            sortBy = "volunteering_type";
        }
        return new ResponseEntity<>(requestService.getAllRequestsByOrganization(pageNumber, pageSize, sortBy, organizationName), HttpStatus.OK);
    }

    @ApiOperation(value = "Get all all requests by volunteer")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_ORGANIZATION_ADMIN')")
    @GetMapping("/organization/allRequestsByVolunteer/{volunteerId}/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<AidRequestPageResponse> getAllRequestsByVolunteer(
            @ApiParam(value = "Volunteer id") @PathVariable Long volunteerId,
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by") @PathVariable String sortBy
    ) {
        if (sortBy.isBlank()) {
            sortBy = "queue_number";
        }
        return new ResponseEntity<>(requestService.getAllRequestsByVolunteer(volunteerId, pageNumber, pageSize, sortBy), HttpStatus.OK);
    }

    @ApiOperation(value = "Get the request by id")
    @GetMapping("/{requestId}")
    public ResponseEntity<RequestGetDto> getRequestsById(
            @ApiParam(value = "Request id") @PathVariable Long requestId
    ) {
        return new ResponseEntity<>(requestService.getRequestById(requestId), HttpStatus.OK);
    }


}
