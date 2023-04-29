package nure.ua.volunteering_ua.repository.volunteer;

import nure.ua.volunteering_ua.model.user.User;
import nure.ua.volunteering_ua.model.user.Volunteer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface VolunteerRepository extends PagingAndSortingRepository<Volunteer, Long> {
  Optional<Volunteer> findByUser(User user);
}
