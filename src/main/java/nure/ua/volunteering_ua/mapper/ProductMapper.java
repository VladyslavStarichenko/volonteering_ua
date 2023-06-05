package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.product.ProductGetDto;
import nure.ua.volunteering_ua.model.Product;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class ProductMapper implements Function<Product, ProductGetDto> {
    @Override
    public ProductGetDto apply(Product product) {
        return new ProductGetDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getAmount(),
                product.getImage(),
                product.getOrganization_warehouse().getName()
        );
    }
}
