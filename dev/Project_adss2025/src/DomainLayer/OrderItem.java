package DomainLayer;

/**
 * Represents a single line item within a SupplierOrder.
 * Prices and discounts are snapshotted from the agreement at order time.
 */
public class OrderItem {

    private int catalogNumber;      // Supplier's catalog number (links to SupplierItem)
    private int internalItemId;     // Company's internal item ID
    private String itemDescription;
    private int quantity;
    private double unitPrice;       // Base price per unit at time of order
    private double discountPercent; // Applied discount percentage at time of order

    public OrderItem(int catalogNumber, int internalItemId, String itemDescription,
                     int quantity, double unitPrice, double discountPercent) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
        }
        this.catalogNumber = catalogNumber;
        this.internalItemId = internalItemId;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountPercent = discountPercent;
    }

    /**
     * Returns the total price for this line item after applying the discount.
     */
    public double getTotalPrice() {
        return unitPrice * (1 - discountPercent / 100.0) * quantity;
    }

    /**
     * Returns the effective price per unit after discount.
     */
    public double getEffectiveUnitPrice() {
        return unitPrice * (1 - discountPercent / 100.0);
    }

    public int getCatalogNumber() { return catalogNumber; }
    public int getInternalItemId() { return internalItemId; }
    public String getItemDescription() { return itemDescription; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getDiscountPercent() { return discountPercent; }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "catalogNumber=" + catalogNumber +
                ", internalItemId=" + internalItemId +
                ", description='" + itemDescription + '\'' +
                ", qty=" + quantity +
                ", unitPrice=" + unitPrice +
                ", discount=" + discountPercent + "%" +
                ", total=" + String.format("%.2f", getTotalPrice()) +
                '}';
    }
}
