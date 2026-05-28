package Tests;

import DomainLayer.*;
import DomainLayer.SupplierAgreement.SupplyMethod;
import DomainLayer.SupplierOrder.OrderStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 21 Unit Tests for the Domain/Service Layers using JUnit 5.
 *
 * Compile and run from src/:
 *   javac -cp ../idea/junit_lib/junit-platform-console-standalone-1.10.2.jar DomainLayer/*.java Tests/DomainLayerTests.java
 *   java -jar ../idea/junit_lib/junit-platform-console-standalone-1.10.2.jar --class-path . --select-class Tests.DomainLayerTests
 */
public class DomainLayerTests {

    // ---------------------------------------------------------------
    // Test 1: Supplier – valid construction and getters
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 1: Supplier creation and getters")
    public void testSupplierCreation() {
        PaymentTerms terms = new PaymentTerms("Net", 30);
        Supplier s = new Supplier(1, "CMP-001", "Acme Ltd", "12-345-678", terms);
        assertEquals(1, s.getSupplierId());
        assertEquals("CMP-001", s.getCompanyId());
        assertEquals("Acme Ltd", s.getName());
        assertEquals("12-345-678", s.getBankAccount());
        assertEquals("Net", s.getPaymentTerms().getPaymentMethod());
        assertEquals(30, s.getPaymentTerms().getNetDays());
        assertTrue(s.getContactPersons().isEmpty());
        assertNull(s.getAgreement());
    }

    // ---------------------------------------------------------------
    // Test 2: Supplier – reject null/empty company ID and name
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 2: Supplier validation rejects invalid inputs")
    public void testSupplierValidation() {
        PaymentTerms terms = new PaymentTerms("Net", 30);
        assertThrows(IllegalArgumentException.class,
                () -> new Supplier(1, null, "Name", "bank", terms));
        assertThrows(IllegalArgumentException.class,
                () -> new Supplier(1, "", "Name", "bank", terms));
        assertThrows(IllegalArgumentException.class,
                () -> new Supplier(1, "CMP", null, "bank", terms));
        assertThrows(IllegalArgumentException.class,
                () -> new Supplier(1, "CMP", "", "bank", terms));
    }

    // ---------------------------------------------------------------
    // Test 3: Supplier – add, find, and remove contact persons
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 3: Supplier contact person management")
    public void testSupplierContactPersons() {
        PaymentTerms terms = new PaymentTerms("Net", 30);
        Supplier s = new Supplier(1, "CMP-001", "Acme", "bank", terms);
        ContactPerson cp1 = new ContactPerson("Alice", "050-1111111", "alice@acme.com");
        ContactPerson cp2 = new ContactPerson("Bob", "050-2222222", "bob@acme.com");

        s.addContactPerson(cp1);
        s.addContactPerson(cp2);
        assertEquals(2, s.getContactPersons().size());

        // Find by name (case-insensitive)
        assertEquals(cp1, s.findContactByName("alice"));
        assertNull(s.findContactByName("Charlie"));

        // Remove
        s.removeContactPerson(cp1);
        assertEquals(1, s.getContactPersons().size());

        // Null contact rejected
        assertThrows(IllegalArgumentException.class, () -> s.addContactPerson(null));
    }

    // ---------------------------------------------------------------
    // Test 4: ContactPerson – validation and setters
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 4: ContactPerson validation and setters")
    public void testContactPersonValidation() {
        assertThrows(IllegalArgumentException.class,
                () -> new ContactPerson(null, "050", "e@mail"));
        assertThrows(IllegalArgumentException.class,
                () -> new ContactPerson("", "050", "e@mail"));

        ContactPerson cp = new ContactPerson("Dana", "050-333", "dana@x.com");
        cp.setPhoneNumber("050-444");
        assertEquals("050-444", cp.getPhoneNumber());

        assertThrows(IllegalArgumentException.class, () -> cp.setName(""));
    }

    // ---------------------------------------------------------------
    // Test 5: QuantityDiscount – valid creation and edge validation
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 5: QuantityDiscount validation")
    public void testQuantityDiscountValidation() {
        QuantityDiscount qd = new QuantityDiscount(10, 5.0);
        assertEquals(10, qd.getMinQuantity());
        assertEquals(5.0, qd.getDiscountPercent());

        assertThrows(IllegalArgumentException.class, () -> new QuantityDiscount(0, 10));
        assertThrows(IllegalArgumentException.class, () -> new QuantityDiscount(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> new QuantityDiscount(1, -1));
        assertThrows(IllegalArgumentException.class, () -> new QuantityDiscount(1, 101));
    }

    // ---------------------------------------------------------------
    // Test 6: SupplierItem – effective price with quantity discounts
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 6: SupplierItem effective price with discounts")
    public void testSupplierItemEffectivePrice() {
        SupplierItem item = new SupplierItem(100, 200, "Widget", 50.0, "WidgetCo");

        // No discounts -> full price
        assertEquals(50.0, item.getEffectivePrice(1), 0.001);

        // Add discount tiers: 10+ units -> 10%, 50+ units -> 20%
        item.addQuantityDiscount(new QuantityDiscount(10, 10));
        item.addQuantityDiscount(new QuantityDiscount(50, 20));

        assertEquals(50.0, item.getEffectivePrice(5), 0.001);
        assertEquals(45.0, item.getEffectivePrice(10), 0.001);
        assertEquals(45.0, item.getEffectivePrice(25), 0.001);
        assertEquals(40.0, item.getEffectivePrice(50), 0.001);
        assertEquals(40.0, item.getEffectivePrice(100), 0.001);
    }

    // ---------------------------------------------------------------
    // Test 7: SupplierItem – add/remove discount, null rejected
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 7: SupplierItem discount management")
    public void testSupplierItemDiscountManagement() {
        SupplierItem item = new SupplierItem(1, 2, "Gadget", 100.0, "GadgetCo");
        QuantityDiscount qd = new QuantityDiscount(5, 15);

        item.addQuantityDiscount(qd);
        assertEquals(1, item.getQuantityDiscounts().size());

        item.removeQuantityDiscount(qd);
        assertEquals(0, item.getQuantityDiscounts().size());

        assertThrows(IllegalArgumentException.class, () -> item.addQuantityDiscount(null));
    }

    // ---------------------------------------------------------------
    // Test 8: SupplierAgreement – add/find/remove items, fixed days
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 8: SupplierAgreement item management")
    public void testSupplierAgreementItems() {
        SupplierAgreement agreement = new SupplierAgreement(SupplyMethod.FIXED_DAYS);
        assertEquals(SupplyMethod.FIXED_DAYS, agreement.getSupplyMethod());

        SupplierItem item1 = new SupplierItem(101, 201, "Bolt", 1.5, "BoltCo");
        SupplierItem item2 = new SupplierItem(102, 202, "Nut", 0.8, "NutCo");

        agreement.addItem(item1);
        agreement.addItem(item2);
        assertEquals(2, agreement.getItems().size());

        assertEquals(item1, agreement.findItemByCatalogNumber(101));
        assertNull(agreement.findItemByCatalogNumber(999));
        assertEquals(item2, agreement.findItemByInternalId(202));

        agreement.removeItem(item1);
        assertEquals(1, agreement.getItems().size());

        assertThrows(IllegalArgumentException.class, () -> agreement.addItem(null));
    }

    // ---------------------------------------------------------------
    // Test 9: SupplierAgreement – fixed supply days & delivery days
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 9: SupplierAgreement days validation")
    public void testSupplierAgreementDays() {
        SupplierAgreement agreement = new SupplierAgreement(SupplyMethod.ON_ORDER);

        agreement.setDeliveryDays(3);
        assertEquals(3, agreement.getDeliveryDays());

        assertThrows(IllegalArgumentException.class, () -> agreement.setDeliveryDays(-1));

        agreement.addFixedSupplyDay(1);
        agreement.addFixedSupplyDay(4);
        assertEquals(2, agreement.getFixedSupplyDays().size());

        // Duplicate day should not be added twice
        agreement.addFixedSupplyDay(1);
        assertEquals(2, agreement.getFixedSupplyDays().size());

        assertThrows(IllegalArgumentException.class, () -> agreement.addFixedSupplyDay(0));
        assertThrows(IllegalArgumentException.class, () -> agreement.addFixedSupplyDay(8));

        agreement.removeFixedSupplyDay(1);
        assertEquals(1, agreement.getFixedSupplyDays().size());
    }

    // ---------------------------------------------------------------
    // Test 10: SupplierManager – add, get, find, remove suppliers
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 10: SupplierManager CRUD operations")
    public void testSupplierManager() {
        SupplierManager manager = new SupplierManager();
        assertEquals(0, manager.getSupplierCount());

        PaymentTerms terms1 = new PaymentTerms("Net", 30);
        PaymentTerms terms2 = new PaymentTerms("Net", 60);

        Supplier s1 = manager.addSupplier("CMP-100", "Alpha", "bank1", terms1);
        Supplier s2 = manager.addSupplier("CMP-200", "Beta", "bank2", terms2);
        assertEquals(2, manager.getSupplierCount());

        assertEquals(s1, manager.getSupplier(s1.getSupplierId()));
        assertNull(manager.getSupplier(999));

        assertEquals(s2, manager.findByCompanyId("CMP-200"));

        SupplierAgreement agreement = new SupplierAgreement(SupplyMethod.ON_ORDER);
        agreement.addItem(new SupplierItem(10, 500, "Screw", 0.1, "ScrewCo"));
        s1.setAgreement(agreement);

        assertEquals(1, manager.findSuppliersByItem(500).size());
        assertEquals(0, manager.findSuppliersByItem(999).size());

        assertTrue(manager.removeSupplier(s1.getSupplierId()));
        assertFalse(manager.removeSupplier(s1.getSupplierId()));
        assertEquals(1, manager.getSupplierCount());
    }

    // ===============================================================
    // NEW TESTS (11-17) — Addressing professor feedback
    // ===============================================================

    // ---------------------------------------------------------------
    // Test 11: PaymentTerms – creation, parsing, and validation
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 11: PaymentTerms creation and validation")
    public void testPaymentTerms() {
        // Explicit constructor
        PaymentTerms pt1 = new PaymentTerms("Net", 30);
        assertEquals("Net", pt1.getPaymentMethod());
        assertEquals(30, pt1.getNetDays());
        assertEquals("Net 30", pt1.toString());

        // String-parsing constructor
        PaymentTerms pt2 = new PaymentTerms("Net 60");
        assertEquals("Net", pt2.getPaymentMethod());
        assertEquals(60, pt2.getNetDays());

        // Single-word terms (no days)
        PaymentTerms pt3 = new PaymentTerms("Cash");
        assertEquals("Cash", pt3.getPaymentMethod());
        assertEquals(0, pt3.getNetDays());

        // Validation
        assertThrows(IllegalArgumentException.class, () -> new PaymentTerms(null, 30));
        assertThrows(IllegalArgumentException.class, () -> new PaymentTerms("", 30));
        assertThrows(IllegalArgumentException.class, () -> new PaymentTerms("Net", -1));

        // Setter validation
        pt1.setNetDays(45);
        assertEquals(45, pt1.getNetDays());
        assertThrows(IllegalArgumentException.class, () -> pt1.setNetDays(-5));
        assertThrows(IllegalArgumentException.class, () -> pt1.setPaymentMethod(""));
    }

    // ---------------------------------------------------------------
    // Test 12: SupplierOrder – creation, add items, verify totals
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 12: SupplierOrder creation and total calculation")
    public void testSupplierOrderCreation() {
        SupplierOrder order = new SupplierOrder(1, 100, false);
        assertEquals(1, order.getOrderId());
        assertEquals(100, order.getSupplierId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertFalse(order.isUrgent());
        assertEquals(LocalDate.now(), order.getOrderDate());
        assertTrue(order.getItems().isEmpty());
        assertEquals(0.0, order.getTotalPrice(), 0.001);

        // Add items
        OrderItem oi1 = new OrderItem(1001, 101, "Bamba", 10, 3.50, 0);
        OrderItem oi2 = new OrderItem(1002, 102, "Bissli", 5, 5.00, 10); // 10% discount

        order.addItem(oi1);
        order.addItem(oi2);
        assertEquals(2, order.getItems().size());

        // Total: 10 * 3.50 + 5 * 5.00 * 0.90 = 35.00 + 22.50 = 57.50
        assertEquals(57.50, order.getTotalPrice(), 0.001);
    }

    // ---------------------------------------------------------------
    // Test 13: Order discount calculation from agreement
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 13: Order applies quantity discounts correctly")
    public void testOrderDiscountCalculation() {
        // Setup: item with discount tiers: 10+ -> 5%, 100+ -> 15%
        SupplierItem agrItem = new SupplierItem(1001, 101, "Widget", 20.0, "WidgetCo");
        agrItem.addQuantityDiscount(new QuantityDiscount(10, 5));
        agrItem.addQuantityDiscount(new QuantityDiscount(100, 15));

        // Order 50 units -> 5% discount applies
        int qty1 = 50;
        double discount1 = agrItem.getApplicableDiscount(qty1);
        assertEquals(5.0, discount1, 0.001);
        OrderItem oi1 = new OrderItem(1001, 101, "Widget", qty1, 20.0, discount1);
        // Expected: 50 * 20 * 0.95 = 950.00
        assertEquals(950.0, oi1.getTotalPrice(), 0.001);

        // Order 200 units -> 15% discount applies
        int qty2 = 200;
        double discount2 = agrItem.getApplicableDiscount(qty2);
        assertEquals(15.0, discount2, 0.001);
        OrderItem oi2 = new OrderItem(1001, 101, "Widget", qty2, 20.0, discount2);
        // Expected: 200 * 20 * 0.85 = 3400.00
        assertEquals(3400.0, oi2.getTotalPrice(), 0.001);

        // Order 5 units -> no discount
        int qty3 = 5;
        double discount3 = agrItem.getApplicableDiscount(qty3);
        assertEquals(0.0, discount3, 0.001);
        OrderItem oi3 = new OrderItem(1001, 101, "Widget", qty3, 20.0, discount3);
        // Expected: 5 * 20 = 100.00
        assertEquals(100.0, oi3.getTotalPrice(), 0.001);
    }

    // ---------------------------------------------------------------
    // Test 14: Agreement freeze – blocks addItem and removeItem
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 14: Frozen agreement rejects modifications")
    public void testAgreementFreeze() {
        SupplierAgreement agreement = new SupplierAgreement(SupplyMethod.ON_ORDER);
        SupplierItem item1 = new SupplierItem(101, 201, "Bolt", 1.5, "BoltCo");

        // Can add before freeze
        assertFalse(agreement.isFrozen());
        agreement.addItem(item1);
        assertEquals(1, agreement.getItems().size());

        // Freeze
        agreement.freeze();
        assertTrue(agreement.isFrozen());

        // Cannot add or remove while frozen
        SupplierItem item2 = new SupplierItem(102, 202, "Nut", 0.8, "NutCo");
        assertThrows(IllegalStateException.class, () -> agreement.addItem(item2));
        assertThrows(IllegalStateException.class, () -> agreement.removeItem(item1));

        // Unfreeze restores normal operation
        agreement.unfreeze();
        assertFalse(agreement.isFrozen());
        agreement.addItem(item2);
        assertEquals(2, agreement.getItems().size());
    }

    // ---------------------------------------------------------------
    // Test 15: Urgent order – delivery date is next day
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 15: Urgent order sets delivery to next day")
    public void testUrgentOrderDeliveryDate() {
        // Setup agreement with ON_ORDER method, 5 day delivery
        SupplierAgreement agreement = new SupplierAgreement(SupplyMethod.ON_ORDER);
        agreement.setDeliveryDays(5);

        // Non-urgent order: should be orderDate + 5
        SupplierOrder normalOrder = new SupplierOrder(1, 100, false);
        normalOrder.computeExpectedDeliveryDate(agreement);
        assertEquals(LocalDate.now().plusDays(5), normalOrder.getExpectedDeliveryDate());

        // Urgent order: should be orderDate + 1 regardless of delivery days
        SupplierOrder urgentOrder = new SupplierOrder(2, 100, true);
        urgentOrder.computeExpectedDeliveryDate(agreement);
        assertEquals(LocalDate.now().plusDays(1), urgentOrder.getExpectedDeliveryDate());
    }

    // ---------------------------------------------------------------
    // Test 16: Order history – multiple orders per supplier
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 16: OrderManager tracks order history per supplier")
    public void testOrderHistory() {
        OrderManager manager = new OrderManager();
        assertEquals(0, manager.getOrderCount());

        // Create orders for two suppliers
        SupplierOrder o1 = manager.createOrder(1, false);
        SupplierOrder o2 = manager.createOrder(1, false);
        SupplierOrder o3 = manager.createOrder(2, true);

        assertEquals(3, manager.getOrderCount());

        // History for supplier 1
        List<SupplierOrder> history1 = manager.getOrdersBySupplier(1);
        assertEquals(2, history1.size());

        // History for supplier 2
        List<SupplierOrder> history2 = manager.getOrdersBySupplier(2);
        assertEquals(1, history2.size());
        assertTrue(history2.get(0).isUrgent());

        // No history for non-existent supplier
        List<SupplierOrder> history3 = manager.getOrdersBySupplier(999);
        assertEquals(0, history3.size());
    }

    // ---------------------------------------------------------------
    // Test 17: OrderManager & SupplierOrder lifecycle
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 17: Order lifecycle (PENDING -> SENT -> DELIVERED)")
    public void testOrderLifecycle() {
        OrderManager manager = new OrderManager();
        SupplierOrder order = manager.createOrder(1, false);
        assertEquals(OrderStatus.PENDING, order.getStatus());

        // Add an item so we can send
        order.addItem(new OrderItem(1001, 101, "Test Item", 10, 5.0, 0));

        // Cannot add items once sent
        order.send();
        assertEquals(OrderStatus.SENT, order.getStatus());
        assertThrows(IllegalStateException.class,
                () -> order.addItem(new OrderItem(1002, 102, "Another", 1, 1.0, 0)));

        // Cannot send again
        assertThrows(IllegalStateException.class, order::send);

        // Mark delivered
        order.markDelivered();
        assertEquals(OrderStatus.DELIVERED, order.getStatus());

        // Cannot cancel delivered order
        assertThrows(IllegalStateException.class, order::cancel);

        // Test cancel flow
        SupplierOrder order2 = manager.createOrder(1, false);
        order2.cancel();
        assertEquals(OrderStatus.CANCELLED, order2.getStatus());

        // Verify retrieval
        assertEquals(order, manager.getOrder(order.getOrderId()));
        assertNull(manager.getOrder(999));
    }

    // ---------------------------------------------------------------
    // Test 18: Shortage flow chooses cheapest supplier using discounts
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 18: Shortage order chooses best supplier by quantity discount")
    public void testShortageOrderSelectsBestSupplier() {
        SupplierManager supplierManager = new SupplierManager();
        OrderManager orderManager = new OrderManager();
        ServiceLayer.SupplierService service = new ServiceLayer.SupplierService(supplierManager, orderManager);

        Supplier s1 = service.addSupplier("CMP-1", "Bulk Supplier", "bank1", new PaymentTerms("Net", 30));
        service.createAgreement(s1.getSupplierId(), SupplyMethod.ON_ORDER, null, 3);
        service.addItemToAgreement(s1.getSupplierId(), 1001, 500, "Shared Item", 10.0, "Maker");
        service.addQuantityDiscount(s1.getSupplierId(), 1001, 100, 30);

        Supplier s2 = service.addSupplier("CMP-2", "Cheap Small Supplier", "bank2", new PaymentTerms("Net", 30));
        service.createAgreement(s2.getSupplierId(), SupplyMethod.ON_ORDER, null, 2);
        service.addItemToAgreement(s2.getSupplierId(), 2001, 500, "Shared Item", 8.0, "Maker");

        Supplier best = service.findBestSupplierForItem(500, 100);
        assertEquals(s1.getSupplierId(), best.getSupplierId());

        SupplierOrder order = service.createShortageOrder(500, 100, false);
        assertEquals(s1.getSupplierId(), order.getSupplierId());
        assertEquals(OrderStatus.SENT, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals(30.0, order.getItems().get(0).getDiscountPercent(), 0.001);
        assertEquals(700.0, order.getTotalPrice(), 0.001);
    }

    // ---------------------------------------------------------------
    // Test 19: Periodic fixed-day order flow
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 19: Periodic order requires fixed-day agreement")
    public void testPeriodicOrderForFixedDaySupplier() {
        SupplierManager supplierManager = new SupplierManager();
        OrderManager orderManager = new OrderManager();
        ServiceLayer.SupplierService service = new ServiceLayer.SupplierService(supplierManager, orderManager);

        Supplier fixedSupplier = service.addSupplier("CMP-FIX", "Fixed Days", "bank", new PaymentTerms("Net", 30));
        service.createAgreement(fixedSupplier.getSupplierId(), SupplyMethod.FIXED_DAYS, Arrays.asList(1, 4), 0);
        service.addItemToAgreement(fixedSupplier.getSupplierId(), 3001, 700, "Periodic Item", 12.0, "Maker");

        Map<Integer, Integer> internalQuantities = new LinkedHashMap<>();
        internalQuantities.put(700, 20);
        SupplierOrder order = service.createPeriodicOrderForSupplier(fixedSupplier.getSupplierId(), internalQuantities);

        assertEquals(fixedSupplier.getSupplierId(), order.getSupplierId());
        assertEquals(OrderStatus.SENT, order.getStatus());
        assertNotNull(order.getExpectedDeliveryDate());
        assertEquals(1, order.getItems().size());
        assertEquals(700, order.getItems().get(0).getInternalItemId());

        Supplier onOrderSupplier = service.addSupplier("CMP-ON", "On Order", "bank", new PaymentTerms("Net", 30));
        service.createAgreement(onOrderSupplier.getSupplierId(), SupplyMethod.ON_ORDER, null, 3);
        service.addItemToAgreement(onOrderSupplier.getSupplierId(), 4001, 800, "On Order Item", 5.0, "Maker");

        Map<Integer, Integer> onOrderItems = new LinkedHashMap<>();
        onOrderItems.put(800, 5);
        assertThrows(IllegalArgumentException.class,
                () -> service.createPeriodicOrderForSupplier(onOrderSupplier.getSupplierId(), onOrderItems));
    }

    // ---------------------------------------------------------------
    // Test 20: Service-level freeze blocks all agreement mutations
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 20: Frozen agreement blocks service-level mutations")
    public void testServiceFreezeBlocksAllAgreementChanges() {
        SupplierManager supplierManager = new SupplierManager();
        OrderManager orderManager = new OrderManager();
        ServiceLayer.SupplierService service = new ServiceLayer.SupplierService(supplierManager, orderManager);

        Supplier supplier = service.addSupplier("CMP-FROZEN", "Frozen Supplier", "bank", new PaymentTerms("Net", 30));
        int supplierId = supplier.getSupplierId();
        service.createAgreement(supplierId, SupplyMethod.FIXED_DAYS, Arrays.asList(2), 0);
        service.addItemToAgreement(supplierId, 1001, 101, "Frozen Item", 3.0, "Maker");
        service.freezeAgreement(supplierId);

        assertThrows(IllegalStateException.class,
                () -> service.addItemToAgreement(supplierId, 1002, 102, "New Item", 4.0, "Maker"));
        assertThrows(IllegalStateException.class,
                () -> service.removeItemFromAgreement(supplierId, 1001));
        assertThrows(IllegalStateException.class,
                () -> service.addQuantityDiscount(supplierId, 1001, 10, 5));
        assertThrows(IllegalStateException.class,
                () -> service.updateSupplyMethod(supplierId, SupplyMethod.ON_ORDER));
        assertThrows(IllegalStateException.class,
                () -> service.setDeliveryDays(supplierId, 4));
        assertThrows(IllegalStateException.class,
                () -> service.addFixedSupplyDay(supplierId, 5));
        assertThrows(IllegalStateException.class,
                () -> service.removeFixedSupplyDay(supplierId, 2));
        assertThrows(IllegalStateException.class,
                () -> service.createAgreement(supplierId, SupplyMethod.ON_ORDER, null, 3));
    }

    // ---------------------------------------------------------------
    // Test 21: Order-from-agreement flow by internal item IDs
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Test 21: Order from agreement by internal item IDs")
    public void testCreateOrderFromAgreementByInternalItems() {
        SupplierManager supplierManager = new SupplierManager();
        OrderManager orderManager = new OrderManager();
        ServiceLayer.SupplierService service = new ServiceLayer.SupplierService(supplierManager, orderManager);

        Supplier supplier = service.addSupplier("CMP-ORDER", "Order Supplier", "bank", new PaymentTerms("Net", 45));
        int supplierId = supplier.getSupplierId();
        service.createAgreement(supplierId, SupplyMethod.ON_ORDER, null, 4);
        service.addItemToAgreement(supplierId, 5001, 901, "Agreement Item", 6.0, "Maker");
        service.addQuantityDiscount(supplierId, 5001, 10, 10);

        Map<Integer, Integer> internalQuantities = new LinkedHashMap<>();
        internalQuantities.put(901, 10);
        SupplierOrder order = service.createOrderFromAgreementByInternalItems(supplierId, internalQuantities, true);

        assertEquals(OrderStatus.SENT, order.getStatus());
        assertTrue(order.isUrgent());
        assertEquals(LocalDate.now().plusDays(1), order.getExpectedDeliveryDate());
        assertEquals(1, order.getItems().size());
        assertEquals(5001, order.getItems().get(0).getCatalogNumber());
        assertEquals(54.0, order.getTotalPrice(), 0.001);
    }
}
