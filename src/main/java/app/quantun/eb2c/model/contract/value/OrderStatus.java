package app.quantun.eb2c.model.contract.value;

/**
 * Enumeration representing the different statuses an order can have.
 */
public enum OrderStatus {
    /**
     * Order has been created but not yet processed.
     */
    PENDING,

    /**
     * Order has been approved and is being processed.
     */
    APPROVED,

    /**
     * Order is currently being processed for shipping.
     */
    PROCESSING,

    /**
     * Order has been shipped to the customer.
     */
    SHIPPED,

    /**
     * Order has been delivered to the customer.
     */
    DELIVERED,

    /**
     * Order has been completed.
     */
    COMPLETED,

    /**
     * Order has been cancelled.
     */
    CANCELLED,

    /**
     * Order is on hold.
     */
    ON_HOLD,

    /**
     * Payment for the order has failed.
     */
    PAYMENT_FAILED,

    /**
     * Order is being returned.
     */
    RETURNED
}