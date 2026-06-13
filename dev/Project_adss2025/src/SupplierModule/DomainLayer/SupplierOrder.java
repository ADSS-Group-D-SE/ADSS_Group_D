package SupplierModule.DomainLayer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an order placed to a supplier based on an agreement.
 */
public class SupplierOrder {

    public enum OrderStatus {
        PENDING,    // Order created but not yet sent
        SENT,       // Order sent to supplier
        DELIVERED,  // Order has been delivered
        CANCELLED,   // Order was cancelled
        PREP
    }

    private String orderId;
    private LocalDate orderDate;
    private List<SupplierItem> items;
    private OrderStatus status;

    private SupplierAgreement agreement; //ref for the agreement.

    public SupplierOrder(HashMap<String,Integer> quantities,SupplierAgreement agreement) {


        if(agreement == null)
            throw new RuntimeException("Agreement must not be null to create an order.");

        this.agreement = agreement;
        this.orderDate = LocalDate.now();
        this.orderId = agreement.getSupId() +"-" +this.orderDate;
        this.status = OrderStatus.PENDING;
        this.items = CreateItems(quantities);
    }

    /**
    Helper method that creates the list of items from agreement, given order quantities.
     */
    private List<SupplierItem> CreateItems(HashMap<String,Integer> quantities)
    {
        List<SupplierItem> res = new ArrayList<>();
        for(Map.Entry<String,Integer> en:quantities.entrySet())
        {
            String item = en.getKey();
            int amount = en.getValue();
            res.add(new SupplierItem(item,agreement.GetEffectivePrice(item,amount),amount));
        }
        return res;
    }

    /**
     * Calculates the total price of all items in this order.
     */
    public double GetTotalPrice() {
        double total = 0;
        for (SupplierItem item : items) {
            total += item.getPrice(); // after discount is applied
        }
        return total;
    }

    /**
     * Marks the order as sent.
     */
    public void SendOrder() {
        if (status != OrderStatus.PREP) {
            throw new IllegalStateException("Only prepared orders can be sent.");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot send an order with no items.");
        }
        this.status = OrderStatus.SENT;
    }

    /**
     * Marks the order as delivered.
     */
    public void MarkDelivered() {
        if (status != OrderStatus.SENT) {
            throw new IllegalStateException("Only sent orders can be marked as delivered.");
        }
        this.status = OrderStatus.DELIVERED;
    }

    /**
     * Cancels the order.
     */
    public void Cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered order.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void Prepare()
    {
        if(this.status != OrderStatus.PENDING)
            throw new RuntimeException("Cannot prepare a non pending order");

        this.status = OrderStatus.PREP;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public LocalDate getOrderDate() { return orderDate; }
    public List<SupplierItem> getItems() { return new ArrayList<>(items); }
    public OrderStatus getStatus() { return status; }

    
}