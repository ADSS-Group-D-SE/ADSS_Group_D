package Tests;

import DomainLayer.*;

/**
 * 10 simple unit tests for the Domain Layer.
 * Run from terminal:
 *   cd src
 *   javac DomainLayer/*.java Tests/DomainLayerTests.java
 *   java Tests.DomainLayerTests
 */
public class DomainLayerTests {

    private static int passed = 0;
    private static int failed = 0;

    // Helper method to check a condition and print PASS or FAIL
    private static void check(boolean condition, String testName) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName);
        }
    }

    // Test 1: Create a Supplier and check its name
    private static void test1_createSupplier() {
        Supplier s = new Supplier(1, "CMP-001", "Acme Ltd", "12-345", "Net 30");
        check(s.getName().equals("Acme Ltd"), "Test 1 - Supplier name is correct");
    }

    // Test 2: Supplier rejects empty name
    private static void test2_supplierRejectsEmptyName() {
        try {
            new Supplier(1, "CMP-001", "", "12-345", "Net 30");
            check(false, "Test 2 - Should throw for empty name");
        } catch (IllegalArgumentException e) {
            check(true, "Test 2 - Empty name throws exception");
        }
    }

    // Test 3: Add a contact person to a supplier
    private static void test3_addContactPerson() {
        Supplier s = new Supplier(1, "CMP-001", "Acme", "bank", "terms");
        ContactPerson cp = new ContactPerson("Alice", "050-111", "alice@mail.com");
        s.addContactPerson(cp);
        check(s.getContactPersons().size() == 1, "Test 3 - Contact person added");
    }

    // Test 4: Create a ContactPerson and check phone
    private static void test4_contactPersonPhone() {
        ContactPerson cp = new ContactPerson("Bob", "050-222", "bob@mail.com");
        cp.setPhoneNumber("050-999");
        check(cp.getPhoneNumber().equals("050-999"), "Test 4 - Phone number updated");
    }

    // Test 5: Create a QuantityDiscount and check values
    private static void test5_createQuantityDiscount() {
        QuantityDiscount qd = new QuantityDiscount(10, 5.0);
        check(qd.getMinQuantity() == 10 && qd.getDiscountPercent() == 5.0,
                "Test 5 - Discount created with correct values");
    }

    // Test 6: QuantityDiscount rejects negative quantity
    private static void test6_discountRejectsNegative() {
        try {
            new QuantityDiscount(-1, 10);
            check(false, "Test 6 - Should throw for negative quantity");
        } catch (IllegalArgumentException e) {
            check(true, "Test 6 - Negative quantity throws exception");
        }
    }

    // Test 7: SupplierItem effective price with a discount
    private static void test7_itemEffectivePrice() {
        SupplierItem item = new SupplierItem(100, 200, "Widget", 50.0, "WidgetCo");
        item.addQuantityDiscount(new QuantityDiscount(10, 10)); // 10% off for 10+ units
        check(item.getEffectivePrice(10) == 45.0, "Test 7 - Effective price with 10% discount");
    }

    // Test 8: Add an item to a SupplierAgreement
    private static void test8_addItemToAgreement() {
        SupplierAgreement agreement = new SupplierAgreement(SupplierAgreement.SupplyMethod.ON_ORDER);
        SupplierItem item = new SupplierItem(101, 201, "Bolt", 1.5, "BoltCo");
        agreement.addItem(item);
        check(agreement.getItems().size() == 1, "Test 8 - Item added to agreement");
    }

    // Test 9: SupplierAgreement rejects invalid supply day
    private static void test9_agreementRejectsInvalidDay() {
        SupplierAgreement agreement = new SupplierAgreement(SupplierAgreement.SupplyMethod.FIXED_DAYS);
        try {
            agreement.addFixedSupplyDay(8); // invalid, must be 1-7
            check(false, "Test 9 - Should throw for day 8");
        } catch (IllegalArgumentException e) {
            check(true, "Test 9 - Invalid day throws exception");
        }
    }

    // Test 10: SupplierManager add and find supplier
    private static void test10_managerAddAndFind() {
        SupplierManager manager = new SupplierManager();
        Supplier s = manager.addSupplier("CMP-100", "Alpha", "bank1", "Net 30");
        check(manager.getSupplier(s.getSupplierId()) == s,
                "Test 10 - Supplier found by ID");
    }

    // Main - run all 10 tests
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   Domain Layer Tests - Running 10 Tests   ");
        System.out.println("===========================================\n");

        test1_createSupplier();
        test2_supplierRejectsEmptyName();
        test3_addContactPerson();
        test4_contactPersonPhone();
        test5_createQuantityDiscount();
        test6_discountRejectsNegative();
        test7_itemEffectivePrice();
        test8_addItemToAgreement();
        test9_agreementRejectsInvalidDay();
        test10_managerAddAndFind();

        System.out.println("\n===========================================");
        System.out.println("   Results: " + passed + " passed, " + failed + " failed");
        System.out.println("===========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
