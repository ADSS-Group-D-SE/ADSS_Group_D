package DomainLayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all supplier orders in-memory. Provides order history capabilities.
 */
public class OrderManager {

    private Map<Integer, SupplierOrder> orders; // orderId -> SupplierOrder
    private int nextOrderId;

    public OrderManager() {
        this.orders = new HashMap<>();
        this.nextOrderId = 1;
    }

    /**
     * Creates a new order for a given supplier.
     * @param supplierId the supplier to order from
     * @param isUrgent whether this is an urgent order
     * @return the newly created SupplierOrder in PENDING status
     */
    public SupplierOrder createOrder(int supplierId, boolean isUrgent) {
        SupplierOrder order = new SupplierOrder(nextOrderId, supplierId, isUrgent);
        orders.put(nextOrderId, order);
        nextOrderId++;
        return order;
    }

    /**
     * Retrieves an order by its ID.
     */
    public SupplierOrder getOrder(int orderId) {
        return orders.get(orderId);
    }

    public void registerOrder(SupplierOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }
        orders.put(order.getOrderId(), order);
        if (order.getOrderId() >= nextOrderId) {
            nextOrderId = order.getOrderId() + 1;
        }
    }

    public void clear() {
        orders.clear();
        nextOrderId = 1;
    }

    /**
     * Returns the order history for a specific supplier.
     */
    public List<SupplierOrder> getOrdersBySupplier(int supplierId) {
        List<SupplierOrder> result = new ArrayList<>();
        for (SupplierOrder order : orders.values()) {
            if (order.getSupplierId() == supplierId) {
                result.add(order);
            }
        }
        result.sort(Comparator.comparingInt(SupplierOrder::getOrderId));
        return result;
    }

    /**
     * Returns all orders.
     */
    public List<SupplierOrder> getAllOrders() {
        List<SupplierOrder> result = new ArrayList<>(orders.values());
        result.sort(Comparator.comparingInt(SupplierOrder::getOrderId));
        return result;
    }

    /**
     * Returns the total number of orders.
     */
    public int getOrderCount() {
        return orders.size();
    }

    @Override
    public String toString() {
        return "OrderManager{orderCount=" + orders.size() + '}';
    }
}
