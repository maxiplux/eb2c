package app.quantun.eb2c.model.contract.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for encapsulating product search criteria.
 * This class provides a clean way to pass multiple search parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {
    private String namePattern;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;
    private Integer minStock;
    private String categoryName;

    // Additional fields could be added as needed
}

