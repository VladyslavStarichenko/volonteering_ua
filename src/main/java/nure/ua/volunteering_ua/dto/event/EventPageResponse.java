package nure.ua.volunteering_ua.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPageResponse {
    private List<EventGetDto> events;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
