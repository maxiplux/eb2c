package app.quantun.eb2c.repository;

import app.quantun.eb2c.model.contract.value.OrderStatus;
import app.quantun.eb2c.model.entity.bussines.Order;
import app.quantun.eb2c.model.entity.bussines.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entities.
 * This interface provides methods for CRUD operations on orders.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    /**
     * Find an order by its order number.
     *
     * @param orderNumber the order number
     * @return an Optional containing the Order if found, otherwise empty
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find all orders for a specific organization.
     *
     * @param organization the organization
     * @return a list of orders
     */
    List<Order> findByOrganization(Organization organization);

    /**
     * Find all orders for a specific organization with pagination.
     *
     * @param organization the organization
     * @param pageable     the pagination information
     * @return a page of orders
     */
    Page<Order> findByOrganization(Organization organization, Pageable pageable);

    /**
     * Find all orders with a specific status.
     *
     * @param status the order status
     * @return a list of orders
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find all orders with a specific status with pagination.
     *
     * @param status   the order status
     * @param pageable the pagination information
     * @return a page of orders
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Find all orders placed between the specified dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return a list of orders
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all orders placed between the specified dates with pagination.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param pageable  the pagination information
     * @return a page of orders
     */
    Page<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find all orders for a specific organization with a specific status.
     *
     * @param organization the organization
     * @param status       the order status
     * @return a list of orders
     */
    List<Order> findByOrganizationAndStatus(Organization organization, OrderStatus status);

    /**
     * Find all orders for a specific organization with a specific status with pagination.
     *
     * @param organization the organization
     * @param status       the order status
     * @param pageable     the pagination information
     * @return a page of orders
     */
    Page<Order> findByOrganizationAndStatus(Organization organization, OrderStatus status, Pageable pageable);
}