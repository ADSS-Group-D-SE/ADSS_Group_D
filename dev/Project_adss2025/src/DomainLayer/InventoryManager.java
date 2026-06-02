package DomainLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {

    private Map<Integer, InventoryItem> items;

    public InventoryManager() {
        this.items = new HashMap<>();
    }

    public InventoryItem addInventoryItem(int internalItemId, String name, int warehouseQuantity,
            int shelfQuantity, int minimumQuantity) {
        if (items.containsKey(internalItemId)) {
            throw new IllegalArgumentException("Inventory item already exists: " + internalItemId);
        }
        InventoryItem item = new InventoryItem(internalItemId, name, warehouseQuantity,
                shelfQuantity, minimumQuantity);
        items.put(internalItemId, item);
        return item;
    }

    public void registerInventoryItem(InventoryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Inventory item cannot be null.");
        }
        items.put(item.getInternalItemId(), item);
    }

    public InventoryItem getInventoryItem(int internalItemId) {
        return items.get(internalItemId);
    }

    public List<InventoryItem> getAllInventoryItems() {
        return new ArrayList<>(items.values());
    }

    public List<InventoryItem> getItemsBelowMinimum() {
        List<InventoryItem> result = new ArrayList<>();
        for (InventoryItem item : items.values()) {
            if (item.isBelowMinimum()) {
                result.add(item);
            }
        }
        return result;
    }

    public void updateStock(int internalItemId, int warehouseDelta, int shelfDelta) {
        InventoryItem item = getInventoryItemOrThrow(internalItemId);
        item.changeStock(warehouseDelta, shelfDelta);
    }

    public void addExpectedIncomingQuantity(int internalItemId, int quantity) {
        InventoryItem item = getInventoryItemOrThrow(internalItemId);
        item.addExpectedIncomingQuantity(quantity);
    }

    public void receiveIncomingQuantity(int internalItemId, int quantity) {
        InventoryItem item = getInventoryItemOrThrow(internalItemId);
        item.receiveExpectedIncomingQuantity(quantity);
    }

    public int getInventoryItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    private InventoryItem getInventoryItemOrThrow(int internalItemId) {
        InventoryItem item = items.get(internalItemId);
        if (item == null) {
            throw new IllegalArgumentException("Inventory item not found: " + internalItemId);
        }
        return item;
    }
}
