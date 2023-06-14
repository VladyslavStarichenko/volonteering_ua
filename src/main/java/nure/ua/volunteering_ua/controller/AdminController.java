package nure.ua.volunteering_ua.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nure.ua.volunteering_ua.dto.auth.AuthenticationDto;
import nure.ua.volunteering_ua.dto.user.UserGetDto;
import nure.ua.volunteering_ua.dto.user.UserPageResponse;
import nure.ua.volunteering_ua.exeption.EmptyDataException;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@Api(value = "Admin operations")
@RequestMapping(value = "/admin/")
public class AdminController {

    private final UserServiceSCRT userServiceSCRT;


    @Autowired
    public AdminController(UserServiceSCRT userServiceSCRT) {
        this.userServiceSCRT = userServiceSCRT;
    }

    @PostMapping("registerVolunteeringAdmin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Register volunteering organization admin")
    public ResponseEntity<UserGetDto> signUp(@ApiParam(value = "User name to register volunteering organization admin") @RequestBody AuthenticationDto authenticationDto) {
        return Optional.ofNullable(authenticationDto)
                .map(AuthenticationDto::toUser)
                .map(userServiceSCRT::signUpOrganizationAdmin)
                .map(response -> new ResponseEntity<>(response, HttpStatus.CREATED))
                .orElseThrow(() -> new EmptyDataException("Invalid or empty input"));
    }


    @PutMapping("unblockUser/{userName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Unblock user")
    public ResponseEntity<String> block(@ApiParam(value = "User name to unblock") @PathVariable String userName) {
        userServiceSCRT.unBlockUser(userName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User with name: " + userName + " was successfully unblocked");
    }

    @PutMapping("blockUser/{userName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "block user")
    public ResponseEntity<String> unBlock(@ApiParam(value = "User name to block") @PathVariable String userName) {
        userServiceSCRT.blockUser(userName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User with name: " + userName + " was successfully blocked");
    }


    @ApiOperation(value = "Get all system users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all-users/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<UserPageResponse> getAllProducts(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by parameter") @PathVariable(required = false) String sortBy

    ) {
        if (sortBy.isBlank()) {
            sortBy = "userName";
        }
        return new ResponseEntity<>(userServiceSCRT
                .getAllUsers(pageNumber, pageSize, sortBy), HttpStatus.OK);
    }

    @ApiOperation(value = "Get the user by id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserGetDto> getUserById(
            @ApiParam(value = "User id") @PathVariable UUID userId) {
        return new ResponseEntity<>(userServiceSCRT
                .getUserById(userId), HttpStatus.OK);
    }

    @ApiOperation(value = "Search user by name")
    @GetMapping("/user/search/{userName}/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<UserPageResponse> getUserById(
            @ApiParam(value = "User name") @PathVariable String userName,
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by parameter") @PathVariable(required = false) String sortBy
    ) {
        if (sortBy.isBlank()) {
            sortBy = "user_name";
        }
        return new ResponseEntity<>(userServiceSCRT
                .searchUserByName(userName, pageNumber, pageSize, sortBy), HttpStatus.OK);
    }


}
