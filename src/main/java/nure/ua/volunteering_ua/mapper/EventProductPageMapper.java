package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.product.EventProductGetDto;
import nure.ua.volunteering_ua.dto.product.EventProductPageResponse;
import nure.ua.volunteering_ua.model.EventProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;



@Service
public class EventProductPageMapper implements Function<Page<EventProductGetDto>, EventProductPageResponse> {


    @Override
    public EventProductPageResponse apply(Page<EventProductGetDto> eventProducts) {
        return new EventProductPageResponse(
                eventProducts.getContent(),
                eventProducts.getNumber(),
                eventProducts.getSize(),
                eventProducts.getTotalElements(),
                eventProducts.getTotalPages()
        );
    }
}
