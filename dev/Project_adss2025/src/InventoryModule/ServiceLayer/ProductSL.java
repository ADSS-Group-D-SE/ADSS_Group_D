package InventoryModule.ServiceLayer;

import InventoryModule.DomainLayer.Promotion;
import InventoryModule.DomainLayer.ShelfLocation;
import InventoryModule.DomainLayer.Warehouse;
import InventoryModule.DomainLayer.ProductDL;

import java.util.ArrayList;
import java.util.List;


public class ProductSL {
    public String name;
    public String catalog_number;

    public Warehouse warehouse;
    public ShelfLocation location;

    public String manufacturer;
    public int amount_on_shelves;
    public int amount_on_stock;
    public double price_to_consumer;
    public double price_to_supply;
    public List<Promotion> product_discount;
    public double supplier_discount;
    public int minAmountAlert;

    public String main_category_id;
    public String sub_category_id;
    public String subsub_category_id;

    public ProductSL(ProductDL dl) {
        this.name = dl.getName();
        this.catalog_number = dl.getCatalog_number();

        this.warehouse = dl.getWarehouse();
        this.location = dl.getLocation();

        this.manufacturer = dl.getManufacturer();
        this.amount_on_shelves = dl.getAmount_on_shelves();
        this.amount_on_stock = dl.getAmount_on_stock();
        this.price_to_consumer = dl.getPrice_to_consumer();
        this.price_to_supply = dl.getPrice_to_supply();
        this.product_discount = dl.getProduct_discounts();
        this.supplier_discount = dl.getSupplier_discount();
        this.minAmountAlert = dl.getMinAmountAlert();

        this.main_category_id = dl.getMain_category_id();
        this.sub_category_id = dl.getSub_category_id();
        this.subsub_category_id = dl.getSubsub_category_id();
    }

    public double getTotalProductDiscount() {
        double priceMultiplier = 1.0;

        for (Promotion p : product_discount) {
            if (p.isActiveNow()) {
                priceMultiplier *= (1 - p.getDiscountPercentage());
            }
        }

        return 1 - priceMultiplier;
    }

    public static List<ProductSL> convert (List<ProductDL> toConvert)
    {
        List<ProductSL> res = new ArrayList<>();
        for(ProductDL product:toConvert)
        {
            res.add(new ProductSL(product));
        }
        return res;
    }
}