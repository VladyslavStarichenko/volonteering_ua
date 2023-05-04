package nure.ua.volunteering_ua.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationPageResponse {

  List<OrganizationGetDto> organizations;
  private int pageNumber;
  private int pageSize;
  private long totalElements;
  private int totalPages;


}
