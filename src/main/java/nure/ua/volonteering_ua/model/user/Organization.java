package nure.ua.volonteering_ua.model.user;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nure.ua.volonteering_ua.model.*;

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


    @Enumerated(EnumType.STRING)
    private Volunteering_Type volunteering_type;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_admin", referencedColumnName = "id")
    private User organization_admin;

    @ManyToMany(mappedBy = "subscriptions")
    private List<Customer> subscribers;

    @ManyToMany(mappedBy = "volunteering_area")
    private List<Volunteer> volunteers;

    @OneToMany(mappedBy = "organization")
    private List<Aid_Request> requests;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @OneToMany(mappedBy = "organization")
    private List<Event> events;

    @OneToMany(mappedBy = "organization_warehouse")
    private List<Product> products;

    @OneToMany(mappedBy = "organization")
    private List<Feedback> feedbacks;

//    private List<Donate> donates;
//    private String balance;
//    private String stripe_api_key;
//    private String stripe_public_key;
//    private String stripe_secret_key;
//    private List<Transactions> transactions;

    public Organization(String name, Volunteering_Type volunteering_type, User organization_admin) {
        this.name = name;
        this.volunteering_type = volunteering_type;
        this.organization_admin = organization_admin;
        this.volunteers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
        this.requests = new ArrayList<>();

    }
}
