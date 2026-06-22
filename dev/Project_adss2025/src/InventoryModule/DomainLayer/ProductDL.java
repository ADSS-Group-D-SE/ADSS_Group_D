package InventoryModule.DomainLayer;

import CrossCuttingPackage.productDTO;
import CrossCuttingPackage.promotionDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDL {
    private final static int MAIN = 0;
    private final static int SUB = 1;
    private final static int SUBSUB = 2;
    private String name;
    private final String catalog_number;

    private Warehouse warehouse;
    private ShelfLocation location;

    private HashMap<Integer, String> tags;
    private final String manufacturer;

    private int amount_on_shelves;
    private int amount_on_stock;

    private List<Promotion> product_discounts;
    private double price_to_consumer;
    private double price_to_supply;
    private double supplier_discount;

    private int minAmountAlert;

    public ProductDL(String name, String cat, String main_id, String sub_id, String subsub_id,
                     String warehouseName, String loc, String manu,
                     int on_shelves, int on_stock, double price_to_consumer, double price_to_supply, int minAmountAlert)
    {
        if(main_id == null || sub_id == null  || main_id.isEmpty() || sub_id.isEmpty() || (subsub_id != null && subsub_id.isEmpty()))
            throw new IllegalArgumentException("Product construction:" + cat + " ,Bad categories was sent.");

        this.tags = new HashMap<>();
        this.tags.put(MAIN, main_id);
        this.tags.put(SUB, sub_id);
        this.tags.put(SUBSUB, subsub_id);

        setName(name);
        if (cat == null) {
            throw new IllegalArgumentException("Catalog number cannot be null or empty.");
        }
        this.catalog_number = cat;

        setWarehouse(warehouseName);
        setLocation(loc);

        if (manu == null || manu.trim().isEmpty()) {
            throw new IllegalArgumentException("manufacturer cannot be null or empty.");
        }
        this.manufacturer = manu;

        setAmount_on_shelves(on_shelves);
        setAmount_on_stock(on_stock);
        setPrice_to_consumer(price_to_consumer);
        setPrice_to_supply(price_to_supply);

        setMinAmountAlert(minAmountAlert);

        this.supplier_discount = 0;

        this.product_discounts = new ArrayList<>();
    }

    public ProductDL(productDTO p) {

        this.tags = new HashMap<>();

        this.tags.put(0, p.getMain_category_id());
        this.tags.put(1, p.getSub_category_id());
        this.tags.put(2, p.getSubsub_category_id());

        this.name = p.getName();
        this.catalog_number = p.getCatalogNumber();

        this.warehouse = new Warehouse(p.getWarehouseName());
        this.location = new ShelfLocation(p.getShelfLocation());

        this.manufacturer = p.getManufacturer();

        this.amount_on_shelves = p.getAmountOnShelves();
        this.amount_on_stock = p.getAmountOnStock();

        this.price_to_consumer = p.getPriceToConsumer();
        this.price_to_supply = p.getPriceToSupply();
        this.supplier_discount = p.getSupplierDiscount();

        this.minAmountAlert = p.getMinAmountAlert();

        this.product_discounts = new ArrayList<>();
        if (p.getProductDiscounts() != null) {
            for (promotionDTO dto : p.getProductDiscounts()) {
                this.product_discounts.add(new Promotion(dto));
            }
        }
    }

    public productDTO toDTO() {


        String whName =this.warehouse.getName() ;
        String shelfLoc = this.location.toString();

        return new productDTO(
                this.name,
                this.catalog_number,
                this.tags.get(0),
                this.tags.get(1),
                this.tags.get(2),
                whName,
                shelfLoc,
                this.manufacturer,
                this.amount_on_shelves,
                this.amount_on_stock,
                convertPromotionsToDTO(this.product_discounts),
                this.price_to_consumer,
                this.price_to_supply,
                this.supplier_discount,
                this.minAmountAlert
        );
    }

    private static List<promotionDTO> convertPromotionsToDTO(List<Promotion> promotions) {
        List<promotionDTO> dtos = new ArrayList<>();
        if (promotions != null) {
            for (Promotion p : promotions) {
                dtos.add(p.toDTO());
            }
        }
        return dtos;
    }
    /*
     Method for testing, allows "purchasing" and changing amounts on shelves and stocks.
     input a positive amount for amount decrease, negative for increase.
     */
    public void Purchase(int shelvesChange, int stockChange) {
        int newShelves = this.amount_on_shelves - shelvesChange;
        int newStock = this.amount_on_stock - stockChange;

        if (newShelves < 0 || newStock < 0) {
            throw new IllegalArgumentException("Inventory Error: Catalog " + this.catalog_number +
                    ". Operation would result in negative inventory (Shelves: " + newShelves + ", Stock: " + newStock + ")");
        }

        this.amount_on_shelves = newShelves;
        this.amount_on_stock = newStock;
    }

    /*
    Method that returns whether the product is in low stock range.
     */
    public boolean isInWarningRange()
    {
        return this.amount_on_shelves + amount_on_stock <= this.minAmountAlert;
    }


    public void addPromotion(Promotion promotion) {
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion cannot be null");
        }
        this.product_discounts.add(promotion);
    }

    public double getTotalProductDiscount() {
        double priceMultiplier = 1.0;

        for (Promotion p : product_discounts) {
            if (p.isActiveNow()) {
                priceMultiplier *= (1 - p.getDiscountPercentage());
            }
        }

        return 1 - priceMultiplier;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse cannot be null.");
        }
        this.warehouse = new Warehouse(warehouse);
    }

    public ShelfLocation getLocation() {
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

    public double getPrice_to_supplyDiscounted()
    {
        return price_to_supply * (1 - supplier_discount);
    }

    public double getSupplier_discount() {
        return supplier_discount;
    }

    public double getPrice_to_consumer() {
        return price_to_consumer;
    }


    public List<Promotion> getProduct_discounts() {
        return product_discounts;
    }



    public void setSupplier_discount(double supplier_discount) {
        if(supplier_discount < 0 || supplier_discount > 1){
            throw new IllegalArgumentException("Supplier discount can be between 0-1  (50% --> 0.5) ");
        }
        this.supplier_discount = supplier_discount;
    }

    public void setLocation(String location) {
        this.location = new ShelfLocation(location);
    }

    public void setAmount_on_shelves(int amount_on_shelves) {
        if(amount_on_shelves < 0){
            throw new IllegalArgumentException("amount can't be negative");
        }
        this.amount_on_shelves = amount_on_shelves;
    }

    public void setAmount_on_stock(int amount_on_stock) {
        if(amount_on_stock < 0){
            throw new IllegalArgumentException("amount can't be negative");
        }
        this.amount_on_stock = amount_on_stock;
    }

    public void setPrice_to_consumer(double price_to_consumer) {
        if(price_to_consumer < 0){
            throw new IllegalArgumentException("price can't be negative");
        }
        this.price_to_consumer = price_to_consumer;
    }

    public void setPrice_to_supply(double price_to_supply) {
        if(price_to_supply < 0){
            throw new IllegalArgumentException("price can't be negative");
        }
        this.price_to_supply = price_to_supply;
    }

    public void setName(String name) {
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("name can't be empty");
        }
        this.name = name;
    }

    public void setMinAmountAlert(int minAmountAlert) {
        if(minAmountAlert < 0){
            throw new IllegalArgumentException("Min Amount can't be negative");
        }
        this.minAmountAlert = minAmountAlert;
    }


    public void setProduct_discounts(List<Promotion> product_discounts) {
        if (product_discounts == null) {
            throw new IllegalArgumentException("Promotions list cannot be null");
        }
        this.product_discounts = product_discounts;
    }

    public int getMinAmountAlert() {
        return minAmountAlert;
    }

    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .toFormatter();
    public void removeExpiredPromotions() {
        if (this.product_discounts == null || this.product_discounts.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();

        this.product_discounts.removeIf(promo -> {
            String endDateStr = String.valueOf(promo.getEndDate());

            if (endDateStr == null || endDateStr.isEmpty()) {
                return false;
            }
            LocalDate endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
            return endDate.isBefore(today);
        });
    }
}