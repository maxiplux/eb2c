package app.quantun.eb2c.model.contract.contract.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Order Item requests.
 * This class is used to transfer order item data between the client and the server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDTO {

    /**
     * The ID of the product associated with this order item.
     */
    @NotNull(message = "{orderItem.product.required}")
    private Long productId;

    /**
     * The quantity of the product ordered.
     */
    @NotNull(message = "{orderItem.quantity.required}")
    @Positive(message = "{orderItem.quantity.positive}")
    private Integer quantity;

    /**
     * Any discount applied to this specific order item.
     */
    private BigDecimal discount;

    /**
     * Additional notes specific to this order item.
     */
    private String notes;
}