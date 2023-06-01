package nure.ua.volunteering_ua.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPageResponse {
    private List<NotificationDto> events;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
