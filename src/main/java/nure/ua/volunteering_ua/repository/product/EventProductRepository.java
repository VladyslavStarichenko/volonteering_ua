package nure.ua.volunteering_ua.repository.product;


import nure.ua.volunteering_ua.dto.product.EventProductGetDto;
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

//    @Query(value = "SELECT * FROM event_product WHERE event_id =?", nativeQuery = true)
//    Page<EventProduct> findAllByEvent_warehouse(Pageable pageable, Long event_id);

    @Query(value = "SELECT ep.id AS \"id\", p.id  AS \"product_id\", p.name  AS \"product_name\", ep.amount AS \"amount\", p.description AS \"description\", p.image AS \"image\", e.name  AS \"event_name\" FROM event_product ep JOIN product p ON ep.product_id = p.id JOIN event e ON ep.event_id = e.id WHERE ep.event_id=?", nativeQuery = true)
    Page<EventProductGetDto> findAllByEvent_warehouse(Pageable pageable, Long event_id);

    @Modifying
    @Transactional
    @Query(value = "update event_product set amount=? where id=?", nativeQuery = true)
    void productEventUpdateAmount(int amount, Long id);

    @Modifying
    @Transactional
    @Query(value = "delete FROM event_product where id=?", nativeQuery = true)
    void deleteById(Long id);


}
