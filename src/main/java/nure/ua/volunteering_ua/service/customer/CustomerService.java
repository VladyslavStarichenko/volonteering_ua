package nure.ua.volunteering_ua.service.customer;

import nure.ua.volunteering_ua.dto.customer.CustomerGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.CustomerMapper;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.repository.customer.CustomerRepository;
import nure.ua.volunteering_ua.repository.organization.OrganizationRepository;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserServiceSCRT userServiceSCRT;

    private final OrganizationRepository organizationRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, UserServiceSCRT userServiceSCRT, OrganizationRepository organizationRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.userServiceSCRT = userServiceSCRT;
        this.organizationRepository = organizationRepository;
        this.customerMapper = customerMapper;
    }

    public CustomerGetDto getCurrentLoggedInCustomer() {
        return customerRepository
                .findByUser(userServiceSCRT.getCurrentLoggedInUser())
                .map(customerMapper)
                .orElseThrow(() -> new CustomException("There is no customer account associated with the current user", HttpStatus.NOT_FOUND));
    }


    public Customer getCurrentLoggedInCustomerInternal() {
        return customerRepository
                .findByUser(userServiceSCRT.getCurrentLoggedInUser())
                .orElseThrow(() -> new CustomException("There is no customer account associated with the current user", HttpStatus.NOT_FOUND));
    }

    public Customer getCustomerByIdInternal(Long id) {
        return customerRepository
                .findCustomerById(id)
                .orElseThrow(() -> new CustomException("There is no customer account associated with the id provided", HttpStatus.NOT_FOUND));
    }
    public CustomerGetDto getCustomerByName(String username) {
        return customerRepository
                .findCustomerByUser_UserName(username)
                .map(customerMapper)
                .orElseThrow(() -> new CustomException("There is no customer account associated with the name provided", HttpStatus.NOT_FOUND));
    }


    public Customer getCustomerByNameInternal(String username) {
        return customerRepository
                .findCustomerByUser_UserName(username)
                .orElseThrow(() -> new CustomException("There is no customer account associated with the name provided", HttpStatus.NOT_FOUND));
    }

    public void subscribeOrganization(String organizationName) {
        customerRepository.findByUser(userServiceSCRT.getCurrentLoggedInUser())
                .ifPresent(customer -> organizationRepository.getOrganizationByName(organizationName)
                        .ifPresent(organization -> {
                            customer.subscribe(organization);
                            customerRepository.save(customer);
                        }));
    }

    public void unSubscribeOrganization(String organizationName) {
        customerRepository.findByUser(userServiceSCRT.getCurrentLoggedInUser())
                .ifPresent(customer -> organizationRepository.getOrganizationByName(organizationName)
                        .ifPresent(organization -> {
                            customer.unsubscribe(organization);
                            customerRepository.save(customer);
                        }));
    }
}
