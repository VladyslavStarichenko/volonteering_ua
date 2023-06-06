package nure.ua.volunteering_ua.repository.product;


import nure.ua.volunteering_ua.model.EventProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventProductRepository extends PagingAndSortingRepository<EventProduct, Long> {

    @Query(value = "SELECT * FROM event_product WHERE event_id =?", nativeQuery = true)
    Page<EventProduct> findAllByEvent_warehouse(Pageable pageable, Long event_id);

    @Modifying
    @Transactional
    @Query(value = "update event_product set amount=? where id=?", nativeQuery = true)
    void productEventUpdateAmount(int amount, Long id);

    @Modifying
    @Transactional
    @Query(value = "delete FROM event_product where id=?", nativeQuery = true)
    void deleteById(Long id);
}
