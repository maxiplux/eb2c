package app.quantun.eb2c.rest;

import app.quantun.eb2c.model.contract.contract.response.OrderResponseDTO;
import app.quantun.eb2c.model.contract.value.OrderStatus;
import app.quantun.eb2c.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller class for managing orders for administrators.
 * This class provides endpoints for administrative operations on orders.
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Order Management", description = "Administrative operations for managing orders")
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * Retrieve a list of all orders.
     *
     * @param page the page number (zero-based)
     * @param size the page size
     * @param sort the sorting criteria
     * @return a page of OrderResponseDTO
     */
    @GetMapping
    @Operation(summary = "Get all orders",
            description = "Retrieve a list of all orders with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)))
            })
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction, e.g. orderDate,desc", example = "orderDate,desc")
            @RequestParam(defaultValue = "orderDate,desc") String sort) {
        log.info("Retrieving all orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Update the status of an order.
     *
     * @param id     the ID of the order
     * @param status the new status
     * @return the updated OrderResponseDTO
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status",
            description = "Update the status of an existing order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order status updated successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "New order status", example = "APPROVED")
            @RequestParam OrderStatus status) {
        log.info("Updating status of order with ID: {} to {}", id, status);
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    /**
     * Retrieve all orders with a specific status.
     *
     * @param status the order status
     * @param page   the page number (zero-based)
     * @param size   the page size
     * @param sort   the sorting criteria
     * @return a page of OrderResponseDTO
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status",
            description = "Retrieve all orders with a specific status with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)))
            })
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByStatus(
            @Parameter(description = "Order status", example = "PENDING")
            @PathVariable OrderStatus status,
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction, e.g. orderDate,desc", example = "orderDate,desc")
            @RequestParam(defaultValue = "orderDate,desc") String sort) {
        log.info("Retrieving orders with status: {} with pagination", status);

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        return ResponseEntity.ok(orderService.getOrdersByStatus(status, pageable));
    }

    /**
     * Retrieve all orders placed between the specified dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param page      the page number (zero-based)
     * @param size      the page size
     * @param sort      the sorting criteria
     * @return a page of OrderResponseDTO
     */
    @GetMapping("/date-range")
    @Operation(summary = "Get orders by date range",
            description = "Retrieve all orders placed between the specified dates with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)))
            })
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByDateRange(
            @Parameter(description = "Start date (ISO format)", example = "2023-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)", example = "2023-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction, e.g. orderDate,desc", example = "orderDate,desc")
            @RequestParam(defaultValue = "orderDate,desc") String sort) {
        log.info("Retrieving orders between {} and {} with pagination", startDate, endDate);

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        return ResponseEntity.ok(orderService.getOrdersByDateRange(startDate, endDate, pageable));
    }
}