package nure.ua.volunteering_ua.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nure.ua.volunteering_ua.dto.auth.AuthenticationDto;
import nure.ua.volunteering_ua.exeption.EmptyDataException;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@Api(value = "Admin operations")
@RequestMapping(value = "/admin/")
@CrossOrigin(origins = {"http://localhost:3000", "http://someserver:8000"},
        methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.POST},
        allowCredentials = "true", maxAge = 3600, allowedHeaders = "*")
public class AdminController {

    private final UserServiceSCRT userServiceSCRT;

    @Autowired
    public AdminController(UserServiceSCRT userServiceSCRT) {
        this.userServiceSCRT = userServiceSCRT;
    }

    @PostMapping("registerVolunteeringAdmin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Register volunteering organization admin")
    public ResponseEntity<Map<Object, Object>> signUp(@ApiParam(value = "User object to sign up to the system") @RequestBody AuthenticationDto authenticationDto) {
        return Optional.ofNullable(authenticationDto)
                .map(AuthenticationDto::toUser)
                .map(userServiceSCRT::signUpOrganizationAdmin)
                .map(response -> new ResponseEntity<>(response, HttpStatus.CREATED))
                .orElseThrow(() -> new EmptyDataException("Invalid or empty input"));
    }


    @PutMapping("blockVolunteeringAdmin/{userName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Block volunteering user")
    public ResponseEntity<String> block(@ApiParam(value = "User object to sign up to the system") @PathVariable String userName) {
        userServiceSCRT.blockComplexAdmin(userName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User with name: " + userName + " was successfully blocked");
    }

}
