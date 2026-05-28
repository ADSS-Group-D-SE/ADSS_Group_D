package DomainLayer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an order placed to a supplier based on an agreement.
 */
public class SupplierOrder {

    public enum OrderStatus {
        PENDING,    // Order created but not yet sent
        SENT,       // Order sent to supplier
        DELIVERED,  // Order has been delivered
        CANCELLED   // Order was cancelled
    }

    private int orderId;
    private int supplierId;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private List<OrderItem> items;
    private OrderStatus status;
    private boolean isUrgent;

    public SupplierOrder(int orderId, int supplierId, boolean isUrgent) {
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.orderDate = LocalDate.now();
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.isUrgent = isUrgent;
        this.expectedDeliveryDate = null; // Set when order is finalized
    }

    /**
     * Adds an order item to this order.
     */
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null.");
        }
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to a non-pending order.");
        }
        items.add(item);
    }

    /**
     * Calculates the total price of all items in this order.
     */
    public double getTotalPrice() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    /**
     * Computes and sets the expected delivery date based on the agreement's supply method.
     * If the order is urgent, delivery is set to the next day.
     */
    public void computeExpectedDeliveryDate(SupplierAgreement agreement) {
        if (isUrgent) {
            this.expectedDeliveryDate = orderDate.plusDays(1);
            return;
        }

        switch (agreement.getSupplyMethod()) {
            case FIXED_DAYS:
                this.expectedDeliveryDate = computeNextFixedDay(agreement.getFixedSupplyDays());
                break;
            case ON_ORDER:
                this.expectedDeliveryDate = orderDate.plusDays(agreement.getDeliveryDays());
                break;
            case SELF_PICKUP:
                this.expectedDeliveryDate = orderDate; // Same day pickup
                break;
        }
    }

    /**
     * Finds the next fixed supply day from the order date.
     * Days are 1=Sunday ... 7=Saturday.
     */
    private LocalDate computeNextFixedDay(List<Integer> fixedDays) {
        if (fixedDays == null || fixedDays.isEmpty()) {
            return orderDate.plusDays(7); // Default: one week if no days specified
        }

        LocalDate candidate = orderDate.plusDays(1); // Start from tomorrow
        for (int i = 0; i < 7; i++) {
            int dayOfWeek = convertDayOfWeek(candidate.getDayOfWeek());
            if (fixedDays.contains(dayOfWeek)) {
                return candidate;
            }
            candidate = candidate.plusDays(1);
        }
        return orderDate.plusDays(7); // Fallback
    }

    /**
     * Converts Java's DayOfWeek (MONDAY=1 ... SUNDAY=7) to the project's format (SUNDAY=1 ... SATURDAY=7).
     */
    private int convertDayOfWeek(DayOfWeek dow) {
        switch (dow) {
            case SUNDAY:    return 1;
            case MONDAY:    return 2;
            case TUESDAY:   return 3;
            case WEDNESDAY: return 4;
            case THURSDAY:  return 5;
            case FRIDAY:    return 6;
            case SATURDAY:  return 7;
            default:        return 1;
        }
    }

    /**
     * Marks the order as sent.
     */
    public void send() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be sent.");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot send an order with no items.");
        }
        this.status = OrderStatus.SENT;
    }

    /**
     * Marks the order as delivered.
     */
    public void markDelivered() {
        if (status != OrderStatus.SENT) {
            throw new IllegalStateException("Only sent orders can be marked as delivered.");
        }
        this.status = OrderStatus.DELIVERED;
    }

    /**
     * Cancels the order.
     */
    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered order.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    // Getters
    public int getOrderId() { return orderId; }
    public int getSupplierId() { return supplierId; }
    public LocalDate getOrderDate() { return orderDate; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    public OrderStatus getStatus() { return status; }
    public boolean isUrgent() { return isUrgent; }

    // Allow setting order date for test/mock data purposes
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setExpectedDeliveryDate(LocalDate date) {
        this.expectedDeliveryDate = date;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SupplierOrder{" +
                "orderId=" + orderId +
                ", supplierId=" + supplierId +
                ", date=" + orderDate +
                ", expectedDelivery=" + expectedDeliveryDate +
                ", status=" + status +
                ", urgent=" + isUrgent +
                ", items=" + items.size() +
                ", total=" + String.format("%.2f", getTotalPrice()) +
                '}';
    }
}
