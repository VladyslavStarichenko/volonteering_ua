package nure.ua.volunteering_ua.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.model.user.Location;
import nure.ua.volunteering_ua.model.user.VolunteeringType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationCreateDto {

    private String name;
    private VolunteeringType volunteeringType;
    private Location location;
}
