package nure.ua.volunteering_ua.model.user;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.model.Aid_Request;
import nure.ua.volunteering_ua.model.Event;
import nure.ua.volunteering_ua.model.Feedback;
import nure.ua.volunteering_ua.model.Notification;
import org.springframework.http.HttpStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "subscription",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    private List<Organization> subscriptions;

    @OneToMany(mappedBy = "customer")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "customer")
    private List<Aid_Request> requests;

    @OneToMany(mappedBy = "customer")
    private List<Feedback> feedbacks;

    @ManyToMany
    @JoinTable(
            name = "participation",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;
    private String address;

    public Customer(User user, String address) {
        this.user = user;
        this.notifications = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
        this.address = address;
    }

    public void subscribe(Organization organization) {
        if (this.subscriptions.stream()
                .noneMatch(o -> Objects.equals(o.getId(), organization.getId()))) {
            this.subscriptions.add(organization);
        } else {
            throw new CustomException("The customer is already subscribing an organization", HttpStatus.BAD_REQUEST);
        }
    }

    public void unsubscribe(Organization organization) {
        if (this.subscriptions.stream()
                .anyMatch(o -> Objects.equals(o.getId(), organization.getId()))) {
            this.subscriptions.remove(organization);
        } else {
            throw new CustomException("The customer is not subscribing an organization", HttpStatus.BAD_REQUEST);
        }
    }
}
