package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.product.EventProductPageResponse;
import nure.ua.volunteering_ua.dto.product.ProductPageResponse;
import nure.ua.volunteering_ua.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductPageMapper implements Function<Page<Product>, ProductPageResponse> {

    private final ProductMapper productMapper;

    @Autowired
    public ProductPageMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public ProductPageResponse apply(Page<Product> products) {
        return new ProductPageResponse(
                products.getContent()
                        .stream()
                        .map(productMapper)
                        .collect(Collectors.toList()),
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages()
        );
    }
}
