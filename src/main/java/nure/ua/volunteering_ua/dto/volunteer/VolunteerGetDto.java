package nure.ua.volunteering_ua.dto.volunteer;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerGetDto {

  private long id;
  private String name;
  private String email;
  private List<String> organizations;

}
