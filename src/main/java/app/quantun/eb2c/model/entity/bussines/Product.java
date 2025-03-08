package app.quantun.eb2c.model.entity.bussines;

import app.quantun.eb2c.model.entity.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity class representing a Product.
 * This class is mapped to the "Product" table in the database.
 */

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
@ToString

@NoArgsConstructor
@AllArgsConstructor
public class Product  extends AuditModel<String> {

    /**
     * The unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product.
     * It must not be blank.
     */
    @NotBlank(message = "Product name is required")
    private String name;

    /**
     * The description of the product.
     */
    private String description;

    /**
     * The price of the product.
     * It must be a positive value.
     */
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    /**
     * Indicates whether the product is in stock.
     */
    private boolean inStock;

    /**
     * The quantity of the product in stock.
     */
    private int stock;

    @JsonIgnoreProperties(value = {"categories", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Category category;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Product product = (Product) o;
        return getId() != null && Objects.equals(getId(), product.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
