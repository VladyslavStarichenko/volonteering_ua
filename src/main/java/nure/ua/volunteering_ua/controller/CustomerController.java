package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.customer.CustomerGetDto;
import nure.ua.volunteering_ua.service.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(value = "Operations with organization")
@RequestMapping(value = "/customer/")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @ApiOperation(value = "Subscribe an organization")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("subscribe/{organizationName}")
    public ResponseEntity<String> register(
            @ApiParam(value = "Organization Name")
            @PathVariable String organizationName
    ) {
        customerService.subscribeOrganization(organizationName);
        return new ResponseEntity<>("Now you are subscriber of volunteering organization", HttpStatus.OK);
    }

    @ApiOperation(value = "Unsubscribe an organization")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("unsubscribe/{organizationName}")
    public ResponseEntity<String> unRegister(
            @ApiParam(value = "Organization Name")
            @PathVariable String organizationName
    ) {
        customerService.unSubscribeOrganization(organizationName);
        return new ResponseEntity<>(
                "You're successfully unsubscribed",
                HttpStatus.OK
        );
    }

    @ApiOperation(value = "My account")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("myAccount")
    public ResponseEntity<CustomerGetDto> myAccount() {
        return new ResponseEntity<>(
                customerService.getCurrentLoggedInCustomer(),
                HttpStatus.OK
        );
    }

    @ApiOperation(value = "Get customer by name")
    @GetMapping("getCustomerByName/{customerName}")
    public ResponseEntity<CustomerGetDto> getCustomerByName(
            @ApiParam(value = "Organization Name")
            @PathVariable String customerName
    ) {
        return new ResponseEntity<>(
                customerService.getCustomerByName(customerName),
                HttpStatus.OK
        );
    }

}
