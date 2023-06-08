package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.product.EventProductGetDto;
import nure.ua.volunteering_ua.dto.product.EventProductPageResponse;
import nure.ua.volunteering_ua.model.EventProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class EventProductPageMapper implements Function<Page<EventProduct>, EventProductPageResponse> {

    private final EventProductMapper eventProductMapper;

    @Autowired
    public EventProductPageMapper(EventProductMapper eventProductMapper) {
        this.eventProductMapper = eventProductMapper;
    }

    @Override
    public EventProductPageResponse apply(Page<EventProduct> eventProducts) {
        return new EventProductPageResponse(
                eventProducts.getContent()
                        .stream()
                        .map(eventProductMapper)
                        .collect(Collectors.toList()),
                eventProducts.getNumber(),
                eventProducts.getSize(),
                eventProducts.getTotalElements(),
                eventProducts.getTotalPages()
        );
    }
}
