package nure.ua.volunteering_ua.mapper.notification;

import nure.ua.volunteering_ua.dto.notification.NotificationDto;
import nure.ua.volunteering_ua.model.Notification;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class NotificationMapper implements Function<Notification, NotificationDto> {
    @Override
    public NotificationDto apply(Notification notification) {
        return new NotificationDto(
                notification.getCustomer().getUser().getUserName(),
                notification.getMessage(),
                notification.getTitle()
        );
    }
}
