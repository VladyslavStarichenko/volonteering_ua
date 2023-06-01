package nure.ua.volunteering_ua.mapper.payment;

import com.stripe.model.Money;
import nure.ua.volunteering_ua.dto.payment.MoneyDto;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class MoneyMapper implements Function<Money, MoneyDto> {

    @Override
    public MoneyDto apply(Money money) {
        return new MoneyDto(
                (money.getAmount().doubleValue()/100),
                money.getCurrency()
        );
    }
}
