package DomainLayer;

import java.util.HashMap;
import java.util.Map;

public class ProductDL {

    private final static int MAIN =0;
    private final static int SUB=1;
    private final static int SUBSUB =2;
    private String name;
    private final String catalog_number;

    private HashMap<Integer, String> tags;

    private String location;
    private final String manufacturer;

    private int amount_on_shelves;
    private int amount_on_stock;

    private double product_discount;
    private double price_to_consumer;
    private double price_to_supply;
    private double supplier_discount;

    private int minAmountAlert;

    /*
    The productDL class's constructor, inits the fields of the class.
    Checks for nulls in category inputs.
     */

    public ProductDL(String name, String cat, String main_id, String sub_id, String subsub_id, String loc, String manu,
                     int on_shelves, int on_stock, double price_to_consumer, double price_to_supply,int minAmountAlert)
    {
        if(main_id == null || sub_id == null || subsub_id ==null || main_id.isEmpty() || sub_id.isEmpty()||subsub_id.isEmpty())
            throw new IllegalArgumentException("Product construction:" +cat+" ,Bad categories was sent.");

        this.tags = new HashMap<>();
        this.tags.put(MAIN,main_id);
        this.tags.put(SUB,sub_id);
        this.tags.put(SUBSUB,subsub_id); // inits tags map.

        setName(name);
        if (cat == null) {
            throw new IllegalArgumentException("Catalog number cannot be null or empty.");
        }
        this.catalog_number =cat;
        setLocation(loc);

        if (manu == null||manu.trim().isEmpty()) {
            throw new IllegalArgumentException("manufacturer cannot be null or empty.");
        }
        this.manufacturer = manu;

        setAmount_on_shelves(on_shelves);
        setAmount_on_stock(on_stock);
        setPrice_to_consumer(price_to_consumer);
        setPrice_to_supply(price_to_supply);

        setMinAmountAlert(minAmountAlert);

        this.supplier_discount = 0;
        this.product_discount = 0; // initial product amd supplier discount is 0.
    }



    public ProductDL(ProductDL other) {
        this.name = other.name;
        this.catalog_number = other.catalog_number;
        this.location = other.location;
        this.manufacturer = other.manufacturer;
        this.amount_on_shelves = other.amount_on_shelves;
        this.amount_on_stock = other.amount_on_stock;
        this.price_to_consumer = other.price_to_consumer;
        this.price_to_supply = other.price_to_supply;
        this.minAmountAlert = other.minAmountAlert;
        this.product_discount = other.product_discount;
        this.supplier_discount = other.supplier_discount;

        this.tags = new HashMap<>(other.tags);
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
    Method that returns whether the product is in low stock range.
     */
    public boolean isInWarningRange()
    {
        return this.amount_on_shelves+amount_on_stock <= this.minAmountAlert;
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

    public String getMain_category_id() {
        return tags.get(MAIN);
    }

    public String getSub_category_id() {
        return tags.get(SUB);
    }

    public String getSubsub_category_id() {
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

    public double getPrice_to_consumer() {
        return price_to_consumer;
    }

    public double getProduct_discount() {
        return product_discount;
    }




    public void setSupplier_discount(double supplier_discount) {
        if(supplier_discount<0 ||supplier_discount>100){
            throw new IllegalArgumentException("Supplier discount can be between 0-100");
        }
        this.supplier_discount = supplier_discount;
    }

    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }


        String normalizedLocation = location.trim().toUpperCase();


        String locationPattern = "^[A-Z]-[0-9]+$";

        if (!normalizedLocation.matches(locationPattern)) {
            throw new IllegalArgumentException("Invalid location format. Expected format: Letter-Number (e.g., A-12).");
        }
        this.location = normalizedLocation;
    }

    public void setAmount_on_shelves(int amount_on_shelves) {
        if(amount_on_shelves<0){
            throw new IllegalArgumentException("amount can't be negative");
        }
        this.amount_on_shelves = amount_on_shelves;
    }

    public void setAmount_on_stock(int amount_on_stock) {
        if(amount_on_stock<0){
            throw new IllegalArgumentException("amount can't be negative");
        }
        this.amount_on_stock = amount_on_stock;
    }

    public void setPrice_to_consumer(double price_to_consumer) {
        if(price_to_consumer<0){
            throw new IllegalArgumentException("price can't be negative");
        }
        this.price_to_consumer = price_to_consumer;
    }

    public void setPrice_to_supply(double price_to_supply) {
        if(price_to_supply<0){
            throw new IllegalArgumentException("price can't be negative");
        }
        this.price_to_supply = price_to_supply;
    }

    public void setName(String name) {
        if(name==null||name.trim().isEmpty()){
            throw new IllegalArgumentException("name can't be empty");
        }
        this.name = name;
    }

    public void setMinAmountAlert(int minAmountAlert) {
        if(minAmountAlert<0){
            throw new IllegalArgumentException("Min Amount can't be negative");
        }
        this.minAmountAlert = minAmountAlert;
    }



    public void setProduct_discount(double product_discount) {
        if(product_discount<0 ||product_discount>100){
            throw new IllegalArgumentException("Product discount can be only between 0-100");
        }
        this.product_discount = product_discount;
    }

    public int getMinAmountAlert() {
        return minAmountAlert;
    }
}
