package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nure.ua.volunteering_ua.dto.AuthorizationDto;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@Api(value = "Authentication operations (login, sign up)")
@RequestMapping(value = "/auth/")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8000"},
        methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.POST},
        allowCredentials = "true", maxAge = 3600, allowedHeaders = "*")
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




}
