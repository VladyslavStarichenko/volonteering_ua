package nure.ua.volunteering_ua.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AidRequestPageResponse {


  private List<RequestGetDto> requests;
  private int pageNumber;
  private int pageSize;
  private long totalElements;
  private int totalPages;




}
