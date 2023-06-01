package nure.ua.volunteering_ua.dto.payment;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodDto {
    private String stripe_api_key;
    private String stripe_public_key;
    private String stripe_secret_key;
}
