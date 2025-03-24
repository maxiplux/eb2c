package app.quantun.eb2c.rest;


import app.quantun.eb2c.model.contract.request.ProductRequestDTO;
import app.quantun.eb2c.model.contract.request.ProductSearchCriteria;
import app.quantun.eb2c.model.contract.response.ProductResponseDTO;
import app.quantun.eb2c.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller class for managing products.
 * This class provides endpoints for CRUD operations on products.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * // Retrieve all products
 * ResponseEntity<List<ProductResponseDTO>> response = productRestController.getAllProducts();
 * List<ProductResponseDTO> products = response.getBody();
 *
 * // Retrieve a product by ID
 * ResponseEntity<ProductResponseDTO> response = productRestController.getProductById(1L);
 * ProductResponseDTO product = response.getBody();
 *
 * // Create a new product
 * ProductRequestDTO newProduct = new ProductRequestDTO();
 * newProduct.setName("New Product");
 * ResponseEntity<ProductResponseDTO> response = productRestController.createProduct(newProduct);
 * ProductResponseDTO createdProduct = response.getBody();
 *
 * // Update an existing product
 * ProductRequestDTO updatedProduct = new ProductRequestDTO();
 * updatedProduct.setName("Updated Product");
 * ResponseEntity<ProductResponseDTO> response = productRestController.updateProduct(1L, updatedProduct);
 * ProductResponseDTO updatedProductResponse = response.getBody();
 *
 * // Delete a product
 * ResponseEntity<Void> response = productRestController.deleteProduct(1L);
 * }
 * </pre>
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Operations for managing products")
public class ProductRestController {
    private final ProductService productService;

    /**
     * Retrieve a list of all products.
     *
     * @return a list of ProductResponseDTO
     */
    @GetMapping
    @Operation(summary = "Get all products",
            description = "Retrieve a list of all products",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Retrieve a specific product by its ID.
     *
     * @param id the ID of the product
     * @return the ProductResponseDTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID",
            description = "Retrieve a specific product by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            })
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add a new product to the system.
     *
     * @param productRequestDTO the details of the product to be created
     * @return the created ProductResponseDTO
     */
    @PostMapping
    @Operation(summary = "Create a new product",
            description = "Add a new product to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Product created successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Parameter(description = "Product details", required = true)
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productRequestDTO));
    }

    /**
     * Update details of an existing product.
     *
     * @param id                the ID of the product to be updated
     * @param productRequestDTO the updated product details
     * @return the updated ProductResponseDTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product",
            description = "Update details of an existing product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product updated successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated product details", required = true)
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequestDTO));
    }

    /**
     * Remove a product from the system.
     *
     * @param id the ID of the product to be deleted
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product",
            description = "Remove a product from the system",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
            })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find products containing the given name.
     *
     * @param name the name to search for
     * @return a list of ProductResponseDTO
     */
    @GetMapping("/search")
    @Operation(summary = "Search products by name",
            description = "Find products containing the given name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<List<ProductResponseDTO>> searchProductsByName(
            @Parameter(description = "Product name to search", example = "Phone")
            @RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    /**
     * Retrieve products priced below a given value.
     *
     * @param price the maximum price
     * @return a list of ProductResponseDTO
     */
    @GetMapping("/under-price")
    @Operation(summary = "Get products under a specific price",
            description = "Retrieve products priced below a given value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<List<ProductResponseDTO>> getProductsUnderPrice(
            @Parameter(description = "Maximum price", example = "100.00")
            @RequestParam BigDecimal price) {
        return ResponseEntity.ok(productService.getProductsUnderPrice(price));
    }

    /**
     * Retrieve all products that are currently in stock.
     *
     * @return a list of ProductResponseDTO
     */
    @GetMapping("/in-stock")
    @Operation(summary = "Get products in stock",
            description = "Retrieve all products that are currently in stock",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved in-stock products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<List<ProductResponseDTO>> getInStockProducts() {
        return ResponseEntity.ok(productService.getInStockProducts());
    }


    /**
     * Search for products based on multiple criteria with pagination.
     *
     * @param criteria the search criteria
     * @param page     the page number (zero-based)
     * @param size     the page size
     * @param sort     the sorting criteria
     * @return a page of products matching the criteria
     */
    @PostMapping("/search")
    @Operation(summary = "Search products by criteria",
            description = "Search for products based on multiple criteria with pagination and sorting",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved filtered products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)))
            })
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestBody ProductSearchCriteria criteria,
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction, e.g. name,asc", example = "name,asc")
            @RequestParam(defaultValue = "id,asc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<ProductResponseDTO> products = productService.findProductsByCriteria(criteria, pageable);

        return ResponseEntity.ok(products);
    }

}
