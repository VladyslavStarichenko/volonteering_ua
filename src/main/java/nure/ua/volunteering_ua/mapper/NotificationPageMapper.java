package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.notification.NotificationPageResponse;
import nure.ua.volunteering_ua.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationPageMapper implements Function<Page<Notification>, NotificationPageResponse> {

    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationPageMapper(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    public NotificationPageResponse apply(Page<Notification> notifications) {
        return new NotificationPageResponse(
                notifications.getContent()
                        .stream()
                        .map(notificationMapper)
                        .collect(Collectors.toList()),
                notifications.getNumber(),
                notifications.getSize(),
                notifications.getTotalElements(),
                notifications.getTotalPages()
        );
    }
}
