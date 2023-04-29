package nure.ua.volunteering_ua.model.user;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "volunteer")
public class Volunteer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @ManyToMany
  @JoinTable(
          name = "organization_volunteers",
          joinColumns = @JoinColumn(name = "volunteer_id"),
          inverseJoinColumns = @JoinColumn(name = "organization_id")
  )
  private List<Organization> volunteering_area;

  public Volunteer(User user) {
    this.user = user;
    this.volunteering_area = new ArrayList<>();
  }
}
