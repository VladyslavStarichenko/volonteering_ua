package nure.ua.volunteering_ua.repository.product;


import nure.ua.volunteering_ua.model.EventProduct;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventProductRepository extends PagingAndSortingRepository<EventProduct, Long> {
}
