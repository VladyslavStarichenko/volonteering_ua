package nure.ua.volunteering_ua.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nure.ua.volunteering_ua.dto.auth.AuthenticationDto;
import nure.ua.volunteering_ua.dto.product.ProductPageResponse;
import nure.ua.volunteering_ua.dto.user.UserPageResponse;
import nure.ua.volunteering_ua.exeption.EmptyDataException;
import nure.ua.volunteering_ua.model.user.User;
import nure.ua.volunteering_ua.service.BackupService;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;


import java.util.Map;
import java.util.Optional;

@RestController
@Api(value = "Admin operations")
@RequestMapping(value = "/admin/")
public class AdminController {

    private final UserServiceSCRT userServiceSCRT;
    private final BackupService backupService;

    @Autowired
    public AdminController(UserServiceSCRT userServiceSCRT, BackupService backupService) {
        this.userServiceSCRT = userServiceSCRT;
        this.backupService = backupService;
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


    @ApiOperation(value = "Get all system users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all-users/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<UserPageResponse> getAllProducts(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by parameter")  @PathVariable(required = false) String sortBy

    ) {
        if (sortBy.isBlank()) {
            sortBy = "userName";
        }
        return new ResponseEntity<>(userServiceSCRT
                .getAllUsers(pageNumber, pageSize, sortBy), HttpStatus.OK);
    }




}
