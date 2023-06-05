package nure.ua.volunteering_ua.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackPageResponse {

    private List<FeedBackGetDto> feedBacks;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
