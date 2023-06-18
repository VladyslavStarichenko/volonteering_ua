package nure.ua.volunteering_ua.dto.volunteer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerPageResponse {
    private List<VolunteerGetDto> requests;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
