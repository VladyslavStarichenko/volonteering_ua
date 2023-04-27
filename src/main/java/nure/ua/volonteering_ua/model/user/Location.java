package nure.ua.volonteering_ua.model.user;
import javax.persistence.*;
import lombok.Data;
import nure.ua.volonteering_ua.model.Event;

@Entity
@Table(name = "location")
@Data
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

    @OneToOne(mappedBy = "location")
    private Customer customer;


}
