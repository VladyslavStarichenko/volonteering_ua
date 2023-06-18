package nure.ua.volunteering_ua.repository.event;

import nure.ua.volunteering_ua.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

    @Query(value = "SELECT * FROM event ", nativeQuery = true )
    List<Event> getAllEvents();


    @Query(value = "SELECT * FROM event ", nativeQuery = true )
    Page<Event> getAllEventsPage(Pageable pageable);
    Page<Event> getAllByOrganization_Name(Pageable pageable , String organizationName);
    
    @Query(value = "SELECT * FROM event e WHERE e.id IN " +
            "(SELECT p.event_id FROM participation p WHERE p.customer_id = ?)", nativeQuery = true )
    Page<Event> getAllByCustomerId(Pageable pageable, Long customerId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO participation (customer_id, event_id) VALUES (?, ?)", nativeQuery = true)
    void participate(Long customer_id, Long event_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM participation WHERE customer_id = ? AND event_id = ?", nativeQuery = true)
    void unparticipate(Long customer_id, Long event_id);

    @Query(value = "SELECT * FROM event e WHERE e.id=? LIMIT 1", nativeQuery = true)
    Optional<Event> findEventById(Long id);
}
