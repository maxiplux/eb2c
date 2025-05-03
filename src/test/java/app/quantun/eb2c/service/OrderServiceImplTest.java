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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Organization organization;
    private Branch branch;
    private Product product;
    private Order order;
    private OrderResponseDTO orderResponseDTO;
    private OrderRequestDTO orderRequestDTO;
    private OrderItemRequestDTO orderItemRequestDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Organization");

        branch = new Branch();
        branch.setId(1L);
        branch.setName("Test Branch");
        branch.setOrganization(organization);

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .build();

        order = Order.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .organization(organization)
                .branch(branch)
                .totalAmount(BigDecimal.valueOf(200))
                .build();
        order.addOrderItem(orderItem);

        orderResponseDTO = OrderResponseDTO.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .status(OrderStatus.PENDING)
                .organizationId(1L)
                .organizationName("Test Organization")
                .branchId(1L)
                .branchName("Test Branch")
                .totalAmount(BigDecimal.valueOf(200))
                .build();

        orderItemRequestDTO = OrderItemRequestDTO.builder()
                .productId(1L)
                .quantity(2)
                .build();

        orderRequestDTO = OrderRequestDTO.builder()
                .organizationId(1L)
                .branchId(1L)
                .orderItems(Set.of(orderItemRequestDTO))
                .build();
    }

    @Test
    @DisplayName("Should return all orders")
    void shouldReturnAllOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toOrderResponseDTOList(anyList())).thenReturn(List.of(orderResponseDTO));

        // Act
        List<OrderResponseDTO> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderResponseDTO, result.get(0));
        verify(orderRepository).findAll();
        verify(orderMapper).toOrderResponseDTOList(anyList());
    }

    @Test
    @DisplayName("Should return order by ID when exists")
    void shouldReturnOrderById_whenExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        Optional<OrderResponseDTO> result = orderService.getOrderById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(orderResponseDTO, result.get());
        verify(orderRepository).findById(1L);
        verify(orderMapper).toOrderResponseDTO(order);
    }

    @Test
    @DisplayName("Should return empty when order ID does not exist")
    void shouldReturnEmpty_whenOrderIdDoesNotExist() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<OrderResponseDTO> result = orderService.getOrderById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(orderRepository).findById(1L);
        verify(orderMapper, never()).toOrderResponseDTO(any());
    }

    @Test
    @DisplayName("Should return order by order number when exists")
    void shouldReturnOrderByOrderNumber_whenExists() {
        // Arrange
        String orderNumber = "ORD-12345678";
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        Optional<OrderResponseDTO> result = orderService.getOrderByOrderNumber(orderNumber);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(orderResponseDTO, result.get());
        verify(orderRepository).findByOrderNumber(orderNumber);
        verify(orderMapper).toOrderResponseDTO(order);
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrder_successfully() {
        // Arrange
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.createOrder(orderRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(orderResponseDTO, result);
        verify(organizationRepository).findById(1L);
        verify(branchRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toOrderResponseDTO(any(Order.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when organization not found")
    void shouldThrowEntityNotFoundException_whenOrganizationNotFound() {
        // Arrange
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(orderRequestDTO));
        verify(organizationRepository).findById(1L);
        verify(branchRepository, never()).findById(anyLong());
        verify(productRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order status successfully")
    void shouldUpdateOrderStatus_successfully() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.APPROVED);

        // Assert
        assertNotNull(result);
        assertEquals(orderResponseDTO, result);
        assertEquals(OrderStatus.APPROVED, order.getStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(order);
        verify(orderMapper).toOrderResponseDTO(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when updating status of non-existent order")
    void shouldThrowOrderNotFoundException_whenUpdatingStatusOfNonExistentOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(1L, OrderStatus.APPROVED));
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void shouldCancelOrder_successfully() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.cancelOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(orderResponseDTO, result);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(order);
        verify(orderMapper).toOrderResponseDTO(order);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when cancelling non-pending order")
    void shouldThrowIllegalStateException_whenCancellingNonPendingOrder() {
        // Arrange
        order.setStatus(OrderStatus.APPROVED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should return orders by organization")
    void shouldReturnOrdersByOrganization() {
        // Arrange
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(orderRepository.findByOrganization(organization)).thenReturn(List.of(order));
        when(orderMapper.toOrderResponseDTOList(anyList())).thenReturn(List.of(orderResponseDTO));

        // Act
        List<OrderResponseDTO> result = orderService.getOrdersByOrganization(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderResponseDTO, result.get(0));
        verify(organizationRepository).findById(1L);
        verify(orderRepository).findByOrganization(organization);
        verify(orderMapper).toOrderResponseDTOList(anyList());
    }

    @Test
    @DisplayName("Should return orders by organization with pagination")
    void shouldReturnOrdersByOrganization_withPagination() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        Page<OrderResponseDTO> expectedPage = new PageImpl<>(List.of(orderResponseDTO));

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(orderRepository.findByOrganization(organization, pageable)).thenReturn(orderPage);
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        Page<OrderResponseDTO> result = orderService.getOrdersByOrganization(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(orderResponseDTO, result.getContent().get(0));
        verify(organizationRepository).findById(1L);
        verify(orderRepository).findByOrganization(organization, pageable);
        verify(orderMapper).toOrderResponseDTO(order);
    }
}
