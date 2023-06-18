package nure.ua.volunteering_ua.mapper.customer;

import nure.ua.volunteering_ua.dto.customer.CustomerGetDto;
import nure.ua.volunteering_ua.mapper.request.RequestMapper;
import nure.ua.volunteering_ua.model.user.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerMapper implements Function<Customer, CustomerGetDto> {

    private final RequestMapper requestMapper;

    @Autowired
    public CustomerMapper(RequestMapper requestMapper) {
        this.requestMapper = requestMapper;
    }

    @Override
    public CustomerGetDto apply(Customer customer) {

        return new CustomerGetDto(
                customer.getId(),
                customer.getUser().getUserName(),
                customer.getUser().getEmail(),
                customer.getAddress(),
                customer.getRequests().stream()
                        .map(requestMapper)
                        .collect(Collectors.toList()));
    }
}

