package nure.ua.volunteering_ua.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.model.StripeCurrency;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargeCustomerDto {
    private String token;
    private double amount;
    private StripeCurrency currency;
    private Long customerId;

}
