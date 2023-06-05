package nure.ua.volunteering_ua.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_product")
public class EventProduct extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private Integer amount;

    private String image;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event_warehouse;

    public EventProduct(String name, String description, Integer amount, String image, Event event_warehouse) {
        super(System_Status.ACTIVE);
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.image = image;
        this.event_warehouse = event_warehouse;
    }
}
