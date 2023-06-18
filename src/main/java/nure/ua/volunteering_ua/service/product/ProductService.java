package nure.ua.volunteering_ua.service.product;

import nure.ua.volunteering_ua.dto.product.*;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.EventProductMapper;
import nure.ua.volunteering_ua.mapper.EventProductPageMapper;
import nure.ua.volunteering_ua.mapper.ProductMapper;
import nure.ua.volunteering_ua.mapper.ProductPageMapper;
import nure.ua.volunteering_ua.model.Event;
import nure.ua.volunteering_ua.model.EventProduct;
import nure.ua.volunteering_ua.model.Product;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.repository.product.EventProductRepository;
import nure.ua.volunteering_ua.repository.product.ProductRepository;
import nure.ua.volunteering_ua.service.event.EventService;
import nure.ua.volunteering_ua.service.organization.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrganizationService organizationService;
    private final EventProductRepository eventProductRepository;
    private final ProductMapper productMapper;
    private final EventService eventService;
    private final EventProductMapper eventProductMapper;
    private final ProductPageMapper productPageMapper;
    private final EventProductPageMapper eventProductPageMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, OrganizationService organizationService, EventProductRepository eventProductRepository, ProductMapper productMapper, EventService eventService, EventProductMapper eventProductMapper, ProductPageMapper productPageMapper, EventProductPageMapper eventProductPageMapper) {
        this.productRepository = productRepository;
        this.organizationService = organizationService;
        this.eventProductRepository = eventProductRepository;
        this.productMapper = productMapper;
        this.eventService = eventService;
        this.eventProductMapper = eventProductMapper;
        this.productPageMapper = productPageMapper;

        this.eventProductPageMapper = eventProductPageMapper;
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

    public EventProductGetDto addProductToEvent(EventProductCreateDto eventProductCreateDto) {
        int amount = eventProductCreateDto.getAmount();
        Long productId = eventProductCreateDto.getProductId();
        Product product = getProductByIdInternal(productId);
        Event event = eventService.getEventByIdInternal(eventProductCreateDto.getEventId());
        if (product.getAmount() < amount) {
            throw new CustomException("It's not enough resources", HttpStatus.BAD_REQUEST);
        } else {
            Optional<EventProduct> eventProductDb = event.getProducts().
                    stream()
                    .filter(product2Check -> Objects.equals(product2Check.getProduct().getId(), productId))
                    .findAny();
            if (eventProductDb.isPresent()) {
                EventProduct eventProduct = eventProductDb.get();
                addIfEventProductExist(amount, productId, product, eventProduct);
                eventProduct.setAmount(eventProduct.getAmount() + eventProductCreateDto.getAmount());
                return eventProductMapper.apply(eventProduct);
            } else {
                EventProduct eventProduct = new EventProduct(
                        product,
                        event,
                        amount
                );
                product.setAmount(product.getAmount() - amount);
                productRepository.productUpdateAmount(product.getAmount(), productId);
                return eventProductMapper.apply(eventProductRepository.save(eventProduct));
            }
        }

    }

    private void addIfEventProductExist(int amount, Long productId, Product product, EventProduct eventProduct) {
        eventProductRepository.productEventUpdateAmount(eventProduct.getAmount() + amount, eventProduct.getId());
        product.setAmount(product.getAmount() - amount);
        productRepository.productUpdateAmount(product.getAmount(), productId);
    }

    public void changeEventProductAmount(Long productEventId, int amount) {
        EventProduct eventProduct = getProductEventByIdInternal(productEventId);
        Product product = eventProduct.getProduct();
        if (product.getAmount() < amount - eventProduct.getAmount()) {
            throw new CustomException(
                    "The amount of the product is not enough",
                    HttpStatus.BAD_REQUEST
            );
        } else {
            product.setAmount(product.getAmount() + eventProduct.getAmount() - amount);
            productRepository.productUpdateAmount(product.getAmount(), product.getId());
            eventProductRepository.productEventUpdateAmount(amount, eventProduct.getId());
        }
    }

    public ProductGetDto updateProduct(ProductCreateDto productCreateDto, Long id) {
        if (isProductCreateDtoInvalid(productCreateDto)) {
            throw new CustomException("Product create object has data filling error", HttpStatus.BAD_REQUEST);
        } else {
            Product product2Update = getProductByIdInternal(id);
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

    public void deleteProductFromEvent(Long id) {
        EventProduct product = getProductEventByIdInternal(id);
        eventProductRepository.deleteById(product.getId());
    }

    public void deleteProductFromOrganization(Long id) {
        Product product = getProductByIdInternal(id);
        productRepository.deleteById(product.getId());
    }

    public EventProductGetDto getProductEventById(Long id) {
        return eventProductMapper.apply(eventProductRepository.findById(id)
                .orElseThrow(
                        () -> new CustomException(
                                "There is no product with specified id",
                                HttpStatus.NOT_FOUND
                        )
                ));
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

    public ProductPageResponse getAllOrganizationProducts(int pageNumber, int sizeOfPage, String sortBy, String organizationName) {
        Organization organization = organizationService.getOrganizationByNameInternalUsage(organizationName);
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        return productPageMapper.apply(productRepository.findAllByOrganization_warehouse(pageable, organization.getId()));
    }

    public EventProductPageResponse getAllEventProducts(int pageNumber, int sizeOfPage, String sortBy, Long eventId) {
        Event event = eventService.getEventByIdInternal(eventId);
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        Page<EventProduct> allByEvent_warehouse = eventProductRepository.findAllByEvent_warehouse(pageable, event.getId());
        return eventProductPageMapper.apply(allByEvent_warehouse);
    }

    private Product getProductByIdInternal(Long id) {
        return productRepository.findProductById(id)
                .orElseThrow(
                        () -> new CustomException(
                                "There is no product with specified id",
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    private EventProduct getProductEventByIdInternal(Long id) {
        return eventProductRepository.findById(id)
                .orElseThrow(
                        () -> new CustomException(
                                "There is no product with specified id",
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    private boolean isProductCreateDtoInvalid(ProductCreateDto productCreateDto) {
        return productCreateDto.getName().isEmpty() || productCreateDto.getDescription().isEmpty()
                || productCreateDto.getAmount() == null || productCreateDto.getAmount() == 0
                || productCreateDto.getImage().isEmpty();
    }


}
