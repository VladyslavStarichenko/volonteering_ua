package nure.ua.volunteering_ua.model;

import lombok.*;
import nure.ua.volunteering_ua.dto.event.EventCreateDto;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.Location;
import nure.ua.volunteering_ua.model.user.Organization;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @ManyToOne
    @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;

    @OneToMany(mappedBy = "event_warehouse")
    private List<EventProduct> products;

    @ManyToOne
    @JoinColumn(name="location_id", nullable=false)
    private Location location;

    @ManyToMany(mappedBy = "events")
    private List<Customer> customers;

    private int capacity;

    public Event(EventCreateDto eventCreateDto){
        super(System_Status.ACTIVE);
        this.name = eventCreateDto.getName();
        this.description = eventCreateDto.getDescription();
        this.startDate = eventCreateDto.getStartDate();
        this.endDate = eventCreateDto.getEndDate();
        this.products = new ArrayList<>();
        this.location = new Location(eventCreateDto.getLocation());
        this.customers = new ArrayList<>();
        this.capacity =  eventCreateDto.getCapacity();
    }

    public void addParticipant(Customer customer){
         this.customers.add(customer);
    }

    public void removeParticipant(Customer customer){
         this.customers.remove(customer);
    }


}
