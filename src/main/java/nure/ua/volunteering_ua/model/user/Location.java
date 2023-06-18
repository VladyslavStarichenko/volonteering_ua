package nure.ua.volunteering_ua.model.user;
import javax.persistence.*;

import lombok.*;
import nure.ua.volunteering_ua.dto.location.LocationDto;
import nure.ua.volunteering_ua.model.Event;

import java.util.List;

@Entity
@Table(name = "location")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;

    private Double longitude;

    private String address;

    @OneToMany(mappedBy = "location")
    private List<Organization> organization;

    @OneToMany(mappedBy = "location")
    private List<Event> event;

    public Location(LocationDto locationDto) {
        this.latitude = locationDto.getLatitude();
        this.longitude = locationDto.getLongitude();
        this.address = locationDto.getAddress();
    }
}
