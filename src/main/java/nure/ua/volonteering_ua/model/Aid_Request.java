package nure.ua.volonteering_ua.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nure.ua.volonteering_ua.model.user.Customer;
import nure.ua.volonteering_ua.model.user.Organization;
import nure.ua.volonteering_ua.model.user.Volunteering_Type;

import javax.persistence.*;


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
  private Volunteering_Type volunteeringType;

  @Enumerated(EnumType.STRING)
  @Column(name = "request_status")
  private Request_Status requestStatus;

  @Column(name = "confirmation_code")
  private int confirmationCode;

  @Column(name = "queue_number")
  private int queueNumber;

  @Column(name = "receiving_address")
  private String receivingAddress;

}
