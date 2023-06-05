package nure.ua.volunteering_ua.repository.customer;


import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

    Optional<Customer> findByUser(User user);

    Optional<Customer> findCustomerByUser_UserName(String username);

    @Query(value = "SELECT * FROM customer c WHERE c.id IN " +
            "(SELECT p.customer_id FROM participation p WHERE p.event_id = ?) ", nativeQuery = true)
    Page<Customer> findAllParticipants(Pageable pageable, Long eventId);

    Optional<Customer> findCustomerById(Long id);


    @Query(value = "SELECT * FROM customer c WHERE c.id IN " +
            "(SELECT s.customer_id FROM subscription s WHERE s.organization_id = ?) ", nativeQuery = true)
    Page<Customer> getAllByOrganization(Pageable pageable, long organizationId);
}


