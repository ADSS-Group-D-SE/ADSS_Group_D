package SupplierModule.DomainLayer;

import CrossCuttingPackage.SupplierOrderDTO;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private String supplierId;
    private LocalDate orderDate;
    private List<SupplierItem> items;
    private OrderStatus status;


    public SupplierOrder(String supId,HashMap<String,Integer> quantities,HashMap<String,Double> prices) {

        if(quantities == null || prices == null)
            throw new RuntimeException("Bad quantities or prices sent.");
        if(supId == null || supId.isEmpty())
            throw new IllegalArgumentException("SupplierID must not be null or empty.");

        this.supplierId = supId;
        this.orderDate = LocalDate.now();
        this.orderId = supId +"-" + LocalDateTime.now(); // unique id as seconds are counted.
        this.status = OrderStatus.PENDING;
        this.items = CreateItems(quantities,prices);
    }

    public SupplierOrder(SupplierOrderDTO dto)
    {
        this.supplierId = dto.supplierId;
        this.orderId = dto.orderId;
        this.orderDate = dto.orderDate;
        this.status = dto.status;
        this.items = SupplierItem.convertToItems(dto.items);
    }

    public SupplierOrderDTO toDTO()
    {
        return new SupplierOrderDTO(this.orderId,this.supplierId,this.orderDate,SupplierItem.convertToDTO(this.items),this.status);
    }

    public static OrderStatus MapOrderStatus(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return OrderStatus.PENDING;
            case "SENT":
                return OrderStatus.SENT;
            case "DELIVERED":
                return OrderStatus.DELIVERED;
            case "CANCELLED":
                return OrderStatus.CANCELLED;
            case "PREP":
                return OrderStatus.PREP;
            default:
                throw new RuntimeException("Invalid order status: " + status);
        }
    }

    /**
    Helper method that creates the list of items from agreement, given order quantities.
     */
    private List<SupplierItem> CreateItems(HashMap<String,Integer> quantities,HashMap<String,Double> prices)
    {
        List<SupplierItem> res = new ArrayList<>();
        for(Map.Entry<String,Integer> en:quantities.entrySet())
        {
            String item = en.getKey();
            int amount = en.getValue();
            res.add(new SupplierItem(item,prices.get(item),amount));
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

    public String Summary()
    {
        StringBuilder res = new StringBuilder();
        for(int i=0;i<this.items.size();i++)
        {
            SupplierItem item =items.get(i);
            res.append("Item " + (i+1) +":Catalog:" + item.getCatalogNumber() + ", Amount:" +item.getAmount() +", Total price:" + item.getPrice() +"\n");
        }
        return res.toString();
    }
    // Getters
    public String getOrderId() { return orderId; }
    public LocalDate getOrderDate() { return orderDate; }
    public List<SupplierItem> getItems() { return new ArrayList<>(items); }
    public OrderStatus getStatus() { return status; }

    public String getSupplierId() {
        return supplierId;
    }

    public static List<SupplierOrder> convertToOrders(List<SupplierOrderDTO> list)
    {
        List<SupplierOrder> res = new ArrayList<>();
        for(SupplierOrderDTO o:list)
            res.add(new SupplierOrder(o));
        return res;
    }
    
}