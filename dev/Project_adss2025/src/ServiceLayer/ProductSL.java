package ServiceLayer;

import DomainLayer.CategoryDL;
import DomainLayer.ProductDL;

import java.util.ArrayList;
import java.util.List;


public class ProductSL {
    public String name;
    public String catalog_number;
    public String location;
    public String manufacturer;
    public int amount_on_shelves;
    public int amount_on_stock;
    public double price_to_consumer;
    public double price_to_supply;
    public double product_discount;
    public double supplier_discount;
    public int minAmountAlert;

    public String main_category_id;
    public String sub_category_id;
    public String subsub_category_id;

    public ProductSL(ProductDL dl) {
        this.name = dl.getName();
        this.catalog_number = dl.getCatalog_number();
        this.location = dl.getLocation();
        this.manufacturer = dl.getManufacturer();
        this.amount_on_shelves = dl.getAmount_on_shelves();
        this.amount_on_stock = dl.getAmount_on_stock();
        this.price_to_consumer = dl.getPrice_to_consumer();
        this.price_to_supply = dl.getPrice_to_supply();
        this.product_discount = dl.getProduct_discount();
        this.supplier_discount = dl.getSupplier_discount();
        this.minAmountAlert = dl.getMinAmountAlert();

        this.main_category_id = dl.getMain_category_id();
        this.sub_category_id = dl.getSub_category_id();
        this.subsub_category_id = dl.getSubsub_category_id();
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