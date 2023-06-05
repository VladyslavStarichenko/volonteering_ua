package nure.ua.volunteering_ua.repository.feedbsck;

import nure.ua.volunteering_ua.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FeedBackRepository extends PagingAndSortingRepository<Feedback, Long> {

    Page<Feedback> getAllByOrganization_id(Pageable pageable, long id);

}
