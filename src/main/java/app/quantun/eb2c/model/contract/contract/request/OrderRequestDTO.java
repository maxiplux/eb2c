package app.quantun.eb2c.model.contract.contract.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data Transfer Object for Order requests.
 * This class is used to transfer order data between the client and the server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    /**
     * Additional notes or comments for the order.
     */
    @Size(max = 500, message = "{order.notes.maxLength}")
    private String notes;

    /**
     * The ID of the organization placing the order.
     */
    @NotNull(message = "{order.organization.required}")
    private Long organizationId;

    /**
     * The ID of the branch placing the order.
     */
    @NotNull(message = "{order.branch.required}")
    private Long branchId;

    /**
     * The items included in this order.
     */
    @NotEmpty(message = "{order.items.notEmpty}")
    @Valid
    private Set<OrderItemRequestDTO> orderItems;
}