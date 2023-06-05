package nure.ua.volunteering_ua.service.product;

import nure.ua.volunteering_ua.dto.product.ProductCreateDto;
import nure.ua.volunteering_ua.dto.product.ProductEventGetDto;
import nure.ua.volunteering_ua.dto.product.ProductGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.EventProductMapper;
import nure.ua.volunteering_ua.mapper.ProductMapper;
import nure.ua.volunteering_ua.model.Event;
import nure.ua.volunteering_ua.model.EventProduct;
import nure.ua.volunteering_ua.model.Product;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.repository.product.EventProductRepository;
import nure.ua.volunteering_ua.repository.product.ProductRepository;
import nure.ua.volunteering_ua.service.event.EventService;
import nure.ua.volunteering_ua.service.organization.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrganizationService organizationService;
    private final EventProductRepository eventProductRepository;
    private final ProductMapper productMapper;
    private final EventService eventService;
    private final EventProductMapper eventProductMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, OrganizationService organizationService, EventProductRepository eventProductRepository, ProductMapper productMapper, EventService eventService, EventProductMapper eventProductMapper) {
        this.productRepository = productRepository;
        this.organizationService = organizationService;
        this.eventProductRepository = eventProductRepository;
        this.productMapper = productMapper;
        this.eventService = eventService;
        this.eventProductMapper = eventProductMapper;
    }

    public ProductGetDto addProductToSystem(ProductCreateDto productCreateDto) {
        if (isProductCreateDtoInvalid(productCreateDto)) {
            throw new CustomException("Product create object has data filling error", HttpStatus.BAD_REQUEST);
        } else {
            Organization organization = organizationService.getOrganizationByNameInternalUsage(productCreateDto.getOrganizationName());
            Product product = new Product(
                    productCreateDto.getName(),
                    productCreateDto.getDescription(),
                    productCreateDto.getAmount(),
                    productCreateDto.getImage(),
                    organization
            );

            return productMapper.apply(productRepository.save(product));
        }
    }

    public ProductEventGetDto addProductToEvent(Long productId, Long eventId, int amount) {
        Product product = getProductByIdInternal(productId);
        Event event = eventService.getEventByIdInternal(eventId);
        if (product.getAmount() >= amount) {
            product.setAmount(product.getAmount() - amount);
            productRepository.addToEventUpdateAmount(eventId, amount, productId);
        }
        EventProduct eventProduct = new EventProduct(
                product.getName(),
                product.getDescription(),
                amount,
                product.getImage(),
                event
        );
        return eventProductMapper.apply(eventProductRepository.save(eventProduct));
    }

    public ProductGetDto updateProduct(ProductCreateDto productCreateDto, Long id) {
        if (isProductCreateDtoInvalid(productCreateDto)) {
            throw new CustomException("Product create object has data filling error", HttpStatus.BAD_REQUEST);
        } else {
            Product product2Update = productRepository.findById(id)
                    .orElseThrow(
                            () -> new CustomException("There is an error to find the product with specified id", HttpStatus.NOT_FOUND)
                    );
            product2Update.setName(productCreateDto.getName());
            product2Update.setDescription(productCreateDto.getDescription());
            product2Update.setAmount(productCreateDto.getAmount());
            product2Update.setImage(productCreateDto.getImage());

            productRepository.update(
                    productCreateDto.getName(),
                    productCreateDto.getDescription(),
                    productCreateDto.getAmount(),
                    productCreateDto.getImage(),
                    id
            );
            return productMapper.apply(product2Update);
        }
    }

    public Product getProductByIdInternal(Long id) {
        return productRepository.findById(id)
                .orElseThrow(
                        () -> new CustomException(
                                "There is no product with specified id",
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    public ProductGetDto getProductById(Long id) {
        return productMapper.apply(productRepository.findById(id)
                .orElseThrow(
                        () -> new CustomException(
                                "There is no product with specified id",
                                HttpStatus.NOT_FOUND
                        )
                ));
    }


    private boolean isProductCreateDtoInvalid(ProductCreateDto productCreateDto) {
        return productCreateDto.getName().isEmpty() || productCreateDto.getDescription().isEmpty()
                || productCreateDto.getAmount() == null || productCreateDto.getAmount() == 0
                || productCreateDto.getImage().isEmpty();
    }


}
