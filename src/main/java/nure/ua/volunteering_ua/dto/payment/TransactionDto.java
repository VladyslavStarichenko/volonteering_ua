package nure.ua.volunteering_ua.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String id;
    private String object;
    private double amount;
    private String application;
    private String currency;
    private String customerEmail;
    private String description;

}
