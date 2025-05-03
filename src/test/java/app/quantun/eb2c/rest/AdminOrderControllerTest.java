package app.quantun.eb2c.rest;

import app.quantun.eb2c.Eb2cApplication;
import app.quantun.eb2c.TestConfig;
import app.quantun.eb2c.exception.OrderNotFoundException;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Eb2cApplication.class)
@AutoConfigureMockMvc
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private OrderResponseDTO orderResponseDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
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
    @DisplayName("Should return all orders")
    void shouldReturnAllOrders() throws Exception {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(List.of(orderResponseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/admin/orders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].orderNumber", is("ORD-12345678")))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    @Test
    @DisplayName("Should update order status successfully")
    void shouldUpdateOrderStatus_successfully() throws Exception {
        // Arrange
        OrderResponseDTO updatedOrder = OrderResponseDTO.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .status(OrderStatus.APPROVED)
                .organizationId(1L)
                .organizationName("Test Organization")
                .branchId(1L)
                .branchName("Test Branch")
                .totalAmount(BigDecimal.valueOf(200))
                .build();

        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(put("/api/admin/orders/1/status")
                        .param("status", "APPROVED"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.orderNumber", is("ORD-12345678")))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    @DisplayName("Should return 404 when updating status of non-existent order")
    void shouldReturn404_whenUpdatingStatusOfNonExistentOrder() throws Exception {
        // Arrange
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class)))
                .thenThrow(new OrderNotFoundException("Order not found with id: 1"));

        // Act & Assert
        mockMvc.perform(put("/api/admin/orders/1/status")
                        .param("status", "APPROVED"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return orders by status with pagination")
    void shouldReturnOrdersByStatus_withPagination() throws Exception {
        // Arrange
        Page<OrderResponseDTO> orderPage = new PageImpl<>(List.of(orderResponseDTO));
        when(orderService.getOrdersByStatus(any(OrderStatus.class), any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/admin/orders/status/PENDING")
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
    @DisplayName("Should return orders by date range with pagination")
    void shouldReturnOrdersByDateRange_withPagination() throws Exception {
        // Arrange
        Page<OrderResponseDTO> orderPage = new PageImpl<>(List.of(orderResponseDTO));
        when(orderService.getOrdersByDateRange(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(orderPage);

        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);

        // Act & Assert
        mockMvc.perform(get("/api/admin/orders/date-range")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
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