package CrossCuttingPackage;

import InventoryModule.DomainLayer.ProductDL;
import InventoryModule.DomainLayer.Promotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class productDTO {
    private final String name;
    private final String catalogNumber;

    private final String warehouseName;
    private final String shelfLocation;

    private final Map<Integer, String> tags;
    private final String manufacturer;

    private final int amountOnShelves;
    private final int amountOnStock;

    private final List<promotionDTO> productDiscounts;

    private final double priceToConsumer;
    private final double priceToSupply;
    private final double supplierDiscount;
    private final int minAmountAlert;


    public productDTO(
            String name,
            String catalogNumber,
            String mainCategory,
            String subCategory,
            String subSubCategory,
            String warehouseName,
            String shelfLocation,
            String manufacturer,
            int amountOnShelves,
            int amountOnStock,
            List<promotionDTO> productDiscounts,
            double priceToConsumer,
            double priceToSupply,
            double supplierDiscount,
            int minAmountAlert) {

        this.name = name;
        this.catalogNumber = catalogNumber;

        this.tags = new HashMap<>();
        this.tags.put(0, mainCategory);
        this.tags.put(1, subCategory);
        this.tags.put(2, subSubCategory);

        this.warehouseName = warehouseName;
        this.shelfLocation = shelfLocation;
        this.manufacturer = manufacturer;
        this.amountOnShelves = amountOnShelves;
        this.amountOnStock = amountOnStock;

        this.productDiscounts = productDiscounts;

        this.priceToConsumer = priceToConsumer;
        this.priceToSupply = priceToSupply;
        this.supplierDiscount = supplierDiscount;
        this.minAmountAlert = minAmountAlert;
    }



    public String getName() { return name; }
    public String getCatalogNumber() { return catalogNumber; }
    public String getWarehouseName() { return warehouseName; }
    public String getShelfLocation() { return shelfLocation; }
    public Map<Integer, String> getTags() { return tags; }
    public String getManufacturer() { return manufacturer; }
    public int getAmountOnShelves() { return amountOnShelves; }
    public int getAmountOnStock() { return amountOnStock; }
    public List<promotionDTO> getProductDiscounts() { return productDiscounts; }
    public double getPriceToConsumer() { return priceToConsumer; }
    public double getPriceToSupply() { return priceToSupply; }
    public double getSupplierDiscount() { return supplierDiscount; }
    public int getMinAmountAlert() { return minAmountAlert; }
    public String getMain_category_id() {
        return tags.get(0);
    }

    public String getSub_category_id() {
        return tags.get(1);
    }

    public String getSubsub_category_id() {
        return tags.get(2);
    }
}

