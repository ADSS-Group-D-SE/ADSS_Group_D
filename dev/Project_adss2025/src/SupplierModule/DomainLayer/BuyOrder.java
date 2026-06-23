package SupplierModule.DomainLayer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BuyOrder {

    private String buyOrderID;
    private String supId;
    private HashMap<String,Integer> items;
    private DeliveryDaySchedule regularDays;
    private LocalDate nextDeliveryDate;


    private static String generateOrderId(String s) {
        return "BO-"+ s+"-"+ UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    public BuyOrder(String supId, HashMap<String,Integer> items, List<DayOfWeek> days)
    {
        if(items == null || items.isEmpty())
            throw new IllegalArgumentException("Bad item map was sent to buyOrder.");
        if(days == null || days.isEmpty())
            throw new IllegalArgumentException("Bad days list was sent to buyOrder.");
        if(supId == null || supId.isEmpty())
            throw new IllegalArgumentException("Bad supplier ID was sent to buyOrder.");

        VerifyQ(items);

        this.buyOrderID = generateOrderId(supId);
        this.items = items;
        this.regularDays = new DeliveryDaySchedule(days);
        this.nextDeliveryDate = regularDays.ComputeNextOrderDate();
    }

    public boolean isDeliveryTomorrow() {
        return nextDeliveryDate != null
                && nextDeliveryDate.equals(LocalDate.now().plusDays(1));
    }

    public void AddItemToBO(String item,Integer amount)
    {
        if(this.items.containsKey(item))
            throw new RuntimeException("BO:"+this.buyOrderID + " Already have the item:" + items);
        if(amount <= 0)
            throw new RuntimeException("Amount must be positive.");
        if(item == null || item.isEmpty())
            throw new RuntimeException("Bad item catalog number was sent to BO");
        if(isDeliveryTomorrow())
            throw new RuntimeException("Cannot change the buy order one day or less before delivery day.");

        this.items.put(item,amount);
    }

    public void RemoveItemFromBO(String item)
    {
        if(!this.items.containsKey(item))
            throw new RuntimeException("BO:"+this.buyOrderID + " does not have the item:" + items);
        if(isDeliveryTomorrow())
            throw new RuntimeException("Cannot change the buy order one day or less before delivery day.");

        items.remove(item);
    }

    public void UpdateAmount(String item,Integer amount)
    {
        if(!this.items.containsKey(item))
            throw new RuntimeException("BO:"+this.buyOrderID + " does not have the item:" + items);
        if(amount <= 0)
            throw new RuntimeException("Amount must be positive.");
        if(isDeliveryTomorrow())
            throw new RuntimeException("Cannot change the buy order one day or less before delivery day.");

        this.items.put(item,amount);
    }

    public void ScheduleNextDelivery()
    {
        this.nextDeliveryDate = regularDays.ComputeNextOrderDate();
    }

    public void AddRegularDay(DayOfWeek d)
    {
        this.regularDays.addDay(d);
    }

    public void RemoveRegularDay(DayOfWeek d)
    {
        DeliveryDaySchedule temp = new DeliveryDaySchedule(regularDays.getDays());
        temp.removeDay(d);
        if(temp.getDays().isEmpty())
            throw new RuntimeException("Cannot remove regular day from BO:"+buyOrderID+" as a buy order cannot have no delivery days.");

        this.regularDays.removeDay(d);
    }

    public void VerifyQ(HashMap<String,Integer> amounts)
    {
        for(Map.Entry<String,Integer> en: amounts.entrySet())
        {
            if(en.getValue() <=0)
                throw new RuntimeException("Bad amount was sent to buy order, all amounts must be positive.");
        }
    }

    public String Summary()
    {
        return "Buy Order:" +this.buyOrderID + "\nItems:" + items.toString() +"\nRegular days:" + this.regularDays.toString()
                + "\nNext Delivery Date:"+this.nextDeliveryDate.toString();
    }

    public String getBuyOrderID() {
        return buyOrderID;
    }

    public String getSupId() {
        return supId;
    }

    public HashMap<String, Integer> getItems() {
        return new HashMap<>(items);
    }

    public DeliveryDaySchedule getRegularDays() {
        return new DeliveryDaySchedule(regularDays.getDays());
    }

    public LocalDate getNextDeliveryDate(){return this.nextDeliveryDate;}
}
