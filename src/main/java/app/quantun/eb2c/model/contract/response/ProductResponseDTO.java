package app.quantun.b2b.model.contract.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Product responses.
 * This class is used to transfer product data between the server and the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    /**
     * The ID of the product.
     */
    private Long id;

    /**
     * The name of the product.
     */
    private String name;

    /**
     * The description of the product.
     */
    private String description;

    /**
     * The price of the product.
     */
    private BigDecimal price;

    /**
     * Indicates whether the product is in stock.
     */
    private boolean inStock;

    /**
     * The quantity of the product in stock.
     */
    private int stock;
}
