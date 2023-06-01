package nure.ua.volunteering_ua.mapper.payment;

import com.stripe.model.Balance;
import nure.ua.volunteering_ua.dto.payment.BalanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BalanceMapper implements Function<Balance, BalanceDto> {

    private final MoneyMapper moneyMapper;

    @Autowired
    public BalanceMapper(MoneyMapper moneyMapper) {
        this.moneyMapper = moneyMapper;
    }

    @Override
    public BalanceDto apply(Balance balance) {
        return new BalanceDto(
                balance.getObject(),
                balance.getAvailable().stream()
                        .map(moneyMapper)
                        .collect(Collectors.toList()),
                balance.getPending().stream()
                        .map(moneyMapper)
                        .collect(Collectors.toList())
        );
    }
}
