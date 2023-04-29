package nure.ua.volunteering_ua.repository.notification;

import nure.ua.volunteering_ua.model.Notification;
import nure.ua.volunteering_ua.model.user.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends PagingAndSortingRepository<Notification, Long> {

  List<Notification> getNotificationByCustomer(Customer customer);
}
