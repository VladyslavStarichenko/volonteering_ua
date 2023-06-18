package nure.ua.volunteering_ua.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateDto2 {
    private String customerUsername;
    private String message;
    private String title;
    private String organizationName;
}
