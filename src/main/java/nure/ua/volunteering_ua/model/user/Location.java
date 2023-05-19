package nure.ua.volunteering_ua.model.user;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.dto.LocationDto;
import nure.ua.volunteering_ua.model.Event;

@Entity
@Table(name = "location")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;

    private Double longitude;

    private String address;

    @OneToOne(mappedBy = "location")
    private Organization organization;

    @OneToOne(mappedBy = "location")
    private Event event;

    public Location(LocationDto locationDto) {
        this.latitude = locationDto.getLatitude();
        this.longitude = locationDto.getLongitude();
        this.address = locationDto.getAddress();
    }
}
