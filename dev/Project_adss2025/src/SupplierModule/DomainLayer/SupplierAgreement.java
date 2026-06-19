package SupplierModule.DomainLayer;

import java.util.*;

public class SupplierAgreement {

    private String supId;
    private HashMap<String,Double> catalogsToPrice;
    private HashMap<String ,List<DiscountRule>> discounts;


    public SupplierAgreement(String sid,HashMap<String,Double> items) {
        if (items == null)
            throw new IllegalArgumentException("Item list cannot be null on agreement creation");
        if (sid == null || sid.isEmpty())
            throw new IllegalArgumentException("Supplier ID cannot be null or empty on agreement creation");

        this.supId = sid;
        this.catalogsToPrice = items;
        this.discounts = new HashMap<>();
    }

 /**
 Note - item must be verified in inventory module for existence
  */
    public void AddItem(String item,Double price) {
        if (item == null || item.isEmpty()) {
            throw new IllegalArgumentException("Item catalog number cannot be null or empty.");
        }
        if(price <= 0)
            throw new IllegalArgumentException("Item price must be positive.");

        catalogsToPrice.put(item,price);
    }

    public void RemoveItem(String item) {
        if(!catalogsToPrice.containsKey(item))
            throw new RuntimeException("Cannot remove item from agreement, item not included in agreement");
        catalogsToPrice.remove(item);
    }


    public Double GetItemPrice(String item)
    {
        if(!this.catalogsToPrice.containsKey(item))
            throw new RuntimeException("Cannot get item price - " +item + " not in agreement.");

        return this.catalogsToPrice.get(item);
    }

    public void AddDiscountRule(String item,String promoName, int min,double disc)
    {
        if(!this.catalogsToPrice.containsKey(item))
            throw new RuntimeException("Item " + item + " is not in agreement");

        DiscountRule promo =new DiscountRule(min,disc,promoName);
        discounts.putIfAbsent(item,new ArrayList<>());
        discounts.get(item).add(promo);
    }

    public DiscountRule FindDRule(String item,String promoName)
    {
        List<DiscountRule> l = this.discounts.get(item);
        if(l == null)
            throw new IllegalArgumentException("No discounts for item - " + item);
        for(DiscountRule d: l)
            if(d.getRuleName().equals(promoName))
                return d;
        throw new RuntimeException("Discount rule " + promoName+ " was not found for item " + item);
    }

    public void RemoveDiscountRule(String item,String promoName)
    {
        DiscountRule toRemove = FindDRule(item,promoName);
        this.discounts.get(item).remove(toRemove);
        if(this.discounts.get(item).isEmpty())
            this.discounts.remove(item);

    }

    public DiscountRule FindBestDiscountRule(String item,int q)
    {
        List<DiscountRule> l = this.discounts.get(item);
        if(l == null || l.isEmpty())
            throw new IllegalArgumentException("No discounts for item - " + item);
        DiscountRule max = l.get(0);
        for(DiscountRule d:l)
        {
            if(d.IsApplicable(q) && max.getDiscountPercent() < d.getDiscountPercent())
                max = d;
        }
        return max;
    }

    public Double GetEffectivePrice(String item,int amount)
    {
        DiscountRule d = FindBestDiscountRule(item,amount);
        return GetItemPrice(item) * (1-d.getDiscountPercent());
    }

    public String getSupId() {
        return supId;
    }

    public HashMap<String,Double> getItemsInAgreement() {return this.catalogsToPrice;}

    public Set<String> getItemsCatalogs(){return this.catalogsToPrice.keySet();}

    public void UpdateItemPrice(String item, Double newPrice) {
        if (item == null || item.isEmpty()) {
            throw new IllegalArgumentException("Item catalog number cannot be null or empty.");
        }
        if (newPrice <= 0) {
            throw new IllegalArgumentException("Item price must be positive.");
        }
        if (!this.catalogsToPrice.containsKey(item)) {
            throw new RuntimeException("Cannot update price - item " + item + " is not in the agreement.");
        }

        this.catalogsToPrice.put(item, newPrice);
    }




}