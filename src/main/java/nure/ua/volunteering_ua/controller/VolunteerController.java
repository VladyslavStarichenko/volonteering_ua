package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.volunteer.VolunteerGetDto;
import nure.ua.volunteering_ua.service.volunteer.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "Operations with volunteer")
@RequestMapping(value = "/volunteer/")
@Slf4j
public class VolunteerController {

    private final VolunteerService volunteerService;

    @Autowired
    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }


    @ApiOperation(value = "Register the volunteer in the organization")
    @PutMapping("register/{volunteerName}")
    public ResponseEntity<String> register(
            @ApiParam(value = "Username of the volunteer") @PathVariable String volunteerName
    ) {
        volunteerService.registerInOrganization(volunteerName);
        return new ResponseEntity<>("Now volunteer " + volunteerName + " is a member of volunteering organization", HttpStatus.OK);
    }

    @ApiOperation(value = "Unregister the volunteer from the organization")
    @PutMapping("unregister/{volunteerName}")
    public ResponseEntity<String> unregister(
            @ApiParam(value = "Username of the volunteer") @PathVariable String volunteerName
    ) {
        volunteerService.unRegisterInOrganization(volunteerName);
        return new ResponseEntity<>("Now volunteer " + volunteerName + " is not a member of volunteering organization", HttpStatus.OK);
    }


    @ApiOperation(value = "My account")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    @GetMapping("/myAccount/")
    public ResponseEntity<VolunteerGetDto> myAccount() {
        return new ResponseEntity<>(volunteerService.getMyAccount(), HttpStatus.OK);
    }


    @ApiOperation(value = "Get volunteer by name")
    @GetMapping("/{volunteerName}")
    public ResponseEntity<VolunteerGetDto> getVolunteerByName(
            @ApiParam(value = "Volunteer name") @PathVariable String volunteerName
    ) {
        return new ResponseEntity<>(volunteerService.getVolunteerByName(volunteerName), HttpStatus.OK);
    }
}
