package nure.ua.volunteering_ua.mapper.volunteer;

import nure.ua.volunteering_ua.dto.volunteer.VolunteerPageResponse;
import nure.ua.volunteering_ua.model.user.Volunteer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VolunteerPageMapper implements Function<Page<Volunteer>, VolunteerPageResponse> {

    private final VolunteeringMapper volunteeringMapper;

    @Autowired
    public VolunteerPageMapper(VolunteeringMapper volunteeringMapper) {
        this.volunteeringMapper = volunteeringMapper;
    }

    @Override
    public VolunteerPageResponse apply(Page<Volunteer> volunteers) {
        return new VolunteerPageResponse(
                volunteers.getContent()
                        .stream()
                        .map(volunteeringMapper)
                        .collect(Collectors.toList()),
                volunteers.getNumber(),
                volunteers.getContent().size(),
                volunteers.getTotalElements(),
                volunteers.getTotalPages()
        );
    }
}
