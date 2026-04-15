package DomainLayer;

import java.util.ArrayList;
import java.util.List;

public class SupplierItem {

    private int catalogNumber; // Supplier's catalog number for this item
    private int internalItemId; // Company's internal item ID
    private String itemDescription; // Description of the item
    private double price; // Base price per unit
    private String manufacturer; // The manufacturer/company the item belongs to
    private List<QuantityDiscount> quantityDiscounts; // Discount tiers for bulk purchases

    public SupplierItem(int catalogNumber, int internalItemId, String itemDescription,
            double price, String manufacturer) {
        this.catalogNumber = catalogNumber;
        this.internalItemId = internalItemId;
        this.itemDescription = itemDescription;
        this.price = price;
        this.manufacturer = manufacturer;
        this.quantityDiscounts = new ArrayList<>();
    }

    public void addQuantityDiscount(QuantityDiscount discount) {
        if (discount == null) {
            throw new IllegalArgumentException("Discount cannot be null.");
        }
        quantityDiscounts.add(discount);
    }

    public void removeQuantityDiscount(QuantityDiscount discount) {
        quantityDiscounts.remove(discount);
    }

    /**
     * Returns the applicable discount percentage for a given order quantity.
     * Finds the highest minQuantity tier that the ordered quantity satisfies.
     */
    public double getApplicableDiscount(int quantity) {
        double bestDiscount = 0;
        for (QuantityDiscount qd : quantityDiscounts) {
            if (quantity >= qd.getMinQuantity() && qd.getDiscountPercent() > bestDiscount) {
                bestDiscount = qd.getDiscountPercent();
            }
        }
        return bestDiscount;
    }

    /**
     * Calculates the effective price per unit after applying the best quantity
     * discount.
     */
    public double getEffectivePrice(int quantity) {
        double discount = getApplicableDiscount(quantity);
        return price * (1 - discount / 100.0);
    }

    public int getCatalogNumber() {
        return catalogNumber;
    }

    public int getInternalItemId() {
        return internalItemId;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public double getPrice() {
        return price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public List<QuantityDiscount> getQuantityDiscounts() {
        return new ArrayList<>(quantityDiscounts);
    }

    public void setCatalogNumber(int catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public void setInternalItemId(int internalItemId) {
        this.internalItemId = internalItemId;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "SupplierItem{" +
                "catalogNumber=" + catalogNumber +
                ", internalItemId=" + internalItemId +
                ", description='" + itemDescription + '\'' +
                ", price=" + price +
                ", manufacturer='" + manufacturer + '\'' +
                ", discountTiers=" + quantityDiscounts.size() +
                '}';
    }
}
