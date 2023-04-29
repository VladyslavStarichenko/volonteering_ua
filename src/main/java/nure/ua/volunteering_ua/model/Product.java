package nure.ua.volunteering_ua.model;

//class to describe products to donate or something that on a balance of the volunteering company


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nure.ua.volunteering_ua.model.user.Organization;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product extends BaseEntity {
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
    @JoinColumn(name = "organization_id",nullable = false)
    private Organization organization_warehouse;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event_warehouse;
}
