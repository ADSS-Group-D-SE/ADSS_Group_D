package Tests;

import DomainLayer.*;

/**
 * 10 Unit Tests for the Domain Layer.
 * Run from terminal:
 * cd src
 * javac DomainLayer/*.java Tests/DomainLayerTests.java
 * java Tests.DomainLayerTests
 */
public class DomainLayerTests {

    private static int passed = 0;
    private static int failed = 0;

    // ---------------------------------------------------------------
    // Assertion helpers
    // ---------------------------------------------------------------
    private static void assertEquals(Object expected, Object actual, String testName) {
        if (expected == null && actual == null || expected != null && expected.equals(actual)) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName +
                    " | Expected: " + expected + " | Got: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName);
        }
    }

    private static void assertThrows(Runnable action, Class<? extends Throwable> expectedException, String testName) {
        try {
            action.run();
            failed++;
            System.out.println("[FAIL] " + testName + " | No exception was thrown");
        } catch (Throwable t) {
            if (expectedException.isInstance(t)) {
                passed++;
                System.out.println("[PASS] " + testName);
            } else {
                failed++;
                System.out.println("[FAIL] " + testName +
                        " | Expected: " + expectedException.getSimpleName() +
                        " | Got: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
    }

    // ---------------------------------------------------------------
    // Test 1: Supplier – valid construction and getters
    // ---------------------------------------------------------------
    private static void testSupplierCreation() {
        Supplier s = new Supplier(1, "CMP-001", "Acme Ltd", "12-345-678", "Net 30");
        assertEquals(1, s.getSupplierId(), "Test 1a: Supplier ID");
        assertEquals("CMP-001", s.getCompanyId(), "Test 1b: Company ID");
        assertEquals("Acme Ltd", s.getName(), "Test 1c: Supplier name");
        assertEquals("12-345-678", s.getBankAccount(), "Test 1d: Bank account");
        assertEquals("Net 30", s.getPaymentTerms(), "Test 1e: Payment terms");
        assertTrue(s.getContactPersons().isEmpty(), "Test 1f: Contact list initially empty");
        assertTrue(s.getAgreement() == null, "Test 1g: Agreement initially null");
    }

    // ---------------------------------------------------------------
    // Test 2: Supplier – reject null/empty company ID and name
    // ---------------------------------------------------------------
    private static void testSupplierValidation() {
        assertThrows(
                () -> new Supplier(1, null, "Name", "bank", "terms"),
                IllegalArgumentException.class,
                "Test 2a: Null company ID throws");
        assertThrows(
                () -> new Supplier(1, "", "Name", "bank", "terms"),
                IllegalArgumentException.class,
                "Test 2b: Empty company ID throws");
        assertThrows(
                () -> new Supplier(1, "CMP", null, "bank", "terms"),
                IllegalArgumentException.class,
                "Test 2c: Null name throws");
        assertThrows(
                () -> new Supplier(1, "CMP", "", "bank", "terms"),
                IllegalArgumentException.class,
                "Test 2d: Empty name throws");
    }

    // ---------------------------------------------------------------
    // Test 3: Supplier – add, find, and remove contact persons
    // ---------------------------------------------------------------
    private static void testSupplierContactPersons() {
        Supplier s = new Supplier(1, "CMP-001", "Acme", "bank", "terms");
        ContactPerson cp1 = new ContactPerson("Alice", "050-1111111", "alice@acme.com");
        ContactPerson cp2 = new ContactPerson("Bob", "050-2222222", "bob@acme.com");

        s.addContactPerson(cp1);
        s.addContactPerson(cp2);
        assertEquals(2, s.getContactPersons().size(), "Test 3a: Two contacts added");

        // Find by name (case-insensitive)
        assertEquals(cp1, s.findContactByName("alice"), "Test 3b: Find Alice (case-insensitive)");
        assertTrue(s.findContactByName("Charlie") == null, "Test 3c: Non-existent contact returns null");

        // Remove
        s.removeContactPerson(cp1);
        assertEquals(1, s.getContactPersons().size(), "Test 3d: One contact after removal");

        // Null contact rejected
        assertThrows(
                () -> s.addContactPerson(null),
                IllegalArgumentException.class,
                "Test 3e: Null contact throws");
    }

    // ---------------------------------------------------------------
    // Test 4: ContactPerson – validation and setters
    // ---------------------------------------------------------------
    private static void testContactPersonValidation() {
        assertThrows(
                () -> new ContactPerson(null, "050", "e@mail"),
                IllegalArgumentException.class,
                "Test 4a: Null name throws");
        assertThrows(
                () -> new ContactPerson("", "050", "e@mail"),
                IllegalArgumentException.class,
                "Test 4b: Empty name throws");

        ContactPerson cp = new ContactPerson("Dana", "050-333", "dana@x.com");
        cp.setPhoneNumber("050-444");
        assertEquals("050-444", cp.getPhoneNumber(), "Test 4c: Phone updated");

        assertThrows(
                () -> cp.setName(""),
                IllegalArgumentException.class,
                "Test 4d: setName empty throws");
    }

    // ---------------------------------------------------------------
    // Test 5: QuantityDiscount – valid creation and edge validation
    // ---------------------------------------------------------------
    private static void testQuantityDiscountValidation() {
        QuantityDiscount qd = new QuantityDiscount(10, 5.0);
        assertEquals(10, qd.getMinQuantity(), "Test 5a: Min quantity");
        assertEquals(5.0, qd.getDiscountPercent(), "Test 5b: Discount percent");

        // Invalid min quantity
        assertThrows(
                () -> new QuantityDiscount(0, 10),
                IllegalArgumentException.class,
                "Test 5c: Zero min quantity throws");
        assertThrows(
                () -> new QuantityDiscount(-1, 10),
                IllegalArgumentException.class,
                "Test 5d: Negative min quantity throws");

        // Invalid discount percent
        assertThrows(
                () -> new QuantityDiscount(1, -1),
                IllegalArgumentException.class,
                "Test 5e: Negative discount throws");
        assertThrows(
                () -> new QuantityDiscount(1, 101),
                IllegalArgumentException.class,
                "Test 5f: Discount > 100 throws");
    }

    // ---------------------------------------------------------------
    // Test 6: SupplierItem – effective price with quantity discounts
    // ---------------------------------------------------------------
    private static void testSupplierItemEffectivePrice() {
        SupplierItem item = new SupplierItem(100, 200, "Widget", 50.0, "WidgetCo");

        // No discounts → full price
        assertEquals(50.0, item.getEffectivePrice(1), "Test 6a: No discount – full price");

        // Add discount tiers: 10+ units → 10%, 50+ units → 20%
        item.addQuantityDiscount(new QuantityDiscount(10, 10));
        item.addQuantityDiscount(new QuantityDiscount(50, 20));

        assertEquals(50.0, item.getEffectivePrice(5), "Test 6b: Qty 5 – no tier hit");
        assertEquals(45.0, item.getEffectivePrice(10), "Test 6c: Qty 10 – 10% off");
        assertEquals(45.0, item.getEffectivePrice(25), "Test 6d: Qty 25 – still 10% tier");
        assertEquals(40.0, item.getEffectivePrice(50), "Test 6e: Qty 50 – 20% off");
        assertEquals(40.0, item.getEffectivePrice(100), "Test 6f: Qty 100 – best is 20%");
    }

    // ---------------------------------------------------------------
    // Test 7: SupplierItem – add/remove discount, null discount rejected
    // ---------------------------------------------------------------
    private static void testSupplierItemDiscountManagement() {
        SupplierItem item = new SupplierItem(1, 2, "Gadget", 100.0, "GadgetCo");
        QuantityDiscount qd = new QuantityDiscount(5, 15);

        item.addQuantityDiscount(qd);
        assertEquals(1, item.getQuantityDiscounts().size(), "Test 7a: One discount added");

        item.removeQuantityDiscount(qd);
        assertEquals(0, item.getQuantityDiscounts().size(), "Test 7b: Discount removed");

        assertThrows(
                () -> item.addQuantityDiscount(null),
                IllegalArgumentException.class,
                "Test 7c: Null discount throws");
    }

    // ---------------------------------------------------------------
    // Test 8: SupplierAgreement – add/find/remove items, fixed days
    // ---------------------------------------------------------------
    private static void testSupplierAgreementItems() {
        SupplierAgreement agreement = new SupplierAgreement(SupplierAgreement.SupplyMethod.FIXED_DAYS);
        assertEquals(SupplierAgreement.SupplyMethod.FIXED_DAYS, agreement.getSupplyMethod(),
                "Test 8a: Supply method");

        SupplierItem item1 = new SupplierItem(101, 201, "Bolt", 1.5, "BoltCo");
        SupplierItem item2 = new SupplierItem(102, 202, "Nut", 0.8, "NutCo");

        agreement.addItem(item1);
        agreement.addItem(item2);
        assertEquals(2, agreement.getItems().size(), "Test 8b: Two items added");

        // Find by catalog number
        assertEquals(item1, agreement.findItemByCatalogNumber(101), "Test 8c: Find by catalog 101");
        assertTrue(agreement.findItemByCatalogNumber(999) == null, "Test 8d: Missing catalog returns null");

        // Find by internal ID
        assertEquals(item2, agreement.findItemByInternalId(202), "Test 8e: Find by internal ID 202");

        // Remove
        agreement.removeItem(item1);
        assertEquals(1, agreement.getItems().size(), "Test 8f: One item after removal");

        // Null item rejected
        assertThrows(
                () -> agreement.addItem(null),
                IllegalArgumentException.class,
                "Test 8g: Null item throws");
    }

    // ---------------------------------------------------------------
    // Test 9: SupplierAgreement – fixed supply days & delivery days validation
    // ---------------------------------------------------------------
    private static void testSupplierAgreementDays() {
        SupplierAgreement agreement = new SupplierAgreement(SupplierAgreement.SupplyMethod.ON_ORDER);

        // Delivery days
        agreement.setDeliveryDays(3);
        assertEquals(3, agreement.getDeliveryDays(), "Test 9a: Delivery days set to 3");

        assertThrows(
                () -> agreement.setDeliveryDays(-1),
                IllegalArgumentException.class,
                "Test 9b: Negative delivery days throws");

        // Fixed supply days (1-7)
        agreement.addFixedSupplyDay(1); // Sunday
        agreement.addFixedSupplyDay(4); // Wednesday
        assertEquals(2, agreement.getFixedSupplyDays().size(), "Test 9c: Two fixed days");

        // Duplicate day should not be added twice
        agreement.addFixedSupplyDay(1);
        assertEquals(2, agreement.getFixedSupplyDays().size(), "Test 9d: Duplicate day ignored");

        // Invalid day
        assertThrows(
                () -> agreement.addFixedSupplyDay(0),
                IllegalArgumentException.class,
                "Test 9e: Day 0 throws");
        assertThrows(
                () -> agreement.addFixedSupplyDay(8),
                IllegalArgumentException.class,
                "Test 9f: Day 8 throws");

        // Remove day
        agreement.removeFixedSupplyDay(1);
        assertEquals(1, agreement.getFixedSupplyDays().size(), "Test 9g: One day after removal");
    }

    // ---------------------------------------------------------------
    // Test 10: SupplierManager – add, get, find, remove suppliers
    // ---------------------------------------------------------------
    private static void testSupplierManager() {
        SupplierManager manager = new SupplierManager();
        assertEquals(0, manager.getSupplierCount(), "Test 10a: Initially empty");

        Supplier s1 = manager.addSupplier("CMP-100", "Alpha", "bank1", "Net 30");
        Supplier s2 = manager.addSupplier("CMP-200", "Beta", "bank2", "Net 60");
        assertEquals(2, manager.getSupplierCount(), "Test 10b: Two suppliers added");

        // Get by ID
        assertEquals(s1, manager.getSupplier(s1.getSupplierId()), "Test 10c: Get supplier by ID");
        assertTrue(manager.getSupplier(999) == null, "Test 10d: Missing ID returns null");

        // Find by company ID
        assertEquals(s2, manager.findByCompanyId("CMP-200"), "Test 10e: Find by company ID");

        // Find suppliers by item (need an agreement with an item)
        SupplierAgreement agreement = new SupplierAgreement(SupplierAgreement.SupplyMethod.ON_ORDER);
        agreement.addItem(new SupplierItem(10, 500, "Screw", 0.1, "ScrewCo"));
        s1.setAgreement(agreement);

        assertEquals(1, manager.findSuppliersByItem(500).size(), "Test 10f: One supplier has item 500");
        assertEquals(0, manager.findSuppliersByItem(999).size(), "Test 10g: No supplier has item 999");

        // Remove supplier
        assertTrue(manager.removeSupplier(s1.getSupplierId()), "Test 10h: Remove existing supplier");
        assertTrue(!manager.removeSupplier(s1.getSupplierId()), "Test 10i: Remove non-existent returns false");
        assertEquals(1, manager.getSupplierCount(), "Test 10j: One supplier remaining");
    }

    // ---------------------------------------------------------------
    // Main – run all tests
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   Domain Layer Tests – Running 10 Tests   ");
        System.out.println("===========================================\n");

        testSupplierCreation(); // Test 1
        System.out.println();
        testSupplierValidation(); // Test 2
        System.out.println();
        testSupplierContactPersons(); // Test 3
        System.out.println();
        testContactPersonValidation(); // Test 4
        System.out.println();
        testQuantityDiscountValidation(); // Test 5
        System.out.println();
        testSupplierItemEffectivePrice(); // Test 6
        System.out.println();
        testSupplierItemDiscountManagement(); // Test 7
        System.out.println();
        testSupplierAgreementItems(); // Test 8
        System.out.println();
        testSupplierAgreementDays(); // Test 9
        System.out.println();
        testSupplierManager(); // Test 10

        System.out.println("\n===========================================");
        System.out.println("   Results: " + passed + " passed, " + failed + " failed");
        System.out.println("===========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
