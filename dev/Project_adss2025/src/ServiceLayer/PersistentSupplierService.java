package ServiceLayer;

import DataAccessLayer.SuperLeeDataStore;
import DomainLayer.InventoryManager;
import DomainLayer.OrderManager;
import DomainLayer.PaymentTerms;
import DomainLayer.Supplier;
import DomainLayer.SupplierAgreement.SupplyMethod;
import DomainLayer.SupplierManager;
import DomainLayer.SupplierOrder;

import java.util.List;
import java.util.Map;

public class PersistentSupplierService extends SupplierService {

    private SuperLeeDataStore dataStore;
    private InventoryManager inventoryManager;

    public PersistentSupplierService(SupplierManager supplierManager, OrderManager orderManager,
            InventoryManager inventoryManager, SuperLeeDataStore dataStore) {
        super(supplierManager, orderManager);
        this.inventoryManager = inventoryManager;
        this.dataStore = dataStore;
    }

    public void saveNow() {
        dataStore.save(this, inventoryManager);
    }

    @Override
    public Supplier addSupplier(String companyId, String name, String bankAccount, PaymentTerms paymentTerms) {
        Supplier supplier = super.addSupplier(companyId, name, bankAccount, paymentTerms);
        saveNow();
        return supplier;
    }

    @Override
    public boolean removeSupplier(int supplierId) {
        boolean removed = super.removeSupplier(supplierId);
        saveNow();
        return removed;
    }

    @Override
    public void addContactPerson(int supplierId, String name, String phone, String email) {
        super.addContactPerson(supplierId, name, phone, email);
        saveNow();
    }

    @Override
    public boolean removeContactPerson(int supplierId, String contactName) {
        boolean removed = super.removeContactPerson(supplierId, contactName);
        saveNow();
        return removed;
    }

    @Override
    public void createAgreement(int supplierId, SupplyMethod method, List<Integer> fixedDays, int deliveryDays) {
        super.createAgreement(supplierId, method, fixedDays, deliveryDays);
        saveNow();
    }

    @Override
    public void updateSupplyMethod(int supplierId, SupplyMethod method) {
        super.updateSupplyMethod(supplierId, method);
        saveNow();
    }

    @Override
    public void setDeliveryDays(int supplierId, int days) {
        super.setDeliveryDays(supplierId, days);
        saveNow();
    }

    @Override
    public void addFixedSupplyDay(int supplierId, int day) {
        super.addFixedSupplyDay(supplierId, day);
        saveNow();
    }

    @Override
    public void removeFixedSupplyDay(int supplierId, int day) {
        super.removeFixedSupplyDay(supplierId, day);
        saveNow();
    }

    @Override
    public void addItemToAgreement(int supplierId, int catalogNumber, int internalId,
            String description, double price, String manufacturer) {
        super.addItemToAgreement(supplierId, catalogNumber, internalId, description, price, manufacturer);
        saveNow();
    }

    @Override
    public boolean removeItemFromAgreement(int supplierId, int catalogNumber) {
        boolean removed = super.removeItemFromAgreement(supplierId, catalogNumber);
        saveNow();
        return removed;
    }

    @Override
    public void addQuantityDiscount(int supplierId, int catalogNumber, int minQuantity, double discountPercent) {
        super.addQuantityDiscount(supplierId, catalogNumber, minQuantity, discountPercent);
        saveNow();
    }

    @Override
    public SupplierOrder createOrder(int supplierId, boolean isUrgent) {
        SupplierOrder order = super.createOrder(supplierId, isUrgent);
        saveNow();
        return order;
    }

    @Override
    public SupplierOrder createOrderFromAgreement(int supplierId, Map<Integer, Integer> catalogQuantities,
            boolean isUrgent) {
        SupplierOrder order = super.createOrderFromAgreement(supplierId, catalogQuantities, isUrgent);
        saveNow();
        return order;
    }

    @Override
    public SupplierOrder createOrderFromAgreementByInternalItems(int supplierId,
            Map<Integer, Integer> internalItemQuantities, boolean isUrgent) {
        SupplierOrder order = super.createOrderFromAgreementByInternalItems(supplierId,
                internalItemQuantities, isUrgent);
        saveNow();
        return order;
    }

    @Override
    public SupplierOrder createShortageOrder(int internalItemId, int requiredQuantity, boolean isUrgent) {
        SupplierOrder order = super.createShortageOrder(internalItemId, requiredQuantity, isUrgent);
        saveNow();
        return order;
    }

    @Override
    public SupplierOrder createPeriodicOrderForSupplier(int supplierId, Map<Integer, Integer> internalItemQuantities) {
        SupplierOrder order = super.createPeriodicOrderForSupplier(supplierId, internalItemQuantities);
        saveNow();
        return order;
    }

    @Override
    public void addItemToOrder(int orderId, int catalogNumber, int quantity) {
        super.addItemToOrder(orderId, catalogNumber, quantity);
        saveNow();
    }

    @Override
    public SupplierOrder finalizeOrder(int orderId) {
        SupplierOrder order = super.finalizeOrder(orderId);
        saveNow();
        return order;
    }

    @Override
    public void cancelOrder(int orderId) {
        super.cancelOrder(orderId);
        saveNow();
    }

    @Override
    public void markOrderDelivered(int orderId) {
        super.markOrderDelivered(orderId);
        saveNow();
    }

    @Override
    public void freezeAgreement(int supplierId) {
        super.freezeAgreement(supplierId);
        saveNow();
    }

    @Override
    public void unfreezeAgreement(int supplierId) {
        super.unfreezeAgreement(supplierId);
        saveNow();
    }
}
