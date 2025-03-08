package app.quantun.eb2c.repository;


import app.quantun.eb2c.model.entity.bussines.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for managing Product entities.
 * This interface provides methods for CRUD operations and custom queries on Product entities.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find products containing the given name.
     *
     * @param name the name to search for
     * @return a list of products containing the given name
     */
    List<Product> findByNameContaining(String name);

    /**
     * Find products priced below a given value.
     *
     * @param price the maximum price
     * @return a list of products priced below the given value
     */
    List<Product> findByPriceLessThan(BigDecimal price);

    /**
     * Find products that are currently in stock.
     *
     * @param inStock indicates whether the product is in stock
     * @return a list of products that are in stock
     */
    List<Product> findByInStock(boolean inStock);
}
