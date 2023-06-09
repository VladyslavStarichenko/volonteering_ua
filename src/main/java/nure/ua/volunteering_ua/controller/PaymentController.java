package nure.ua.volunteering_ua.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import nure.ua.volunteering_ua.dto.payment.*;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.service.payment.StripeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/payment")
@Api(value = "Operations with payment")

public class PaymentController {

    private final StripeClient stripeClient;

    @Autowired
    PaymentController(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @GetMapping("balance/{organizationName}")
    public BalanceDto getBalance(
            @ApiParam(value = "Organization name to check transactions") @PathVariable String organizationName
    ) {
        return stripeClient.getBalance(organizationName);
    }

    @GetMapping("/transactions/{organizationName}/{limit}")
    public List<TransactionDto> getTransactions(
            @ApiParam(value = "Organization name to check transactions")
            @PathVariable String organizationName,
            @ApiParam(value = "Limit of transactions to retrieve")
            @PathVariable int limit
    ) {
        return stripeClient.getTransactions(organizationName, limit);
    }

    @GetMapping("/stripeAccess/{organizationName}")
    public ResponseEntity<PaymentMethodDto> getStripeKeys(
            @ApiParam(value = "Organization name to check transactions")
            @PathVariable String organizationName
    ) {
        return new ResponseEntity<>(stripeClient.getStripeKey(organizationName),HttpStatus.OK);
    }

    @PutMapping("/addPaymentMethod")
    public ResponseEntity<String> addPayment(
            @ApiParam(value = "Payment details")
            @RequestBody PaymentMethodAddDto payment
    ) {
        stripeClient.updatePayment(payment);
        return new ResponseEntity<>("Payment details are successfully updated",
                HttpStatus.CREATED);
    }

    @PostMapping("/charge")
    public TransactionDto chargeCard(
            @RequestHeader(value="token") String token,
            @ApiParam(value = "Payment details")
            @RequestBody ChargeCustomerDto chargeCustomerDto) {
        try {
            return this.stripeClient.chargeNewCard(chargeCustomerDto, token);
        } catch (Exception e) {
            throw new CustomException("Payment was failed\n" + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
