package nure.ua.volunteering_ua.service.volunteer;

import nure.ua.volunteering_ua.dto.volunteer.VolunteerGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.VolunteeringMapper;
import nure.ua.volunteering_ua.model.user.User;
import nure.ua.volunteering_ua.model.user.Volunteer;
import nure.ua.volunteering_ua.repository.user.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public VolunteerService(VolunteerRepository volunteerRepository, VolunteeringMapper volunteeringMapper, UserServiceSCRT userServiceSCRT, UserRepository userRepository) {
        this.volunteerRepository = volunteerRepository;
        this.volunteeringMapper = volunteeringMapper;
        this.userServiceSCRT = userServiceSCRT;
        this.userRepository = userRepository;
    }

    public void registerInOrganization(String volunteerName) {
        System.out.println(volunteerName);
        Optional.ofNullable(userServiceSCRT.getCurrentLoggedInUser().getOrganization())
                .ifPresent(organizationValue -> {
            Optional<Volunteer> volunteerDb = volunteerRepository.getByUser_UserName(volunteerName);
            volunteerDb.ifPresent(volunteer -> {
                if (!volunteer.getVolunteering_area().contains(organizationValue)) {
                    volunteer.getVolunteering_area().add(organizationValue);
                    volunteerRepository.save(volunteer);
                }
            });
            if (volunteerDb.isEmpty()) {
                throw new CustomException("Volunteer not found", HttpStatus.NOT_FOUND);
            }
        });
    }

    public void unRegisterInOrganization(String volunteerName) {
        Optional.ofNullable(userServiceSCRT.getCurrentLoggedInUser().getOrganization())
                .ifPresent(organizationValue -> {
                    Optional<Volunteer> volunteerDb = volunteerRepository.getByUserName(volunteerName);
                    volunteerDb.ifPresent(volunteer -> {
                        if (volunteer.getVolunteering_area().contains(organizationValue)) {
                            volunteer.getVolunteering_area().remove(organizationValue);
                            volunteerRepository.save(volunteer);
                        }
                    });
                    if (volunteerDb.isEmpty()) {
                        throw new CustomException("Volunteer not found", HttpStatus.NOT_FOUND);
                    }
                });
    }


    public VolunteerGetDto getMyAccount() {
        return volunteerRepository.getByUserName(userServiceSCRT.getCurrentLoggedInUser().getUserName())
                .map(volunteeringMapper)
                .orElseThrow(() -> new CustomException("There is no volunteer account associated with the current user", HttpStatus.NOT_FOUND));
    }

    public Volunteer getVolunteerByNameInternal(String userName){
        return volunteerRepository.getByUserName(userName)
                .orElseThrow(() -> new CustomException("There is no volunteer account associated with the current user", HttpStatus.NOT_FOUND));
    }

    public VolunteerGetDto getVolunteerByName(String userName){
        System.out.println(userName);
        return volunteerRepository.getByUser_UserName(userName)
                .map(volunteeringMapper)
                .orElseThrow(() -> new CustomException("There is no volunteer account associated with the current user", HttpStatus.NOT_FOUND));
    }

    public VolunteerGetDto getVolunteerById(Long requestId) {
        return volunteeringMapper.apply(volunteerRepository.findById(requestId).orElseThrow(()-> new CustomException("There is no volunteer account associated with the current user", HttpStatus.NOT_FOUND)));
    }
}
