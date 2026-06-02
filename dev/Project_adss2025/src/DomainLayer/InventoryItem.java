package DomainLayer;

/**
 * Minimal inventory-side product record used by the suppliers integration.
 */
public class InventoryItem {

    private int internalItemId;
    private String name;
    private int warehouseQuantity;
    private int shelfQuantity;
    private int minimumQuantity;
    private int expectedIncomingQuantity;

    public InventoryItem(int internalItemId, String name, int warehouseQuantity,
            int shelfQuantity, int minimumQuantity) {
        if (internalItemId <= 0) {
            throw new IllegalArgumentException("Internal item ID must be positive.");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Inventory item name cannot be empty.");
        }
        validateNonNegative(warehouseQuantity, "Warehouse quantity");
        validateNonNegative(shelfQuantity, "Shelf quantity");
        validateNonNegative(minimumQuantity, "Minimum quantity");
        this.internalItemId = internalItemId;
        this.name = name;
        this.warehouseQuantity = warehouseQuantity;
        this.shelfQuantity = shelfQuantity;
        this.minimumQuantity = minimumQuantity;
        this.expectedIncomingQuantity = 0;
    }

    public int getInternalItemId() { return internalItemId; }
    public String getName() { return name; }
    public int getWarehouseQuantity() { return warehouseQuantity; }
    public int getShelfQuantity() { return shelfQuantity; }
    public int getMinimumQuantity() { return minimumQuantity; }
    public int getExpectedIncomingQuantity() { return expectedIncomingQuantity; }

    public int getCurrentQuantity() {
        return warehouseQuantity + shelfQuantity;
    }

    public int getExpectedQuantityAfterOpenOrders() {
        return getCurrentQuantity() + expectedIncomingQuantity;
    }

    public boolean isBelowMinimum() {
        return getExpectedQuantityAfterOpenOrders() <= minimumQuantity;
    }

    public int quantityNeededToExceedMinimum() {
        return Math.max(0, minimumQuantity + 1 - getExpectedQuantityAfterOpenOrders());
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Inventory item name cannot be empty.");
        }
        this.name = name;
    }

    public void setWarehouseQuantity(int warehouseQuantity) {
        validateNonNegative(warehouseQuantity, "Warehouse quantity");
        this.warehouseQuantity = warehouseQuantity;
    }

    public void setShelfQuantity(int shelfQuantity) {
        validateNonNegative(shelfQuantity, "Shelf quantity");
        this.shelfQuantity = shelfQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        validateNonNegative(minimumQuantity, "Minimum quantity");
        this.minimumQuantity = minimumQuantity;
    }

    public void setExpectedIncomingQuantity(int expectedIncomingQuantity) {
        validateNonNegative(expectedIncomingQuantity, "Expected incoming quantity");
        this.expectedIncomingQuantity = expectedIncomingQuantity;
    }

    public void addExpectedIncomingQuantity(int quantity) {
        validateNonNegative(quantity, "Expected incoming quantity");
        this.expectedIncomingQuantity += quantity;
    }

    public void receiveExpectedIncomingQuantity(int quantity) {
        validateNonNegative(quantity, "Received quantity");
        if (quantity > expectedIncomingQuantity) {
            throw new IllegalArgumentException("Cannot receive more than expected incoming quantity.");
        }
        expectedIncomingQuantity -= quantity;
        warehouseQuantity += quantity;
    }

    public void changeStock(int warehouseDelta, int shelfDelta) {
        int newWarehouse = warehouseQuantity + warehouseDelta;
        int newShelf = shelfQuantity + shelfDelta;
        validateNonNegative(newWarehouse, "Warehouse quantity");
        validateNonNegative(newShelf, "Shelf quantity");
        warehouseQuantity = newWarehouse;
        shelfQuantity = newShelf;
    }

    private void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "internalItemId=" + internalItemId +
                ", name='" + name + '\'' +
                ", warehouse=" + warehouseQuantity +
                ", shelf=" + shelfQuantity +
                ", minimum=" + minimumQuantity +
                ", expectedIncoming=" + expectedIncomingQuantity +
                '}';
    }
}
