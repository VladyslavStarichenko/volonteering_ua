package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.product.EventProductGetDto;
import nure.ua.volunteering_ua.model.EventProduct;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EventProductMapper implements Function<EventProduct, EventProductGetDto> {
    @Override
    public EventProductGetDto apply(EventProduct product) {
        return new EventProductGetDto(
                product.getId(),
                product.getProduct().getId(),
                product.getEvent_warehouse().getId(),
                product.getAmount()

        );
    }
}
