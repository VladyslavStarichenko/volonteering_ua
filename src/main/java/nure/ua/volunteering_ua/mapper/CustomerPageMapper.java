package nure.ua.volunteering_ua.mapper;


import nure.ua.volunteering_ua.dto.customer.CustomerPageResponse;
import nure.ua.volunteering_ua.model.user.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerPageMapper implements Function<Page<Customer>, CustomerPageResponse> {
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerPageMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerPageResponse apply(Page<Customer> customers) {
        return new CustomerPageResponse(
                customers.getContent()
                        .stream()
                        .map(customerMapper)
                        .collect(Collectors.toList()),
                customers.getNumber(),
                customers.getSize(),
                customers.getTotalElements(),
                customers.getTotalPages()
        );
    }
}
