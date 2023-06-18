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

    private int amount;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event_warehouse;

    public EventProduct(Product product, Event event_warehouse, int amount) {
        super(System_Status.ACTIVE);
        this.amount = amount;
        this.product = product;
        this.event_warehouse = event_warehouse;
    }
}
