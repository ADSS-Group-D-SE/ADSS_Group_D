package ServiceLayer;

import DomainLayer.*;
import DomainLayer.SupplierAgreement.SupplyMethod;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SupplierService {

    private SupplierManager supplierManager;
    private OrderManager orderManager;

    public SupplierService(SupplierManager supplierManager, OrderManager orderManager) {
        this.supplierManager = supplierManager;
        this.orderManager = orderManager;
    }

    public Supplier addSupplier(String companyId, String name, String bankAccount, PaymentTerms paymentTerms) {
        return supplierManager.addSupplier(companyId, name, bankAccount, paymentTerms);
    }

    public boolean removeSupplier(int supplierId) {
        return supplierManager.removeSupplier(supplierId);
    }

    public Supplier getSupplier(int supplierId) {
        return supplierManager.getSupplier(supplierId);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierManager.getAllSuppliers();
    }

    public Supplier findByCompanyId(String companyId) {
        return supplierManager.findByCompanyId(companyId);
    }

    public int getSupplierCount() {
        return supplierManager.getSupplierCount();
    }

    public void addContactPerson(int supplierId, String name, String phone, String email) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        supplier.addContactPerson(new ContactPerson(name, phone, email));
    }

    public boolean removeContactPerson(int supplierId, String contactName) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        ContactPerson cp = supplier.findContactByName(contactName);
        if (cp != null) {
            supplier.removeContactPerson(cp);
            return true;
        }
        return false;
    }

    public List<ContactPerson> getContactPersons(int supplierId) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        return supplier.getContactPersons();
    }

    public void createAgreement(int supplierId, SupplyMethod method, List<Integer> fixedDays, int deliveryDays) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        if (supplier.getAgreement() != null) {
            supplier.getAgreement().ensureMutable();
        }
        SupplierAgreement agreement = new SupplierAgreement(method);
        if (method == SupplyMethod.FIXED_DAYS && fixedDays != null) {
            for (int day : fixedDays) {
                agreement.addFixedSupplyDay(day);
            }
        }
        if (method == SupplyMethod.ON_ORDER) {
            agreement.setDeliveryDays(deliveryDays);
        }
        supplier.setAgreement(agreement);
    }

    public SupplierAgreement getAgreement(int supplierId) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        return supplier.getAgreement();
    }

    public void updateSupplyMethod(int supplierId, SupplyMethod method) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.setSupplyMethod(method);
    }

    public void setDeliveryDays(int supplierId, int days) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.setDeliveryDays(days);
    }

    public void addFixedSupplyDay(int supplierId, int day) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.addFixedSupplyDay(day);
    }

    public void removeFixedSupplyDay(int supplierId, int day) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.removeFixedSupplyDay(day);
    }

    public void addItemToAgreement(int supplierId, int catalogNumber, int internalId,
            String description, double price, String manufacturer) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement. Create one first.");
        }
        SupplierItem item = new SupplierItem(catalogNumber, internalId, description, price, manufacturer);
        agreement.addItem(item);
    }

    public boolean removeItemFromAgreement(int supplierId, int catalogNumber) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.ensureMutable();
        SupplierItem item = agreement.findItemByCatalogNumber(catalogNumber);
        if (item != null) {
            agreement.removeItem(item);
            return true;
        }
        return false;
    }

    public List<SupplierItem> getAgreementItems(int supplierId) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        return agreement.getItems();
    }

    public void addQuantityDiscount(int supplierId, int catalogNumber, int minQuantity, double discountPercent) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.ensureMutable();
        SupplierItem item = agreement.findItemByCatalogNumber(catalogNumber);
        if (item == null) {
            throw new IllegalArgumentException("Item with catalog number " + catalogNumber + " not found.");
        }
        item.addQuantityDiscount(new QuantityDiscount(minQuantity, discountPercent));
    }

    public List<Supplier> findSuppliersByItem(int internalItemId) {
        return supplierManager.findSuppliersByItem(internalItemId);
    }

    /**
     * Finds the supplier that gives the best total price for an item and quantity,
     * using the agreement's quantity discounts.
     */
    public Supplier findBestSupplierForItem(int internalItemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        Supplier bestSupplier = null;
        double bestTotalPrice = Double.MAX_VALUE;

        for (Supplier supplier : supplierManager.getAllSuppliers()) {
            SupplierAgreement agreement = supplier.getAgreement();
            if (agreement == null) {
                continue;
            }

            SupplierItem item = agreement.findItemByInternalId(internalItemId);
            if (item == null) {
                continue;
            }

            double totalPrice = item.getEffectivePrice(quantity) * quantity;
            if (totalPrice < bestTotalPrice) {
                bestTotalPrice = totalPrice;
                bestSupplier = supplier;
            }
        }

        return bestSupplier;
    }

    // ══════════════════════════════════════════════════════════
    // Order Management
    // ══════════════════════════════════════════════════════════

    /**
     * Creates a new order for a supplier. The supplier must have an active agreement.
     */
    public SupplierOrder createOrder(int supplierId, boolean isUrgent) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        if (supplier.getAgreement() == null) {
            throw new IllegalArgumentException("Supplier has no agreement. Cannot create order.");
        }
        return orderManager.createOrder(supplierId, isUrgent);
    }

    /**
     * Creates and sends an order directly from a supplier agreement.
     * The map key is the supplier catalog number and the value is the quantity.
     */
    public SupplierOrder createOrderFromAgreement(int supplierId, Map<Integer, Integer> catalogQuantities,
            boolean isUrgent) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        SupplierAgreement agreement = supplier.getAgreement();
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement. Cannot create order.");
        }
        if (catalogQuantities == null || catalogQuantities.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        for (Map.Entry<Integer, Integer> entry : catalogQuantities.entrySet()) {
            int catalogNumber = entry.getKey();
            int quantity = entry.getValue();
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive.");
            }
            if (agreement.findItemByCatalogNumber(catalogNumber) == null) {
                throw new IllegalArgumentException("Item with catalog number " + catalogNumber
                        + " not found in supplier's agreement.");
            }
        }

        SupplierOrder order = orderManager.createOrder(supplierId, isUrgent);
        for (Map.Entry<Integer, Integer> entry : catalogQuantities.entrySet()) {
            addItemToOrder(order.getOrderId(), entry.getKey(), entry.getValue());
        }
        return finalizeOrder(order.getOrderId());
    }

    /**
     * Creates and sends an order from agreement items using Super-Lee internal item IDs.
     */
    public SupplierOrder createOrderFromAgreementByInternalItems(int supplierId,
            Map<Integer, Integer> internalItemQuantities, boolean isUrgent) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        SupplierAgreement agreement = supplier.getAgreement();
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement. Cannot create order.");
        }
        if (internalItemQuantities == null || internalItemQuantities.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        Map<Integer, Integer> catalogQuantities = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : internalItemQuantities.entrySet()) {
            int internalItemId = entry.getKey();
            int quantity = entry.getValue();
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive.");
            }
            SupplierItem item = agreement.findItemByInternalId(internalItemId);
            if (item == null) {
                throw new IllegalArgumentException("Internal item ID " + internalItemId
                        + " not found in supplier's agreement.");
            }
            catalogQuantities.put(item.getCatalogNumber(), quantity);
        }

        return createOrderFromAgreement(supplierId, catalogQuantities, isUrgent);
    }

    /**
     * Flow for inventory shortage: select the cheapest supplier for the item and
     * create a sent order using the supplier agreement and quantity discounts.
     */
    public SupplierOrder createShortageOrder(int internalItemId, int requiredQuantity, boolean isUrgent) {
        Supplier bestSupplier = findBestSupplierForItem(internalItemId, requiredQuantity);
        if (bestSupplier == null) {
            throw new IllegalArgumentException("No supplier found for internal item ID " + internalItemId + ".");
        }

        SupplierItem item = bestSupplier.getAgreement().findItemByInternalId(internalItemId);
        Map<Integer, Integer> catalogQuantities = new LinkedHashMap<>();
        catalogQuantities.put(item.getCatalogNumber(), requiredQuantity);
        return createOrderFromAgreement(bestSupplier.getSupplierId(), catalogQuantities, isUrgent);
    }

    public SupplierOrder createShortageOrder(int internalItemId, int requiredQuantity) {
        return createShortageOrder(internalItemId, requiredQuantity, false);
    }

    /**
     * Flow for a periodic fixed-day supplier order.
     */
    public SupplierOrder createPeriodicOrderForSupplier(int supplierId,
            Map<Integer, Integer> internalItemQuantities) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        SupplierAgreement agreement = supplier.getAgreement();
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement. Cannot create periodic order.");
        }
        if (agreement.getSupplyMethod() != SupplyMethod.FIXED_DAYS) {
            throw new IllegalArgumentException("Periodic orders require a fixed-days supplier agreement.");
        }
        if (agreement.getFixedSupplyDays().isEmpty()) {
            throw new IllegalArgumentException("Fixed-days agreement must include at least one delivery day.");
        }
        return createOrderFromAgreementByInternalItems(supplierId, internalItemQuantities, false);
    }

    /**
     * Adds an item to a pending order. The item must exist in the supplier's agreement.
     * Price and discount are snapshotted from the agreement.
     */
    public void addItemToOrder(int orderId, int catalogNumber, int quantity) {
        SupplierOrder order = orderManager.getOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }
        Supplier supplier = supplierManager.getSupplier(order.getSupplierId());
        if (supplier == null || supplier.getAgreement() == null) {
            throw new IllegalStateException("Supplier or agreement not found for this order.");
        }
        SupplierItem agrItem = supplier.getAgreement().findItemByCatalogNumber(catalogNumber);
        if (agrItem == null) {
            throw new IllegalArgumentException("Item with catalog number " + catalogNumber
                    + " not found in supplier's agreement.");
        }
        double discount = agrItem.getApplicableDiscount(quantity);
        OrderItem orderItem = new OrderItem(
                agrItem.getCatalogNumber(),
                agrItem.getInternalItemId(),
                agrItem.getItemDescription(),
                quantity,
                agrItem.getPrice(),
                discount
        );
        order.addItem(orderItem);
    }

    /**
     * Finalizes a pending order: computes delivery date and sends it.
     */
    public SupplierOrder finalizeOrder(int orderId) {
        SupplierOrder order = orderManager.getOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }
        Supplier supplier = supplierManager.getSupplier(order.getSupplierId());
        if (supplier == null || supplier.getAgreement() == null) {
            throw new IllegalStateException("Supplier or agreement not found.");
        }
        order.computeExpectedDeliveryDate(supplier.getAgreement());
        order.send();
        return order;
    }

    /**
     * Cancels an order.
     */
    public void cancelOrder(int orderId) {
        SupplierOrder order = orderManager.getOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }
        order.cancel();
    }

    /**
     * Marks an order as delivered.
     */
    public void markOrderDelivered(int orderId) {
        SupplierOrder order = orderManager.getOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }
        order.markDelivered();
    }

    /**
     * Gets a specific order by ID.
     */
    public SupplierOrder getOrder(int orderId) {
        return orderManager.getOrder(orderId);
    }

    /**
     * Gets the order history for a specific supplier.
     */
    public List<SupplierOrder> getOrdersBySupplier(int supplierId) {
        return orderManager.getOrdersBySupplier(supplierId);
    }

    /**
     * Gets all orders.
     */
    public List<SupplierOrder> getAllOrders() {
        return orderManager.getAllOrders();
    }

    /**
     * Gets the total number of orders.
     */
    public int getOrderCount() {
        return orderManager.getOrderCount();
    }

    // ══════════════════════════════════════════════════════════
    // Agreement Freeze/Unfreeze
    // ══════════════════════════════════════════════════════════

    /**
     * Freezes a supplier's agreement, preventing modifications.
     */
    public void freezeAgreement(int supplierId) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.freeze();
    }

    /**
     * Unfreezes a supplier's agreement, allowing modifications.
     */
    public void unfreezeAgreement(int supplierId) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.unfreeze();
    }

    /**
     * Checks if a supplier's agreement is frozen.
     */
    public boolean isAgreementFrozen(int supplierId) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        return agreement.isFrozen();
    }
}
