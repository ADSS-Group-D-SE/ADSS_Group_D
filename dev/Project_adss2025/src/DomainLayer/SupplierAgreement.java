package DomainLayer;

import java.util.ArrayList;
import java.util.List;

public class SupplierAgreement {

    public enum SupplyMethod {
        FIXED_DAYS,
        ON_ORDER,
        SELF_PICKUP
    }

    private SupplyMethod supplyMethod;
    private DeliveryDaySchedule deliveryDaySchedule; // Relevant only for FIXED_DAYS
    private int deliveryDays; // Estimated days from order to delivery (relevant for ON_ORDER)
    private List<SupplierItem> items; // Items included in this agreement
    private boolean frozen; // Whether this agreement is frozen (no modifications allowed)

    public SupplierAgreement(SupplyMethod supplyMethod) {
        this.supplyMethod = supplyMethod;
        this.deliveryDaySchedule = new DeliveryDaySchedule();
        this.deliveryDays = 0;
        this.items = new ArrayList<>();
        this.frozen = false;
    }

    public void addItem(SupplierItem item) {
        ensureMutable();
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        items.add(item);
    }

    public void removeItem(SupplierItem item) {
        ensureMutable();
        items.remove(item);
    }

    public SupplierItem findItemByCatalogNumber(int catalogNumber) {
        for (SupplierItem item : items) {
            if (item.getCatalogNumber() == catalogNumber) {
                return item;
            }
        }
        return null;
    }

    public SupplierItem findItemByInternalId(int internalItemId) {
        for (SupplierItem item : items) {
            if (item.getInternalItemId() == internalItemId) {
                return item;
            }
        }
        return null;
    }

    public void addFixedSupplyDay(int day) {
        ensureMutable();
        deliveryDaySchedule.addDay(day);
    }

    public void removeFixedSupplyDay(int day) {
        ensureMutable();
        deliveryDaySchedule.removeDay(day);
    }

    public SupplyMethod getSupplyMethod() {
        return supplyMethod;
    }

    public List<Integer> getFixedSupplyDays() {
        return deliveryDaySchedule.getDays();
    }

    public DeliveryDaySchedule getDeliveryDaySchedule() {
        return new DeliveryDaySchedule(deliveryDaySchedule.getDays());
    }

    public int getDeliveryDays() {
        return deliveryDays;
    }

    public List<SupplierItem> getItems() {
        return new ArrayList<>(items);
    }

    public void setSupplyMethod(SupplyMethod supplyMethod) {
        ensureMutable();
        this.supplyMethod = supplyMethod;
    }

    public void setDeliveryDays(int deliveryDays) {
        ensureMutable();
        if (deliveryDays < 0) {
            throw new IllegalArgumentException("Delivery days cannot be negative.");
        }
        this.deliveryDays = deliveryDays;
    }

    /**
     * Throws if the agreement is frozen and therefore cannot be changed.
     */
    public void ensureMutable() {
        if (frozen) {
            throw new IllegalStateException("Cannot modify a frozen agreement.");
        }
    }

    /**
     * Freezes this agreement, preventing any item modifications.
     */
    public void freeze() {
        this.frozen = true;
    }

    /**
     * Unfreezes this agreement, allowing item modifications again.
     */
    public void unfreeze() {
        this.frozen = false;
    }

    /**
     * Returns whether this agreement is currently frozen.
     */
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public String toString() {
        return "SupplierAgreement{" +
                "supplyMethod=" + supplyMethod +
                ", deliveryDaySchedule=" + deliveryDaySchedule +
                ", deliveryDays=" + deliveryDays +
                ", itemCount=" + items.size() +
                ", frozen=" + frozen +
                '}';
    }
}
