package nure.ua.volunteering_ua.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.model.user.VolunteeringType;

import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request")
public class Aid_Request {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String description;

  private int amount;

  @ManyToOne
  @JoinColumn(name="organization_id", nullable=false)
  private Organization organization;

  @ManyToOne
  @JoinColumn(name = "customer_id",nullable = false)
  private Customer customer;

  @Enumerated(EnumType.STRING)
  @Column(name = "volunteering_type")
  private VolunteeringType volunteeringType;

  @Enumerated(EnumType.STRING)
  @Column(name = "request_status")
  private Request_Status requestStatus;

  @Column(name = "confirmation_code")
  private int confirmationCode;

  @Column(name = "queue_number")
  private int queueNumber;

  @Column(name = "receiving_address")
  private String receivingAddress;

  private Date created_date;

  private Date updated_date;


}
