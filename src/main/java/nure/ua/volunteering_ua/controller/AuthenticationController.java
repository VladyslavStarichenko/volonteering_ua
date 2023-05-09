package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nure.ua.volunteering_ua.dto.auth.AuthenticationDto;
import nure.ua.volunteering_ua.dto.auth.AuthorizationDto;
import nure.ua.volunteering_ua.dto.auth.CustomerAuthenticationDto;
import nure.ua.volunteering_ua.dto.customer.CustomerGetDto;
import nure.ua.volunteering_ua.dto.volunteer.VolunteerGetDto;
import nure.ua.volunteering_ua.exeption.EmptyDataException;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@Api(value = "Authentication operations (login, sign up)")
@RequestMapping(value = "/auth/")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8000"},
//        methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.POST},
//        allowCredentials = "true", maxAge = 3600, allowedHeaders = "*")
public class AuthenticationController {

    private final UserServiceSCRT userServiceSCRT;

    @Autowired
    public AuthenticationController(UserServiceSCRT userServiceSCRT) {
        this.userServiceSCRT = userServiceSCRT;
    }

    @PostMapping("login")
    @ApiOperation(value = "Login to the system")
    public ResponseEntity<Map<Object, Object>> login(@ApiParam(value = "Registered User object") @RequestBody AuthorizationDto requestDto) {
        return Optional.ofNullable(requestDto)
                .map(userServiceSCRT::signIn)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid or empty input"));
    }

    @PostMapping("registerVolunteer")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION_ADMIN')")
    @ApiOperation(value = "Register Volunteer")
    public ResponseEntity<VolunteerGetDto> registerVolunteer(@ApiParam(value = "User object to sign up to the system") @RequestBody AuthenticationDto user) {
        return Optional.ofNullable(user)
                .map(AuthenticationDto::toUser)
                .map(userServiceSCRT::signUpVolunteer)
                .map(volunteerGetDto -> new ResponseEntity<>(volunteerGetDto, HttpStatus.CREATED))
                .orElseThrow(() -> new EmptyDataException("Invalid or empty input"));
    }

    @PostMapping("registerCustomer")
    @ApiOperation(value = "Register Customer")
    public ResponseEntity<CustomerGetDto> registerCustomer(@ApiParam(value = "User object to sign up to the system") @RequestBody CustomerAuthenticationDto user) {
        return Optional.ofNullable(user)
                .map(AuthenticationDto::toUser)
                .map(userToAdd -> userServiceSCRT.signUpCustomer(userToAdd, user.getAddress(),user.getSocialCategory()))
                .map(customerGetDto -> new ResponseEntity<>(customerGetDto, HttpStatus.CREATED))
                .orElseThrow(() -> new EmptyDataException("Invalid or empty input"));
    }




}
