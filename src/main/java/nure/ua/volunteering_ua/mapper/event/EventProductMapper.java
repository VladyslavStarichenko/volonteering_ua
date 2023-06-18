package nure.ua.volunteering_ua.mapper.event;

import nure.ua.volunteering_ua.dto.product.EventProductGetDto;
import nure.ua.volunteering_ua.model.EventProduct;
import nure.ua.volunteering_ua.service.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EventProductMapper implements Function<EventProduct, EventProductGetDto> {

    @Override
    public EventProductGetDto apply(EventProduct product) {
        return new EventProductGetDto(
                product.getId(),
                product.getProduct().getId(),
                product.getProduct().getName(),
                product.getAmount(),
                product.getProduct().getDescription(),
                product.getProduct().getImage(),
                product.getEvent_warehouse().getName()
        );
    }
}
