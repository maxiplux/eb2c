package app.quantun.eb2c.model.contract.contract.response;

import app.quantun.eb2c.model.contract.value.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for Order responses.
 * This class is used to transfer order data between the server and the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    /**
     * The ID of the order.
     */
    private Long id;

    /**
     * The order number that can be displayed to customers.
     */
    private String orderNumber;

    /**
     * The timestamp when the order was placed.
     */
    private LocalDateTime orderDate;

    /**
     * The total amount of the order.
     */
    private BigDecimal totalAmount;

    /**
     * The current status of the order.
     */
    private OrderStatus status;

    /**
     * Additional notes or comments for the order.
     */
    private String notes;

    /**
     * The ID of the organization that placed the order.
     */
    private Long organizationId;

    /**
     * The name of the organization that placed the order.
     */
    private String organizationName;

    /**
     * The ID of the branch that placed the order.
     */
    private Long branchId;

    /**
     * The name of the branch that placed the order.
     */
    private String branchName;

    /**
     * The items included in this order.
     */
    private Set<OrderItemResponseDTO> orderItems;
}