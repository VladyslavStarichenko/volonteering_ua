package nure.ua.volonteering_ua.model.user;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nure.ua.volonteering_ua.model.Aid_Request;
import nure.ua.volonteering_ua.model.Feedback;
import nure.ua.volonteering_ua.model.Notification;
import java.util.ArrayList;
import java.util.List;

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

  @OneToMany(mappedBy="customer")
  private List<Notification> notifications;

  @OneToMany(mappedBy = "customer")
  private List<Aid_Request> requests;

  @OneToMany(mappedBy = "customer")
  private List<Feedback> feedbacks;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "location_id", referencedColumnName = "id")
  private Location location;
  public Customer(User user) {
    this.user = user;
    this.notifications = new ArrayList<>();
    this.subscriptions = new ArrayList<>();
    this.requests = new ArrayList<>();
  }

}
