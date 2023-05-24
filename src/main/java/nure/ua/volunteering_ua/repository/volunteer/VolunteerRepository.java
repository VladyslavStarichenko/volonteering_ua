package nure.ua.volunteering_ua.repository.volunteer;

import nure.ua.volunteering_ua.model.user.User;
import nure.ua.volunteering_ua.model.user.Volunteer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface VolunteerRepository extends PagingAndSortingRepository<Volunteer, Long> {
  @Query(value = "SELECT * FROM volunteer v WHERE v.user_id  IN (SELECT u.id FROM users u  WHERE u.user_name =?1)", nativeQuery = true )
  Optional<Volunteer> getByUserName(String userName);

  Optional<Volunteer> getByUser_UserName(String userName);

  Optional<Volunteer> findByUser(User user);
}
