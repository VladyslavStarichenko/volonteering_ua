package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.product.*;
import nure.ua.volunteering_ua.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(value = "Operations with products")
@RequestMapping(value = "/products/")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "Get the organization resources' product by id")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductGetDto> getProductById(
            @ApiParam(value = "Product id") @PathVariable Long productId) {
        return new ResponseEntity<>(productService
                .getProductById(productId), HttpStatus.OK);
    }

    @ApiOperation(value = "Get the event resources' product by id")
    @GetMapping("/event-product/{eventProductId}")
    public ResponseEntity<EventProductGetDto> getEventProductById(
            @ApiParam(value = "Event product id") @PathVariable Long eventProductId) {
        return new ResponseEntity<>(productService
                .getProductEventById(eventProductId), HttpStatus.OK);
    }


    @ApiOperation(value = "Get all organization resources' products")
    @GetMapping("/all-products/{pageNumber}/{pageSize}/{sortBy}/{organizationName}")
    public ResponseEntity<ProductPageResponse> getAllProducts(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by parameter")  @PathVariable(required = false) String sortBy,
            @ApiParam(value = "Organization name")  @PathVariable String organizationName

            ) {
        if (sortBy.isBlank()) {
            sortBy = "name";
        }
        return new ResponseEntity<>(productService
                .getAllOrganizationProducts(pageNumber, pageSize, sortBy, organizationName), HttpStatus.OK);
    }


    @ApiOperation(value = "Get all event resources' products")
    @GetMapping("/all-event-products/{pageNumber}/{pageSize}/{sortBy}/{eventId}")
    public ResponseEntity<EventProductPageResponse> getAllEventProducts(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by parameter")  @PathVariable(required = false) String sortBy,
            @ApiParam(value = "Event id")  @PathVariable Long eventId

    ) {
        if (sortBy.isBlank()) {
            sortBy = "product_id";
        }
        return new ResponseEntity<>(productService
                .getAllEventProducts(pageNumber, pageSize, sortBy, eventId), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete the Event Product")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_ADMIN','ROLE_VOLUNTEER')")
    @DeleteMapping("/delete-event-product/{productId}")
    public ResponseEntity<String> deleteEventProduct(
            @ApiParam(value = "Event Product id")  @PathVariable Long productId
            ) {
        productService.deleteProductFromEvent(productId);
        return new ResponseEntity<>("The product is successfully deleted from the event", HttpStatus.OK);
    }

    @ApiOperation(value = "Delete the Organization Product")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_ADMIN','ROLE_VOLUNTEER')")
    @DeleteMapping("/delete-organization-product/{productId}")
    public ResponseEntity<String> deleteProduct(
            @ApiParam(value = "Product id")  @PathVariable Long productId
    ) {
        productService.deleteProductFromOrganization(productId);
        return new ResponseEntity<>("The product is successfully deleted from the organization", HttpStatus.OK);
    }

    @ApiOperation(value = "Add product to organization warehouse")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_ADMIN','ROLE_VOLUNTEER')")
    @PostMapping("/add-product")
    public ResponseEntity<ProductGetDto> addProductOrganization(
            @ApiParam(value = "Product object to add to organization resources", required = true)
            @RequestBody ProductCreateDto productCreateDto) {
        ProductGetDto product = productService
                .addProductToSystem(productCreateDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Add product to event warehouse")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_ADMIN','ROLE_VOLUNTEER')")
    @PostMapping("/add-event-product")
    public ResponseEntity<EventProductGetDto> createProductOrganization(
            @ApiParam(value = "Product to add to event")  @RequestBody EventProductCreateDto productCreateDto
    ) {
        EventProductGetDto product = productService
                .addProductToEvent(productCreateDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update product in the organization warehouse")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_ADMIN','ROLE_VOLUNTEER')")
    @PutMapping("/update-product/{productId}")
    public ResponseEntity<ProductGetDto> updateOrganizationProduct(
            @ApiParam(value = "Product object to update the organization resources", required = true)
            @RequestBody ProductCreateDto productCreateDto,
            @ApiParam(value = "Product id")  @PathVariable Long productId
            ) {
        ProductGetDto product = productService
                .updateProduct(productCreateDto, productId);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update product in the event warehouse")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_ADMIN','ROLE_VOLUNTEER')")
    @PutMapping("/update-event-product/{productEventId}/{amount}")
    public ResponseEntity<String> updateEventProduct(
            @ApiParam(value = "Product event id")  @PathVariable Long productEventId,
            @ApiParam(value = "Amount")  @PathVariable int amount
    ) {
        productService
                .changeEventProductAmount(productEventId, amount);
        return new ResponseEntity<>("The product from event warehouse was successfully updated", HttpStatus.CREATED);
    }
}
