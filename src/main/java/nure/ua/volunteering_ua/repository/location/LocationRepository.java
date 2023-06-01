package nure.ua.volunteering_ua.repository.location;

import nure.ua.volunteering_ua.model.user.Location;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends PagingAndSortingRepository<Location, Long> {
    Optional<Location> getLocationsByAddress(String address);

}
