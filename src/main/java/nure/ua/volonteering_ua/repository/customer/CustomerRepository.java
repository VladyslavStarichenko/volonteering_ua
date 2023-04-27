package nure.ua.volonteering_ua.repository.customer;


import nure.ua.volonteering_ua.model.user.Customer;
import nure.ua.volonteering_ua.model.user.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

  Optional<Customer> findByUser(User user);
}
