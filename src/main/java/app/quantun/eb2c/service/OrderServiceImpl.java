package app.quantun.eb2c.service;

import app.quantun.eb2c.exception.OrderNotFoundException;
import app.quantun.eb2c.mapper.OrderMapper;
import app.quantun.eb2c.model.contract.contract.request.OrderItemRequestDTO;
import app.quantun.eb2c.model.contract.contract.request.OrderRequestDTO;
import app.quantun.eb2c.model.contract.contract.response.OrderResponseDTO;
import app.quantun.eb2c.model.contract.value.OrderStatus;
import app.quantun.eb2c.model.entity.bussines.*;
import app.quantun.eb2c.repository.BranchRepository;
import app.quantun.eb2c.repository.OrderRepository;
import app.quantun.eb2c.repository.OrganizationRepository;
import app.quantun.eb2c.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing orders.
 * This class provides methods for CRUD operations and business logic related to orders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrganizationRepository organizationRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    /**
     * Retrieve a list of all orders.
     *
     * @return a list of OrderResponseDTO
     */
    @Override
    public List<OrderResponseDTO> getAllOrders() {
        log.info("Retrieving all orders");
        return orderMapper.toOrderResponseDTOList(orderRepository.findAll());
    }

    /**
     * Retrieve a specific order by its ID.
     *
     * @param id the ID of the order
     * @return an Optional containing the OrderResponseDTO if found, otherwise empty
     */
    @Override
    public Optional<OrderResponseDTO> getOrderById(Long id) {
        log.info("Retrieving order with ID: {}", id);
        return orderRepository.findById(id)
                .map(orderMapper::toOrderResponseDTO);
    }

    /**
     * Retrieve a specific order by its order number.
     *
     * @param orderNumber the order number
     * @return an Optional containing the OrderResponseDTO if found, otherwise empty
     */
    @Override
    public Optional<OrderResponseDTO> getOrderByOrderNumber(String orderNumber) {
        log.info("Retrieving order with order number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .map(orderMapper::toOrderResponseDTO);
    }

    /**
     * Create a new order.
     *
     * @param orderRequestDTO the details of the order to be created
     * @return the created OrderResponseDTO
     */
    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        log.info("Creating new order for organization ID: {}", orderRequestDTO.getOrganizationId());

        // Get organization
        Organization organization = organizationRepository.findById(orderRequestDTO.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + orderRequestDTO.getOrganizationId()));

        // Get branch
        Branch branch = branchRepository.findById(orderRequestDTO.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with id: " + orderRequestDTO.getBranchId()));

        // Create order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .notes(orderRequestDTO.getNotes())
                .organization(organization)
                .branch(branch)
                .build();

        // Add order items
        for (OrderItemRequestDTO itemDTO : orderRequestDTO.getOrderItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemDTO.getProductId()));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(product.getPrice())
                    .discount(itemDTO.getDiscount())
                    .notes(itemDTO.getNotes())
                    .build();

            order.addOrderItem(orderItem);
        }

        // Recalculate total
        order.recalculateTotal();

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {} and order number: {}", savedOrder.getId(), savedOrder.getOrderNumber());

        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    /**
     * Update the status of an order.
     *
     * @param id     the ID of the order
     * @param status the new status
     * @return the updated OrderResponseDTO
     */
    @Transactional
    @Override
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {
        log.info("Updating status of order with ID: {} to {}", id, status);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponseDTO(updatedOrder);
    }

    /**
     * Cancel an order.
     *
     * @param id the ID of the order
     * @return the updated OrderResponseDTO
     */
    @Transactional
    @Override
    public OrderResponseDTO cancelOrder(Long id) {
        log.info("Cancelling order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        // Only pending orders can be cancelled
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponseDTO(updatedOrder);
    }

    /**
     * Retrieve all orders for a specific organization.
     *
     * @param organizationId the ID of the organization
     * @return a list of OrderResponseDTO
     */
    @Override
    public List<OrderResponseDTO> getOrdersByOrganization(Long organizationId) {
        log.info("Retrieving orders for organization with ID: {}", organizationId);

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + organizationId));

        return orderMapper.toOrderResponseDTOList(orderRepository.findByOrganization(organization));
    }

    /**
     * Retrieve all orders for a specific organization with pagination.
     *
     * @param organizationId the ID of the organization
     * @param pageable       the pagination information
     * @return a page of OrderResponseDTO
     */
    @Override
    public Page<OrderResponseDTO> getOrdersByOrganization(Long organizationId, Pageable pageable) {
        log.info("Retrieving orders for organization with ID: {} with pagination", organizationId);

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + organizationId));

        return orderRepository.findByOrganization(organization, pageable)
                .map(orderMapper::toOrderResponseDTO);
    }

    /**
     * Retrieve all orders with a specific status.
     *
     * @param status the order status
     * @return a list of OrderResponseDTO
     */
    @Override
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {
        log.info("Retrieving orders with status: {}", status);
        return orderMapper.toOrderResponseDTOList(orderRepository.findByStatus(status));
    }

    /**
     * Retrieve all orders with a specific status with pagination.
     *
     * @param status   the order status
     * @param pageable the pagination information
     * @return a page of OrderResponseDTO
     */
    @Override
    public Page<OrderResponseDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        log.info("Retrieving orders with status: {} with pagination", status);
        return orderRepository.findByStatus(status, pageable)
                .map(orderMapper::toOrderResponseDTO);
    }

    /**
     * Retrieve all orders placed between the specified dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return a list of OrderResponseDTO
     */
    @Override
    public List<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Retrieving orders between {} and {}", startDate, endDate);
        return orderMapper.toOrderResponseDTOList(orderRepository.findByOrderDateBetween(startDate, endDate));
    }

    /**
     * Retrieve all orders placed between the specified dates with pagination.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param pageable  the pagination information
     * @return a page of OrderResponseDTO
     */
    @Override
    public Page<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Retrieving orders between {} and {} with pagination", startDate, endDate);
        return orderRepository.findByOrderDateBetween(startDate, endDate, pageable)
                .map(orderMapper::toOrderResponseDTO);
    }

    /**
     * Retrieve all orders for a specific organization with a specific status.
     *
     * @param organizationId the ID of the organization
     * @param status         the order status
     * @return a list of OrderResponseDTO
     */
    @Override
    public List<OrderResponseDTO> getOrdersByOrganizationAndStatus(Long organizationId, OrderStatus status) {
        log.info("Retrieving orders for organization with ID: {} and status: {}", organizationId, status);

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + organizationId));

        return orderMapper.toOrderResponseDTOList(orderRepository.findByOrganizationAndStatus(organization, status));
    }

    /**
     * Retrieve all orders for a specific organization with a specific status with pagination.
     *
     * @param organizationId the ID of the organization
     * @param status         the order status
     * @param pageable       the pagination information
     * @return a page of OrderResponseDTO
     */
    @Override
    public Page<OrderResponseDTO> getOrdersByOrganizationAndStatus(Long organizationId, OrderStatus status, Pageable pageable) {
        log.info("Retrieving orders for organization with ID: {} and status: {} with pagination", organizationId, status);

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + organizationId));

        return orderRepository.findByOrganizationAndStatus(organization, status, pageable)
                .map(orderMapper::toOrderResponseDTO);
    }

    /**
     * Generate a unique order number.
     *
     * @return the generated order number
     */
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}