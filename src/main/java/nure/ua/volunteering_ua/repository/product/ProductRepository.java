package nure.ua.volunteering_ua.repository.product;

import nure.ua.volunteering_ua.model.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
    @Modifying
    @Transactional
    @Query(value = "update product set name=?, description=?, amount=?, image=? where id=?", nativeQuery = true)
    void update(String name, String description, int amount, String image, Long id);


    @Modifying
    @Transactional
    @Query(value = "update product set amount=? where id=?", nativeQuery = true)
    void addToEventUpdateAmount(Long event_id, int amount, Long id);


}
