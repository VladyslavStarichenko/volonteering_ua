package nure.ua.volunteering_ua.repository.product;

import nure.ua.volunteering_ua.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
    @Modifying
    @Transactional
    @Query(value = "update product set name=?, description=?, amount=?, image=? where id=?", nativeQuery = true)
    void update(String name, String description, int amount, String image, Long id);


    @Modifying
    @Transactional
    @Query(value = "update product set amount=? where id=?", nativeQuery = true)
    void productUpdateAmount(int amount, Long id);

    @Query(value = "SELECT * FROM product WHERE organization_id =?", nativeQuery = true)
    Page<Product> findAllByOrganization_warehouse(Pageable pageable, Long organizationId);

    @Modifying
    @Transactional
    @Query(value = "delete FROM product where id=?", nativeQuery = true)
    void deleteById(Long id);

    @Query(value = "SELECT * FROM product p WHERE p.id=? LIMIT 1", nativeQuery = true)
    Optional<Product> findProductById(Long id);
}
