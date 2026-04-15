package DomainLayer;

import java.util.HashMap;
import java.util.Map;

public class ProductDL {

    private final static int MAIN =0;
    private final static int SUB=1;
    private final static int SUBSUB =2;
    private String name;
    private final String catalog_number;

    private HashMap<Integer, CategoryDL> tags;

    private String location;
    private final String manufacturer;

    private int amount_on_shelves;
    private int amount_on_stock;

    private double price_to_consumer;
    private double price_to_supply;
    private double supplier_discount;

    private int minAmountAlert;

    /*
    The productDL class's constructor, inits the fields of the class.
    Checks for nulls in category inputs.
     */

    public ProductDL(String name, String cat, CategoryDL main, CategoryDL sub, CategoryDL subsub, String loc, String manu,
                     int on_shelves, int on_stock, double price_to_consumer, double price_to_supply, double supplier_discount,int minAmountAlert)
    {
        if(main == null || sub == null || subsub ==null)
            throw new IllegalArgumentException("Product construction:" +cat+" ,Bad categories was sent.");

        this.tags = new HashMap<>();
        this.tags.put(MAIN,main);
        this.tags.put(SUB,sub);
        this.tags.put(SUBSUB,subsub); // inits tags map.

        this.name = name;
        this.catalog_number =cat;
        this.location = loc;
        this.manufacturer = manu;
        this.amount_on_shelves = on_shelves;
        this.amount_on_stock = on_stock;
        this.price_to_consumer = price_to_consumer;
        this.price_to_supply = price_to_supply;
        this.supplier_discount = supplier_discount;
        this.minAmountAlert=minAmountAlert;
    }

    /*
     Method for testing, allows "purchasing" and changing amounts on shelves and stocks.
     input a positive amount for amount decrease, negative for increase.
     */
    public void Purchase(int shelves,int stock)
    {
        if(shelves > this.amount_on_shelves || stock > this.amount_on_stock)
            throw new IllegalArgumentException("Product purchase:" + this.catalog_number +" ,amount on shelves or stock is insufficient");
        this.amount_on_shelves -=shelves;
        this.amount_on_stock-=stock;
    }

    /*
    Calculates final price to consumer, based on its categories price discount.
     */
    public double GetFinalPrice()
    {
        double res = this.price_to_consumer;
        for(Map.Entry<Integer, CategoryDL> en:this.tags.entrySet())
        {
            CategoryDL curr = en.getValue();
            if(curr.getDiscount_pre()!= 0)
                res = res*(1- curr.getDiscount_pre()); // applies discount for each category.
        }
        return res;
    }

    /*
    ==================================
    Getters and setters
    ==================================
     */
    public String getName() {
        return name;
    }

    public String getCatalog_number() {
        return catalog_number;
    }

    public CategoryDL getMain_category() {
        return tags.get(MAIN);
    }

    public CategoryDL getSub_category() {
        return tags.get(SUB);
    }

    public CategoryDL getSubsub_category() {
        return tags.get(SUBSUB);
    }

    public String getLocation() {
        return location;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getAmount_on_shelves() {
        return amount_on_shelves;
    }

    public int getAmount_on_stock() {
        return amount_on_stock;
    }

    public double getPrice_to_supply() {
        return price_to_supply;
    }

    public double getSupplier_discount() {
        return supplier_discount;
    }




    public void setSupplier_discount(double supplier_discount) {
        this.supplier_discount = supplier_discount;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAmount_on_shelves(int amount_on_shelves) {
        this.amount_on_shelves = amount_on_shelves;
    }

    public void setAmount_on_stock(int amount_on_stock) {
        this.amount_on_stock = amount_on_stock;
    }

    public void setPrice_to_consumer(double price_to_consumer) {
        this.price_to_consumer = price_to_consumer;
    }

    public void setPrice_to_supply(double price_to_supply) {
        this.price_to_supply = price_to_supply;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMinAmountAlert(int minAmountAlert) {
        this.minAmountAlert = minAmountAlert;
    }
}
