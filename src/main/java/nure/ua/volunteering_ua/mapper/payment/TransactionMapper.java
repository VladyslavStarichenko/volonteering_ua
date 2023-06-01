package nure.ua.volunteering_ua.mapper.payment;

import com.stripe.model.Charge;
import nure.ua.volunteering_ua.dto.payment.TransactionDto;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class TransactionMapper implements Function<Charge, TransactionDto> {
    @Override
    public TransactionDto apply(Charge charge) {
        return new TransactionDto(
                charge.getId(),
                charge.getObject(),
                charge.getAmount().doubleValue() /100,
                charge.getApplication(),
                charge.getCurrency(),
                charge.getReceiptEmail(),
                charge.getDescription()
        );
    }
}
