package nure.ua.volunteering_ua.model;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nure.ua.volunteering_ua.model.user.Customer;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String title;

    private String message;

    @CreationTimestamp
    @Column(name = "created")
    private Date createdAt;

    public Notification(Customer customer, String message, String title) {
        this.customer = customer;
        this.message = message;
        this.title = title;
    }
}
