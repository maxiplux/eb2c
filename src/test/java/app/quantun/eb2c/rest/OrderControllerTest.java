package app.quantun.eb2c.rest;

import app.quantun.eb2c.Eb2cApplication;
import app.quantun.eb2c.TestConfig;
import app.quantun.eb2c.exception.OrderNotFoundException;
import app.quantun.eb2c.model.contract.contract.request.OrderItemRequestDTO;
import app.quantun.eb2c.model.contract.contract.request.OrderRequestDTO;
import app.quantun.eb2c.model.contract.contract.response.OrderResponseDTO;
import app.quantun.eb2c.model.contract.value.OrderStatus;
import app.quantun.eb2c.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Eb2cApplication.class)
@AutoConfigureMockMvc
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private OrderRequestDTO orderRequestDTO;
    private OrderResponseDTO orderResponseDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        OrderItemRequestDTO orderItemRequestDTO = OrderItemRequestDTO.builder()
                .productId(1L)
                .quantity(2)
                .build();

        orderRequestDTO = OrderRequestDTO.builder()
                .organizationId(1L)
                .branchId(1L)
                .orderItems(Set.of(orderItemRequestDTO))
                .build();

        orderResponseDTO = OrderResponseDTO.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .organizationId(1L)
                .organizationName("Test Organization")
                .branchId(1L)
                .branchName("Test Branch")
                .totalAmount(BigDecimal.valueOf(200))
                .orderItems(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrder_successfully() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(orderResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.orderNumber", is("ORD-12345678")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.organizationId", is(1)))
                .andExpect(jsonPath("$.organizationName", is("Test Organization")))
                .andExpect(jsonPath("$.branchId", is(1)))
                .andExpect(jsonPath("$.branchName", is("Test Branch")))
                .andExpect(jsonPath("$.totalAmount", is(200)));
    }

    @Test
    @DisplayName("Should return order by ID when exists")
    void shouldReturnOrderById_whenExists() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(orderResponseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/orders/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.orderNumber", is("ORD-12345678")))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @DisplayName("Should return 404 when order ID does not exist")
    void shouldReturn404_whenOrderIdDoesNotExist() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/orders/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return order by order number when exists")
    void shouldReturnOrderByOrderNumber_whenExists() throws Exception {
        // Arrange
        String orderNumber = "ORD-12345678";
        when(orderService.getOrderByOrderNumber(orderNumber)).thenReturn(Optional.of(orderResponseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/orders/by-number/" + orderNumber))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.orderNumber", is(orderNumber)))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void shouldCancelOrder_successfully() throws Exception {
        // Arrange
        OrderResponseDTO cancelledOrder = OrderResponseDTO.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .status(OrderStatus.CANCELLED)
                .organizationId(1L)
                .organizationName("Test Organization")
                .branchId(1L)
                .branchName("Test Branch")
                .totalAmount(BigDecimal.valueOf(200))
                .build();

        when(orderService.cancelOrder(1L)).thenReturn(cancelledOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders/1/cancel"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.orderNumber", is("ORD-12345678")))
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    @DisplayName("Should return 404 when cancelling non-existent order")
    void shouldReturn404_whenCancellingNonExistentOrder() throws Exception {
        // Arrange
        when(orderService.cancelOrder(1L)).thenThrow(new OrderNotFoundException("Order not found with id: 1"));

        // Act & Assert
        mockMvc.perform(post("/api/orders/1/cancel"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return orders by organization with pagination")
    void shouldReturnOrdersByOrganization_withPagination() throws Exception {
        // Arrange
        Page<OrderResponseDTO> orderPage = new PageImpl<>(List.of(orderResponseDTO));
        when(orderService.getOrdersByOrganization(anyLong(), any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/orders/organization/1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "orderDate,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].orderNumber", is("ORD-12345678")))
                .andExpect(jsonPath("$.content[0].status", is("PENDING")));
    }

    @Test
    @DisplayName("Should return orders by organization and status with pagination")
    void shouldReturnOrdersByOrganizationAndStatus_withPagination() throws Exception {
        // Arrange
        Page<OrderResponseDTO> orderPage = new PageImpl<>(List.of(orderResponseDTO));
        when(orderService.getOrdersByOrganizationAndStatus(anyLong(), any(OrderStatus.class), any(Pageable.class)))
                .thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/orders/organization/1/status/PENDING")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "orderDate,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].orderNumber", is("ORD-12345678")))
                .andExpect(jsonPath("$.content[0].status", is("PENDING")));
    }
}
