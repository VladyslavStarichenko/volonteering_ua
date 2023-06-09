package nure.ua.volunteering_ua.model.user;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nure.ua.volunteering_ua.model.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organization")
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(name = "image_url")
    private String imageURL;
    @Enumerated(EnumType.STRING)
    @Column(name = "volunteering_type")
    private VolunteeringType volunteeringType;


    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_admin", referencedColumnName = "id")
    private User organization_admin;

    @ManyToMany(mappedBy = "subscriptions")
    private List<Customer> subscribers;

    @ManyToMany(mappedBy = "volunteering_area")
    private List<Volunteer> volunteers;

    @OneToMany(mappedBy = "organization")
    private List<Aid_Request> requests;

    @ManyToOne
    @JoinColumn(name="location_id", nullable=false)
    private Location location;

    @OneToMany(mappedBy = "organization")
    private List<Event> events;

    @OneToMany(mappedBy = "organization_warehouse")
    private List<Product> products;

    @OneToMany(mappedBy = "organization")
    private List<Feedback> feedbacks;

    @Column(name = "stripe_api_key")
    private String stripe_api_key;
    @Column(name = "stripe_public_key")
    private String stripe_public_key;
    @Column(name = "stripe_secret_key")
    private String stripe_secret_key;

    public Organization(
            String name,
            String description,
            VolunteeringType volunteeringType,
            User organization_admin,
            Location location
    ) {
        super(System_Status.ACTIVE);
        this.name = name;
        this.description = description;
        this.volunteeringType = volunteeringType;
        this.organization_admin = organization_admin;
        this.location = location;
        this.volunteers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.events = new ArrayList<>();
        this.products = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
    }
}
