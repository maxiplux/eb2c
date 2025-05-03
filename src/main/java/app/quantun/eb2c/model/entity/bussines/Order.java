package app.quantun.eb2c.model.entity.bussines;

import app.quantun.eb2c.model.contract.value.OrderStatus;
import app.quantun.eb2c.model.entity.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity class representing an Order.
 * This class is mapped to the "orders" table in the database.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Order extends AuditModel<String> {

    /**
     * The unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The order number that can be displayed to customers.
     */
    @Column(unique = true, nullable = false)
    private String orderNumber;

    /**
     * The timestamp when the order was placed.
     */
    @Column(nullable = false)
    private LocalDateTime orderDate;

    /**
     * The total amount of the order.
     */
    @Column(nullable = false)
    private BigDecimal totalAmount;

    /**
     * The current status of the order.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * Additional notes or comments for the order.
     */
    @Column(length = 500)
    private String notes;

    /**
     * The organization that placed the order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonIgnoreProperties(value = {"branches", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ToString.Exclude
    private Organization organization;

    /**
     * The specific branch that placed the order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    @JsonIgnoreProperties(value = {"organization", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ToString.Exclude
    private Branch branch;

    /**
     * The line items included in this order.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"order", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();

    /**
     * Helper method to maintain bidirectional relationship with OrderItems
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * Helper method to maintain bidirectional relationship with OrderItems
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    /**
     * Recalculates the total amount based on all order items
     */
    public void recalculateTotal() {
        this.totalAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        recalculateTotal();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Order order = (Order) o;
        return getId() != null && Objects.equals(getId(), order.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}