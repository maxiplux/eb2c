package app.quantun.eb2c.rest;

import app.quantun.eb2c.model.contract.contract.request.OrderRequestDTO;
import app.quantun.eb2c.model.contract.contract.response.OrderResponseDTO;
import app.quantun.eb2c.model.contract.value.OrderStatus;
import app.quantun.eb2c.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing orders for end users.
 * This class provides endpoints for CRUD operations on orders.
 */
@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "Operations for managing orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order.
     *
     * @param orderRequestDTO the details of the order to be created
     * @return the created OrderResponseDTO
     */
    @PostMapping
    @Operation(summary = "Create a new order",
            description = "Add a new order to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order created successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponseDTO.class)))
            })
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Parameter(description = "Order details", required = true)
            @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Creating new order for organization ID: {}", orderRequestDTO.getOrganizationId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderRequestDTO));
    }

    /**
     * Retrieve a specific order by its ID.
     *
     * @param id the ID of the order
     * @return the OrderResponseDTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID",
            description = "Retrieve a specific order by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id) {
        log.info("Retrieving order with ID: {}", id);
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieve a specific order by its order number.
     *
     * @param orderNumber the order number
     * @return the OrderResponseDTO
     */
    @GetMapping("/by-number/{orderNumber}")
    @Operation(summary = "Get order by order number",
            description = "Retrieve a specific order by its order number",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    public ResponseEntity<OrderResponseDTO> getOrderByOrderNumber(
            @Parameter(description = "Order number", example = "ORD-12345678")
            @PathVariable String orderNumber) {
        log.info("Retrieving order with order number: {}", orderNumber);
        return orderService.getOrderByOrderNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cancel an order.
     *
     * @param id the ID of the order
     * @return the updated OrderResponseDTO
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order",
            description = "Cancel an existing order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order cancelled successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "400", description = "Order cannot be cancelled")
            })
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id) {
        log.info("Cancelling order with ID: {}", id);
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    /**
     * Retrieve all orders for a specific organization.
     *
     * @param organizationId the ID of the organization
     * @param page           the page number (zero-based)
     * @param size           the page size
     * @param sort           the sorting criteria
     * @return a page of OrderResponseDTO
     */
    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get orders by organization",
            description = "Retrieve all orders for a specific organization with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)))
            })
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByOrganization(
            @Parameter(description = "Organization ID", example = "1")
            @PathVariable Long organizationId,
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction, e.g. orderDate,desc", example = "orderDate,desc")
            @RequestParam(defaultValue = "orderDate,desc") String sort) {
        log.info("Retrieving orders for organization with ID: {} with pagination", organizationId);

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        return ResponseEntity.ok(orderService.getOrdersByOrganization(organizationId, pageable));
    }

    /**
     * Retrieve all orders for a specific organization with a specific status.
     *
     * @param organizationId the ID of the organization
     * @param status         the order status
     * @param page           the page number (zero-based)
     * @param size           the page size
     * @param sort           the sorting criteria
     * @return a page of OrderResponseDTO
     */
    @GetMapping("/organization/{organizationId}/status/{status}")
    @Operation(summary = "Get orders by organization and status",
            description = "Retrieve all orders for a specific organization with a specific status with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)))
            })
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByOrganizationAndStatus(
            @Parameter(description = "Organization ID", example = "1")
            @PathVariable Long organizationId,
            @Parameter(description = "Order status", example = "PENDING")
            @PathVariable OrderStatus status,
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction, e.g. orderDate,desc", example = "orderDate,desc")
            @RequestParam(defaultValue = "orderDate,desc") String sort) {
        log.info("Retrieving orders for organization with ID: {} and status: {} with pagination", organizationId, status);

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        return ResponseEntity.ok(orderService.getOrdersByOrganizationAndStatus(organizationId, status, pageable));
    }
}