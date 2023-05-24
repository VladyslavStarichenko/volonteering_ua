package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.volunteer.VolunteerGetDto;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.model.user.Volunteer;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class VolunteeringMapper implements Function<Volunteer, VolunteerGetDto> {
    @Override
    public VolunteerGetDto apply(Volunteer volunteer) {
        return new VolunteerGetDto(
                volunteer.getId(),
                volunteer.getUser().getUserName(),
                volunteer.getUser().getEmail(),
                volunteer.getVolunteering_area().stream()
                        .map(Organization::getName)
                        .collect(Collectors.toList()));
    }
}

