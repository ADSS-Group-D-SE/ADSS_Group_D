package Tests;

import DataAccessLayer.SuperLeeDataStore;
import DomainLayer.InventoryItem;
import DomainLayer.InventoryManager;
import DomainLayer.OrderItem;
import DomainLayer.OrderManager;
import DomainLayer.PaymentTerms;
import DomainLayer.Supplier;
import DomainLayer.SupplierAgreement.SupplyMethod;
import DomainLayer.SupplierItem;
import DomainLayer.SupplierManager;
import DomainLayer.SupplierOrder;
import DomainLayer.SupplierOrder.OrderStatus;
import ServiceLayer.IntegratedOrderService;
import ServiceLayer.PersistentSupplierService;
import ServiceLayer.SupplierService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Assignment2IntegrationTests {

    @Test
    @DisplayName("A2-1: Inventory item detects shortage and required order quantity")
    public void testInventoryShortageCalculation() {
        InventoryItem item = new InventoryItem(101, "Bamba", 20, 10, 100);
        assertTrue(item.isBelowMinimum());
        assertEquals(71, item.quantityNeededToExceedMinimum());

        item.addExpectedIncomingQuantity(71);
        assertFalse(item.isBelowMinimum());
        assertEquals(0, item.quantityNeededToExceedMinimum());
    }

    @Test
    @DisplayName("A2-2: Automatic shortage order chooses the best supplier using quantity discounts")
    public void testAutomaticShortageOrderChoosesBestSupplier() {
        TestContext ctx = newContext();
        Supplier expensiveWithoutDiscount = addSupplierWithItem(ctx.service, "CMP-1", "Cheap Unit Supplier",
                2001, 500, 7.0, 0);
        Supplier discountedBulk = addSupplierWithItem(ctx.service, "CMP-2", "Bulk Discount Supplier",
                1001, 500, 10.0, 50);
        ctx.inventory.addInventoryItem(500, "Shared Item", 0, 0, 100);

        SupplierOrder order = ctx.integratedService.createAutomaticShortageOrder(500, false);

        assertEquals(discountedBulk.getSupplierId(), order.getSupplierId());
        assertNotEquals(expensiveWithoutDiscount.getSupplierId(), order.getSupplierId());
        assertEquals(OrderStatus.SENT, order.getStatus());
        assertEquals(101, order.getItems().get(0).getQuantity());
        assertEquals(50.0, order.getItems().get(0).getDiscountPercent(), 0.001);
        assertEquals(101, ctx.inventory.getInventoryItem(500).getExpectedIncomingQuantity());
    }

    @Test
    @DisplayName("A2-3: Automatic shortage order rejects an item that is not below minimum")
    public void testAutomaticShortageOrderRejectsNonShortage() {
        TestContext ctx = newContext();
        addSupplierWithItem(ctx.service, "CMP-1", "Supplier", 1001, 600, 4.0, 0);
        ctx.inventory.addInventoryItem(600, "Enough Item", 100, 100, 50);

        assertThrows(IllegalArgumentException.class,
                () -> ctx.integratedService.createAutomaticShortageOrder(600, false));
    }

    @Test
    @DisplayName("A2-4: Existing order keeps old price after agreement price changes")
    public void testExistingOrderKeepsAgreementSnapshot() {
        TestContext ctx = newContext();
        Supplier supplier = addSupplierWithItem(ctx.service, "CMP-1", "Supplier", 1001, 700, 10.0, 0);

        Map<Integer, Integer> firstOrderItems = new LinkedHashMap<>();
        firstOrderItems.put(1001, 10);
        SupplierOrder firstOrder = ctx.service.createOrderFromAgreement(
                supplier.getSupplierId(), firstOrderItems, false);

        SupplierItem agreementItem = ctx.service.getAgreementItems(supplier.getSupplierId()).get(0);
        agreementItem.setPrice(50.0);

        SupplierOrder secondOrder = ctx.service.createOrderFromAgreement(
                supplier.getSupplierId(), firstOrderItems, false);

        assertEquals(10.0, firstOrder.getItems().get(0).getUnitPrice(), 0.001);
        assertEquals(50.0, secondOrder.getItems().get(0).getUnitPrice(), 0.001);
    }

    @Test
    @DisplayName("A2-5: Periodic order can be created one day before a fixed delivery day")
    public void testPeriodicOrderDueTomorrow() {
        TestContext ctx = newContext();
        int tomorrow = toProjectDay(LocalDate.now().plusDays(1).getDayOfWeek());
        Supplier supplier = ctx.service.addSupplier("CMP-FIX", "Fixed Supplier", "bank", new PaymentTerms("Net", 30));
        ctx.service.createAgreement(supplier.getSupplierId(), SupplyMethod.FIXED_DAYS, Arrays.asList(tomorrow), 0);
        ctx.service.addItemToAgreement(supplier.getSupplierId(), 3001, 800, "Periodic Item", 12.0, "Maker");
        ctx.inventory.addInventoryItem(800, "Periodic Item", 3, 1, 20);

        SupplierOrder order = ctx.integratedService.createPeriodicOrderOneDayBeforeDelivery(supplier.getSupplierId());

        assertEquals(OrderStatus.SENT, order.getStatus());
        assertEquals(17, order.getItems().get(0).getQuantity());
        assertEquals(17, ctx.inventory.getInventoryItem(800).getExpectedIncomingQuantity());
    }

    @Test
    @DisplayName("A2-6: Periodic order is rejected when tomorrow is not a fixed delivery day")
    public void testPeriodicOrderRejectsWrongDay() {
        TestContext ctx = newContext();
        int tomorrow = toProjectDay(LocalDate.now().plusDays(1).getDayOfWeek());
        int notTomorrow = tomorrow == 7 ? 1 : tomorrow + 1;
        Supplier supplier = ctx.service.addSupplier("CMP-FIX", "Fixed Supplier", "bank", new PaymentTerms("Net", 30));
        ctx.service.createAgreement(supplier.getSupplierId(), SupplyMethod.FIXED_DAYS, Arrays.asList(notTomorrow), 0);
        ctx.service.addItemToAgreement(supplier.getSupplierId(), 3001, 801, "Periodic Item", 12.0, "Maker");
        ctx.inventory.addInventoryItem(801, "Periodic Item", 3, 1, 20);

        assertThrows(IllegalStateException.class,
                () -> ctx.integratedService.createPeriodicOrderOneDayBeforeDelivery(supplier.getSupplierId()));
    }

    @Test
    @DisplayName("A2-7: Data store persists and reloads supplier agreement discounts")
    public void testDataStorePersistsAgreementDiscounts() {
        TestContext ctx = newContext();
        Supplier supplier = addSupplierWithItem(ctx.service, "CMP-1", "Supplier", 1001, 900, 8.0, 25);
        ctx.store.save(ctx.service, ctx.inventory);

        TestContext loaded = newContext(ctx.dbPath);
        loaded.store.loadInto(loaded.supplierManager, loaded.orderManager, loaded.inventory);

        Supplier loadedSupplier = loaded.service.getSupplier(supplier.getSupplierId());
        assertNotNull(loadedSupplier);
        SupplierItem loadedItem = loadedSupplier.getAgreement().findItemByCatalogNumber(1001);
        assertEquals(25.0, loadedItem.getApplicableDiscount(100), 0.001);
    }

    @Test
    @DisplayName("A2-8: Data store persists and reloads order history")
    public void testDataStorePersistsOrderHistory() {
        TestContext ctx = newContext();
        Supplier supplier = addSupplierWithItem(ctx.service, "CMP-1", "Supplier", 1001, 901, 8.0, 0);
        Map<Integer, Integer> items = new LinkedHashMap<>();
        items.put(1001, 7);
        SupplierOrder order = ctx.service.createOrderFromAgreement(supplier.getSupplierId(), items, true);
        ctx.store.save(ctx.service, ctx.inventory);

        TestContext loaded = newContext(ctx.dbPath);
        loaded.store.loadInto(loaded.supplierManager, loaded.orderManager, loaded.inventory);

        SupplierOrder loadedOrder = loaded.service.getOrder(order.getOrderId());
        assertNotNull(loadedOrder);
        assertTrue(loadedOrder.isUrgent());
        assertEquals(1, loadedOrder.getItems().size());
        assertEquals(56.0, loadedOrder.getTotalPrice(), 0.001);
    }

    @Test
    @DisplayName("A2-9: Data store persists and reloads inventory records")
    public void testDataStorePersistsInventory() {
        TestContext ctx = newContext();
        InventoryItem item = ctx.inventory.addInventoryItem(1000, "Inventory Item", 5, 6, 30);
        item.addExpectedIncomingQuantity(20);
        ctx.store.save(ctx.service, ctx.inventory);

        TestContext loaded = newContext(ctx.dbPath);
        loaded.store.loadInto(loaded.supplierManager, loaded.orderManager, loaded.inventory);

        InventoryItem loadedItem = loaded.inventory.getInventoryItem(1000);
        assertNotNull(loadedItem);
        assertEquals(5, loadedItem.getWarehouseQuantity());
        assertEquals(6, loadedItem.getShelfQuantity());
        assertEquals(20, loadedItem.getExpectedIncomingQuantity());
    }

    @Test
    @DisplayName("A2-10: Persistent service writes supplier mutations to the local database")
    public void testPersistentServiceSavesMutations() {
        TestContext ctx = newContext();
        Supplier supplier = ctx.service.addSupplier("CMP-PERSIST", "Persistent Supplier", "bank",
                new PaymentTerms("Net", 45));
        ctx.service.addContactPerson(supplier.getSupplierId(), "Contact", "050", "c@example.com");

        TestContext loaded = newContext(ctx.dbPath);
        loaded.store.loadInto(loaded.supplierManager, loaded.orderManager, loaded.inventory);

        Supplier loadedSupplier = loaded.service.findByCompanyId("CMP-PERSIST");
        assertNotNull(loadedSupplier);
        assertEquals(1, loadedSupplier.getContactPersons().size());
        assertEquals("Net", loadedSupplier.getPaymentTerms().getPaymentMethod());
        assertEquals(45, loadedSupplier.getPaymentTerms().getNetDays());
    }

    private Supplier addSupplierWithItem(SupplierService service, String companyId, String name,
            int catalogNumber, int internalItemId, double price, double largeOrderDiscount) {
        Supplier supplier = service.addSupplier(companyId, name, "bank", new PaymentTerms("Net", 30));
        service.createAgreement(supplier.getSupplierId(), SupplyMethod.ON_ORDER, null, 2);
        service.addItemToAgreement(supplier.getSupplierId(), catalogNumber, internalItemId,
                "Shared Item", price, "Maker");
        if (largeOrderDiscount > 0) {
            service.addQuantityDiscount(supplier.getSupplierId(), catalogNumber, 100, largeOrderDiscount);
        }
        return supplier;
    }

    private TestContext newContext() {
        try {
            return newContext(Files.createTempDirectory("adss-a2-db"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TestContext newContext(Path dbPath) {
        SupplierManager supplierManager = new SupplierManager();
        OrderManager orderManager = new OrderManager();
        InventoryManager inventory = new InventoryManager();
        SuperLeeDataStore store = new SuperLeeDataStore(dbPath);
        PersistentSupplierService service = new PersistentSupplierService(
                supplierManager, orderManager, inventory, store);
        IntegratedOrderService integratedService = new IntegratedOrderService(service, inventory, store);
        return new TestContext(dbPath, supplierManager, orderManager, inventory, store, service, integratedService);
    }

    private static int toProjectDay(DayOfWeek dayOfWeek) {
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

    private static class TestContext {
        Path dbPath;
        SupplierManager supplierManager;
        OrderManager orderManager;
        InventoryManager inventory;
        SuperLeeDataStore store;
        PersistentSupplierService service;
        IntegratedOrderService integratedService;

        TestContext(Path dbPath, SupplierManager supplierManager, OrderManager orderManager,
                InventoryManager inventory, SuperLeeDataStore store, PersistentSupplierService service,
                IntegratedOrderService integratedService) {
            this.dbPath = dbPath;
            this.supplierManager = supplierManager;
            this.orderManager = orderManager;
            this.inventory = inventory;
            this.store = store;
            this.service = service;
            this.integratedService = integratedService;
        }
    }
}
