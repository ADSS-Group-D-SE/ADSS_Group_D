package SupplierModule.DomainLayer;

import java.util.*;

public class OrderFacade {

    private HashMap<String,SupplierOrder> orders;

    public OrderFacade()
    {
        this.orders = new HashMap<>();
    }

    public SupplierOrder FindOrderById(String oId)
    {
        if(!this.orders.containsKey(oId))
            throw new NoSuchElementException("OrderFacade:FindOrderById - Order " + oId + " was not located in facade.");
        return this.orders.get(oId);
    }

    public String CreateOrder(HashMap<String,Integer> itemsToQuan,SupplierAgreement agreement)
    {
        SupplierOrder toAdd = new SupplierOrder(itemsToQuan,agreement);
        this.orders.put(toAdd.getOrderId(),toAdd);
        return toAdd.getOrderId();
    }

    public void RemoveOrder(String orderId)
    {
        SupplierOrder toRemove = FindOrderById(orderId);
        this.orders.remove(orderId);
    }

}
