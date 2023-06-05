package nure.ua.volunteering_ua.repository.feedbsck;

import nure.ua.volunteering_ua.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface FeedBackRepository extends PagingAndSortingRepository<Feedback, Long> {

    Page<Feedback> getAllByOrganization_id(Pageable pageable, long id);

    @Modifying
    @Transactional
    @Query(value = "update feedback set comment=?, rating=?, is_edited =? where id=?", nativeQuery = true)
    void update(String comment, int rating, boolean is_edited, Long id);





}
