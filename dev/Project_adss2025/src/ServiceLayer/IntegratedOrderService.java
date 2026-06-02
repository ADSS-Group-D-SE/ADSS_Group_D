package ServiceLayer;

import DataAccessLayer.SuperLeeDataStore;
import DomainLayer.InventoryItem;
import DomainLayer.InventoryManager;
import DomainLayer.Supplier;
import DomainLayer.SupplierAgreement;
import DomainLayer.SupplierAgreement.SupplyMethod;
import DomainLayer.SupplierItem;
import DomainLayer.SupplierOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IntegratedOrderService {

    private SupplierService supplierService;
    private InventoryManager inventoryManager;
    private SuperLeeDataStore dataStore;

    public IntegratedOrderService(SupplierService supplierService, InventoryManager inventoryManager,
            SuperLeeDataStore dataStore) {
        this.supplierService = supplierService;
        this.inventoryManager = inventoryManager;
        this.dataStore = dataStore;
    }

    public InventoryItem addInventoryItem(int internalItemId, String name, int warehouseQuantity,
            int shelfQuantity, int minimumQuantity) {
        InventoryItem item = inventoryManager.addInventoryItem(internalItemId, name,
                warehouseQuantity, shelfQuantity, minimumQuantity);
        save();
        return item;
    }

    public void updateStock(int internalItemId, int warehouseDelta, int shelfDelta) {
        inventoryManager.updateStock(internalItemId, warehouseDelta, shelfDelta);
        save();
    }

    public void receiveIncomingQuantity(int internalItemId, int quantity) {
        inventoryManager.receiveIncomingQuantity(internalItemId, quantity);
        save();
    }

    public InventoryItem getInventoryItem(int internalItemId) {
        return inventoryManager.getInventoryItem(internalItemId);
    }

    public List<InventoryItem> getAllInventoryItems() {
        return inventoryManager.getAllInventoryItems();
    }

    public List<InventoryItem> getItemsBelowMinimum() {
        return inventoryManager.getItemsBelowMinimum();
    }

    public SupplierOrder createAutomaticShortageOrder(int internalItemId, boolean isUrgent) {
        InventoryItem item = requireInventoryItem(internalItemId);
        int requiredQuantity = item.quantityNeededToExceedMinimum();
        if (requiredQuantity <= 0) {
            throw new IllegalArgumentException("Inventory item " + internalItemId
                    + " is not below its minimum after expected incoming orders.");
        }
        SupplierOrder order = supplierService.createShortageOrder(internalItemId, requiredQuantity, isUrgent);
        inventoryManager.addExpectedIncomingQuantity(internalItemId, requiredQuantity);
        save();
        return order;
    }

    public SupplierOrder createPeriodicOrderOneDayBeforeDelivery(int supplierId) {
        Supplier supplier = supplierService.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        SupplierAgreement agreement = supplier.getAgreement();
        if (agreement == null || agreement.getSupplyMethod() != SupplyMethod.FIXED_DAYS) {
            throw new IllegalArgumentException("Periodic orders require a fixed-days supplier agreement.");
        }
        int tomorrow = toProjectDay(LocalDate.now().plusDays(1).getDayOfWeek());
        if (!agreement.getFixedSupplyDays().contains(tomorrow)) {
            throw new IllegalStateException("Periodic order may be updated only one day before a fixed delivery day.");
        }

        Map<Integer, Integer> quantitiesByInternalItem = new LinkedHashMap<>();
        for (SupplierItem agreementItem : agreement.getItems()) {
            InventoryItem inventoryItem = inventoryManager.getInventoryItem(agreementItem.getInternalItemId());
            if (inventoryItem == null) {
                continue;
            }
            int requiredQuantity = inventoryItem.quantityNeededToExceedMinimum();
            if (requiredQuantity > 0) {
                quantitiesByInternalItem.put(agreementItem.getInternalItemId(), requiredQuantity);
            }
        }

        if (quantitiesByInternalItem.isEmpty()) {
            throw new IllegalStateException("No inventory shortages require a periodic order for this supplier.");
        }

        SupplierOrder order = supplierService.createPeriodicOrderForSupplier(supplierId, quantitiesByInternalItem);
        for (Map.Entry<Integer, Integer> entry : quantitiesByInternalItem.entrySet()) {
            inventoryManager.addExpectedIncomingQuantity(entry.getKey(), entry.getValue());
        }
        save();
        return order;
    }

    public Supplier findBestSupplierForInventoryShortage(int internalItemId) {
        InventoryItem item = requireInventoryItem(internalItemId);
        int requiredQuantity = item.quantityNeededToExceedMinimum();
        if (requiredQuantity <= 0) {
            throw new IllegalArgumentException("Inventory item " + internalItemId + " is not below minimum.");
        }
        return supplierService.findBestSupplierForItem(internalItemId, requiredQuantity);
    }

    private InventoryItem requireInventoryItem(int internalItemId) {
        InventoryItem item = inventoryManager.getInventoryItem(internalItemId);
        if (item == null) {
            throw new IllegalArgumentException("Inventory item not found: " + internalItemId);
        }
        return item;
    }

    private int toProjectDay(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case SUNDAY: return 1;
            case MONDAY: return 2;
            case TUESDAY: return 3;
            case WEDNESDAY: return 4;
            case THURSDAY: return 5;
            case FRIDAY: return 6;
            case SATURDAY: return 7;
            default: return 1;
        }
    }

    private void save() {
        if (dataStore != null) {
            dataStore.save(supplierService, inventoryManager);
        }
    }
}
