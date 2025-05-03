package app.quantun.eb2c.service;

import app.quantun.eb2c.model.contract.contract.request.OrderRequestDTO;
import app.quantun.eb2c.model.contract.contract.response.OrderResponseDTO;
import app.quantun.eb2c.model.contract.value.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing orders.
 * This interface provides methods for CRUD operations and business logic related to orders.
 */
public interface OrderService {

    /**
     * Retrieve a list of all orders.
     *
     * @return a list of OrderResponseDTO
     */
    List<OrderResponseDTO> getAllOrders();

    /**
     * Retrieve a specific order by its ID.
     *
     * @param id the ID of the order
     * @return an Optional containing the OrderResponseDTO if found, otherwise empty
     */
    Optional<OrderResponseDTO> getOrderById(Long id);

    /**
     * Retrieve a specific order by its order number.
     *
     * @param orderNumber the order number
     * @return an Optional containing the OrderResponseDTO if found, otherwise empty
     */
    Optional<OrderResponseDTO> getOrderByOrderNumber(String orderNumber);

    /**
     * Create a new order.
     *
     * @param orderRequestDTO the details of the order to be created
     * @return the created OrderResponseDTO
     */
    @Transactional
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);

    /**
     * Update the status of an order.
     *
     * @param id     the ID of the order
     * @param status the new status
     * @return the updated OrderResponseDTO
     */
    @Transactional
    OrderResponseDTO updateOrderStatus(Long id, OrderStatus status);

    /**
     * Cancel an order.
     *
     * @param id the ID of the order
     * @return the updated OrderResponseDTO
     */
    @Transactional
    OrderResponseDTO cancelOrder(Long id);

    /**
     * Retrieve all orders for a specific organization.
     *
     * @param organizationId the ID of the organization
     * @return a list of OrderResponseDTO
     */
    List<OrderResponseDTO> getOrdersByOrganization(Long organizationId);

    /**
     * Retrieve all orders for a specific organization with pagination.
     *
     * @param organizationId the ID of the organization
     * @param pageable       the pagination information
     * @return a page of OrderResponseDTO
     */
    Page<OrderResponseDTO> getOrdersByOrganization(Long organizationId, Pageable pageable);

    /**
     * Retrieve all orders with a specific status.
     *
     * @param status the order status
     * @return a list of OrderResponseDTO
     */
    List<OrderResponseDTO> getOrdersByStatus(OrderStatus status);

    /**
     * Retrieve all orders with a specific status with pagination.
     *
     * @param status   the order status
     * @param pageable the pagination information
     * @return a page of OrderResponseDTO
     */
    Page<OrderResponseDTO> getOrdersByStatus(OrderStatus status, Pageable pageable);

    /**
     * Retrieve all orders placed between the specified dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return a list of OrderResponseDTO
     */
    List<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieve all orders placed between the specified dates with pagination.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param pageable  the pagination information
     * @return a page of OrderResponseDTO
     */
    Page<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Retrieve all orders for a specific organization with a specific status.
     *
     * @param organizationId the ID of the organization
     * @param status         the order status
     * @return a list of OrderResponseDTO
     */
    List<OrderResponseDTO> getOrdersByOrganizationAndStatus(Long organizationId, OrderStatus status);

    /**
     * Retrieve all orders for a specific organization with a specific status with pagination.
     *
     * @param organizationId the ID of the organization
     * @param status         the order status
     * @param pageable       the pagination information
     * @return a page of OrderResponseDTO
     */
    Page<OrderResponseDTO> getOrdersByOrganizationAndStatus(Long organizationId, OrderStatus status, Pageable pageable);
}