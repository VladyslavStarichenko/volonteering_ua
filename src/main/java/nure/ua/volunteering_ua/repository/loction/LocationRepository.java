package nure.ua.volunteering_ua.repository.loction;

import nure.ua.volunteering_ua.model.user.Location;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends PagingAndSortingRepository<Location, Long> {
}
