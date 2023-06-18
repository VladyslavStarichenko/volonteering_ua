package nure.ua.volunteering_ua.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDto {
    String object;
    List<MoneyDto> available;
    List<MoneyDto> pending;
}
