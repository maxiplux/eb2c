package app.quantun.eb2c.model.entity.bussines;

import app.quantun.eb2c.model.entity.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity class representing an Order Item.
 * This class is mapped to the "order_items" table in the database.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends AuditModel<String> {

    /**
     * The unique identifier for the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The order that this item belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties(value = {"orderItems", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ToString.Exclude
    private Order order;

    /**
     * The product associated with this order item.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties(value = {"category", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ToString.Exclude
    private Product product;

    /**
     * The quantity of the product ordered.
     */
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    /**
     * The unit price of the product at the time of ordering.
     */
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;

    /**
     * Any discount applied to this specific order item.
     */
    private BigDecimal discount;

    /**
     * Additional notes specific to this order item.
     */
    private String notes;

    /**
     * Method to calculate the total price for this order item.
     */
    @Transient
    public BigDecimal getSubtotal() {
        BigDecimal itemTotal = unitPrice.multiply(new BigDecimal(quantity));
        return discount != null ? itemTotal.subtract(discount) : itemTotal;
    }

    @PrePersist
    protected void onCreate() {
        if (discount == null) {
            discount = BigDecimal.ZERO;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderItem orderItem = (OrderItem) o;
        return getId() != null && Objects.equals(getId(), orderItem.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}