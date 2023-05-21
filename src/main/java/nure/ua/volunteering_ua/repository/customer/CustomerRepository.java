package nure.ua.volunteering_ua.repository.customer;


import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

  Optional<Customer> findByUser(User user);

  Optional<Customer> findCustomerByUser_UserName(String username);
}
