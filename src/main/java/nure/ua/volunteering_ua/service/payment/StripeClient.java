package nure.ua.volunteering_ua.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.payment.*;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.payment.BalanceMapper;
import nure.ua.volunteering_ua.mapper.payment.TransactionMapper;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.model.user.User;
import nure.ua.volunteering_ua.repository.customer.CustomerRepository;
import nure.ua.volunteering_ua.repository.organization.OrganizationRepository;
import nure.ua.volunteering_ua.service.security.service.UserServiceSCRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StripeClient {

    private final OrganizationRepository organizationRepository;

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECRET_KEY = "ThisIsASecretKey";
    private final TransactionMapper transactionMapper;
    private final UserServiceSCRT userServiceSCRT;
    private final CustomerRepository customerRepository;
    private final BalanceMapper balanceMapper;




    @Autowired
    public StripeClient(OrganizationRepository organizationRepository, TransactionMapper transactionMapper, UserServiceSCRT userServiceSCRT, CustomerRepository customerRepository, BalanceMapper balanceMapper) {
        this.organizationRepository = organizationRepository;
        this.transactionMapper = transactionMapper;
        this.userServiceSCRT = userServiceSCRT;
        this.customerRepository = customerRepository;
        this.balanceMapper = balanceMapper;


    }

    public BalanceDto getBalance(String organizationName) {
        try {
            Stripe.apiKey = getStripe_secret_key(organizationName);
            return balanceMapper.apply(Balance.retrieve());

        } catch (StripeException stripeException) {
            throw new CustomException("There is an error with retrieving balance of the organization", HttpStatus.BAD_REQUEST);
        }
    }

    public List<TransactionDto> getTransactions(String organizationName, int limit)  {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("limit", limit); // Specify the number of transactions to retrieve
            Stripe.apiKey = getStripe_secret_key(organizationName);
            return Charge.list(params).getData()
                    .stream()
                    .map(transactionMapper)
                    .collect(Collectors.toList());

        } catch (StripeException stripeException) {
            throw new CustomException("There is an error with retrieving transactions of the organization", HttpStatus.BAD_REQUEST);
        }
    }

    private String getStripe_secret_key(String organizationName) {
        return getStripeKey(organizationName).getStripe_secret_key();
    }




    public Customer createCustomer(ChargeCustomerDto chargeCustomerDto, String token) throws Exception {
        User currentLoggedInUser = userServiceSCRT.getCurrentLoggedInUser();
        nure.ua.volunteering_ua.model.user.Customer customer = customerRepository.findByUser(currentLoggedInUser)
                .orElseThrow(() -> new CustomException("There is no customer logged in", HttpStatus.BAD_REQUEST));
        String stripeCustomerEmail = "";
        if (customer.getId() == chargeCustomerDto.getCustomerId()) {
            stripeCustomerEmail = customer.getUser().getEmail();
        } else {
            throw new CustomException("Id of the customer is not matching with logged in customer", HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("email", stripeCustomerEmail);
        customerParams.put("source", token);
        return Customer.create(customerParams);
    }

    private Customer getCustomer(String id) throws Exception {
        return Customer.retrieve(id);
    }

    public TransactionDto chargeNewCard(ChargeCustomerDto chargeCustomerDto, String token ) throws Exception {
        Stripe.apiKey = getStripe_secret_key(chargeCustomerDto.getOrganizationName());
        Customer stripeCustomer = createCustomer(chargeCustomerDto, token);
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", (int) (chargeCustomerDto.getAmount() * 100));
        chargeParams.put("currency", chargeCustomerDto.getCurrency().toString());
        chargeParams.put("source", stripeCustomer.getDefaultSource());
        log.info("token " + token);
//        chargeParams.put("receiptEmail", stripeCustomer.getEmail());
//        chargeParams.put("application", "Volunteering_UA");
//        chargeParams.put("description", "Donate to Volunteering_UA");
        return transactionMapper.apply(Charge.create(chargeParams));
    }

    public Charge chargeCustomerCard(String customerId, int amount) throws Exception {
        String sourceCard = getCustomer(customerId).getDefaultSource();
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", amount);
        chargeParams.put("currency", "USD");
        chargeParams.put("customer", customerId);
        chargeParams.put("source", sourceCard);
        return Charge.create(chargeParams);
    }

    public PaymentMethodDto getStripeKey(String organizationName) {
        Optional<Organization> organizationDb = organizationRepository.getOrganizationByName(organizationName);
        if (organizationDb.isPresent()) {
            Organization organization = organizationDb.get();
            return new PaymentMethodDto(
                    decrypt(organization.getStripe_api_key()),
                    decrypt(organization.getStripe_public_key()),
                    decrypt(organization.getStripe_secret_key())
            );
        } else {
            throw new CustomException(
                    "There is an exception occurred during the payment details retrieving",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    public void updatePayment(PaymentMethodAddDto paymentMethodAddDto) {
        String stripeApiKey = paymentMethodAddDto.getStripe_api_key();
        String stripePublicKey = paymentMethodAddDto.getStripe_public_key();
        String stripeSecretKey = paymentMethodAddDto.getStripe_secret_key();

        String encryptedApiKey = encrypt(stripeApiKey);
        String encryptedPublicKey = encrypt(stripePublicKey);
        String encryptedSecretKey = encrypt(stripeSecretKey);

        organizationRepository.updatePaymentMethod(
                encryptedApiKey,
                encryptedPublicKey,
                encryptedSecretKey,
                paymentMethodAddDto.getId()
        );

    }

    private String encrypt(String value) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new CustomException("There is an exception during adding payment\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    private String decrypt(String encryptedValue) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CustomException("There is an exception during adding payment\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
