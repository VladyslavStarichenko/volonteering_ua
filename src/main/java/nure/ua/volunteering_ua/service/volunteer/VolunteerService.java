package nure.ua.volunteering_ua.service.volunteer;

import nure.ua.volunteering_ua.dto.volunteer.VolunteerGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.VolunteeringMapper;
import nure.ua.volunteering_ua.model.user.Volunteer;
import nure.ua.volunteering_ua.repository.volunteer.VolunteerRepository;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final VolunteeringMapper volunteeringMapper;
    private final UserServiceSCRT userServiceSCRT;

    @Autowired
    public VolunteerService(VolunteerRepository volunteerRepository, VolunteeringMapper volunteeringMapper, UserServiceSCRT userServiceSCRT) {
        this.volunteerRepository = volunteerRepository;
        this.volunteeringMapper = volunteeringMapper;
        this.userServiceSCRT = userServiceSCRT;
    }

    public void registerInOrganization(String volunteerName) {
        Optional.ofNullable(userServiceSCRT.getCurrentLoggedInUser().getOrganization())
                .ifPresent(organizationValue -> {
            Optional<Volunteer> volunteerDb = volunteerRepository.findByUser_UserName(volunteerName);
            volunteerDb.ifPresent(volunteer -> {
                if (!volunteer.getVolunteering_area().contains(organizationValue)) {
                    volunteer.getVolunteering_area().add(organizationValue);
                    volunteerRepository.save(volunteer);
                }
            });
            if (!volunteerDb.isPresent()) {
                throw new CustomException("Volunteer not found", HttpStatus.NOT_FOUND);
            }
        });
    }

    public void unRegisterInOrganization(String volunteerName) {
        Optional.ofNullable(userServiceSCRT.getCurrentLoggedInUser().getOrganization())
                .ifPresent(organizationValue -> {
                    Optional<Volunteer> volunteerDb = volunteerRepository.findByUser_UserName(volunteerName);
                    volunteerDb.ifPresent(volunteer -> {
                        if (volunteer.getVolunteering_area().contains(organizationValue)) {
                            volunteer.getVolunteering_area().remove(organizationValue);
                            volunteerRepository.save(volunteer);
                        }
                    });
                    if (!volunteerDb.isPresent()) {
                        throw new CustomException("Volunteer not found", HttpStatus.NOT_FOUND);
                    }
                });
    }


    public VolunteerGetDto getMyAccount() {
        return volunteerRepository.findByUser_UserName(userServiceSCRT.getCurrentLoggedInUser().getUserName())
                .map(volunteeringMapper)
                .orElseThrow(() -> new CustomException("There is no volunteer account associated with the current user", HttpStatus.NOT_FOUND));
    }

}
