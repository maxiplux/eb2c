package app.quantun.b2b.model.contract.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Product requests.
 * This class is used to transfer product data between the client and the server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    private Long id;
    /**
     * The name of the product.
     * It must not be blank and its length must not exceed 255 characters.
     */
    @NotBlank(message = "{product.name.required}")
    @Size(max = 255, message = "{product.name.maxLength}")
    private String name;

    /**
     * The description of the product.
     */
    private String description;

    /**
     * The price of the product.
     * It must be a positive value.
     */
    @Positive(message = "{product.price.positive}")
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
