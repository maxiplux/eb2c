package app.quantun.eb2c.model.contract.contract.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Order Item responses.
 * This class is used to transfer order item data between the server and the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {

    /**
     * The ID of the order item.
     */
    private Long id;

    /**
     * The ID of the product associated with this order item.
     */
    private Long productId;

    /**
     * The name of the product.
     */
    private String productName;

    /**
     * The quantity of the product ordered.
     */
    private Integer quantity;

    /**
     * The unit price of the product at the time of ordering.
     */
    private BigDecimal unitPrice;

    /**
     * Any discount applied to this specific order item.
     */
    private BigDecimal discount;

    /**
     * The subtotal for this order item (quantity * unitPrice - discount).
     */
    private BigDecimal subtotal;

    /**
     * Additional notes specific to this order item.
     */
    private String notes;
}