package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.organization.OrganizationCreateDto;
import nure.ua.volunteering_ua.dto.organization.OrganizationGetDto;
import nure.ua.volunteering_ua.dto.organization.OrganizationPageResponse;
import nure.ua.volunteering_ua.dto.user.UserGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.model.user.VolunteeringType;
import nure.ua.volunteering_ua.service.organization.OrganizationService;
import nure.ua.volunteering_ua.service.statistics.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;

@RestController
@Api(value = "Operations with organization")
@RequestMapping(value = "/organization/")
@Slf4j
public class OrganizationController {
    private final OrganizationService organizationService;
    private final StatisticService statisticService;

    @Autowired
    public OrganizationController(OrganizationService organizationService, StatisticService statisticService) {
        this.organizationService = organizationService;
        this.statisticService = statisticService;
    }

    @ApiOperation(value = "Get my volunteering organization")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping
    public ResponseEntity<OrganizationGetDto> getMyOrganization() {
        return new ResponseEntity<>(organizationService.getMyOrganization(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get volunteering organization by name")
    @GetMapping("name/{organizationName}")
    public ResponseEntity<OrganizationGetDto> getOrganizationByName(@PathVariable String organizationName) {
        return new ResponseEntity<>(organizationService.getOrganizationByName(organizationName), HttpStatus.OK);
    }

    @ApiOperation(value = "Get volunteering organizations by volunteer")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    @GetMapping("/workingOrganizations")
    public ResponseEntity<List<OrganizationGetDto>> getOrganizationsByVolunteer() {
        return Optional.of(organizationService.getOrganizationByVolunteer())
                .filter(not(List::isEmpty))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomException("There is no organizations in your work account", HttpStatus.NO_CONTENT));
    }

    @ApiOperation(value = "Get volunteering organizations by customer (customer Subscriptions)")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/customer-organizations")
    public ResponseEntity<List<OrganizationGetDto>> getOrganizationsByCustomer() {
        return Optional.of(organizationService.getOrganizationByCustomer())
                .filter(not(List::isEmpty))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomException("There is no organizations in your work account", HttpStatus.NO_CONTENT));
    }

    @ApiOperation(value = "Get volunteering organizations by volunteering type")
    @GetMapping("/volunteeringType/{volunteeringType}")
    public ResponseEntity<List<OrganizationGetDto>> getOrganizationsByVolunteeringType(@PathVariable VolunteeringType volunteeringType) {
        return new ResponseEntity<>(organizationService.getOrganizationByType(volunteeringType), HttpStatus.OK);
    }

    @ApiOperation(value = "Get all volunteering organizations")
    @GetMapping("/allOrganizations/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<OrganizationPageResponse> getAllOrganizations(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by parameter")  @PathVariable(required = false) String sortBy

    ) {
        if (sortBy.isBlank()) {
            sortBy = "volunteeringType";
        }
        return new ResponseEntity<>(organizationService
                .getAllOrganizations(pageNumber, pageSize, sortBy), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete my Organizations")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION_ADMIN')")
    @DeleteMapping("/deleteOrganization")
    public ResponseEntity<String> deleteOrganization() {
        return Optional.of(organizationService.delete())
                .filter(result -> result)
                .map(result -> new ResponseEntity<>("Organization Successfully deleted", HttpStatus.OK))
                .orElse(new ResponseEntity<>("You don't own any organizations", HttpStatus.OK));
    }

    @ApiOperation(value = "Create organization")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<OrganizationGetDto> createOrganization(
            @ApiParam(value = "Organization object to create", required = true)
            @RequestBody OrganizationCreateDto organizationCreateDto) {
        OrganizationGetDto organization = organizationService
                .createOrganization(organizationCreateDto);
        return new ResponseEntity<>(organization, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update organization")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION_ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<OrganizationGetDto> updateOrganization(
            @ApiParam(value = "Organization object for updating", required = true)
            @RequestBody OrganizationCreateDto organizationCreateDto) {
        OrganizationGetDto organization = organizationService
                .updateOrganization(organizationCreateDto);
        return new ResponseEntity<>(organization, HttpStatus.CREATED);
    }


    @GetMapping("get-statistic-report/{limit}/{organizationName}/pdf")
    public ResponseEntity<Resource> generateStatisticPdf(
            @ApiParam(value = "Limit of transactions to show", required = true)
            @PathVariable int limit,
            @ApiParam(value = "Organization name", required = true)
            @PathVariable  String organizationName

    )
    {
        byte[] pdfData = statisticService.generatePdf(organizationName, limit); // Pass the desired organization name and transaction limit
        ByteArrayResource resource = new ByteArrayResource(pdfData);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistics.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @ApiOperation(value = "Search organization by name")
    @GetMapping("/organization/search/{organizationName}")
    public ResponseEntity<List<OrganizationGetDto>> getUserById(
            @ApiParam(value = "Organization name") @PathVariable String organizationName) {
        return new ResponseEntity<>(organizationService
                .searchOrganizationByName(organizationName), HttpStatus.OK);
    }
}
