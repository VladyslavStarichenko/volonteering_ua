package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.product.ProductEventGetDto;
import nure.ua.volunteering_ua.dto.product.ProductGetDto;
import nure.ua.volunteering_ua.model.EventProduct;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EventProductMapper implements Function<EventProduct, ProductEventGetDto> {
    @Override
    public ProductEventGetDto apply(EventProduct product) {
        return new ProductEventGetDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getAmount(),
                product.getImage(),
                product.getEvent_warehouse() != null ? product.getEvent_warehouse().getName() : ""
        );
    }
}
