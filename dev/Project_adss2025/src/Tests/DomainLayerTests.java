package Tests;

import DomainLayer.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DomainLayerTests {

    // Test 1: Create a Supplier and check its name
    @Test
    public void testCreateSupplier() {
        Supplier s = new Supplier(1, "CMP-001", "Acme Ltd", "12-345", "Net 30");
        assertEquals("Acme Ltd", s.getName(), "Supplier name is correct");
    }

    // Test 2: Supplier rejects empty name
    @Test
    public void testSupplierRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Supplier(1, "CMP-001", "", "12-345", "Net 30");
        }, "Empty name should throw IllegalArgumentException");
    }

    // Test 3: Add a contact person to a supplier
    @Test
    public void testAddContactPerson() {
        Supplier s = new Supplier(1, "CMP-001", "Acme", "bank", "terms");
        ContactPerson cp = new ContactPerson("Alice", "050-111", "alice@mail.com");
        
        s.addContactPerson(cp);
        
        assertEquals(1, s.getContactPersons().size(), "Contact person should be added successfully");
        assertTrue(s.getContactPersons().contains(cp), "The correct contact person should be in the list");
    }

    // Test 4: Create a ContactPerson and check phone
    @Test
    public void testContactPersonPhone() {
        ContactPerson cp = new ContactPerson("Bob", "050-222", "bob@mail.com");
        cp.setPhoneNumber("050-999");
        
        assertEquals("050-999", cp.getPhoneNumber(), "Phone number should be updated");
    }

    // Test 5: Create a QuantityDiscount and check values
    @Test
    public void testCreateQuantityDiscount() {
        QuantityDiscount qd = new QuantityDiscount(10, 5.0);
        
        assertEquals(10, qd.getMinQuantity(), "Minimum quantity should match");
        assertEquals(5.0, qd.getDiscountPercent(), "Discount percent should match");
    }

    // Test 6: QuantityDiscount rejects negative quantity
    @Test
    public void testDiscountRejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new QuantityDiscount(-1, 10);
        }, "Negative quantity should throw exception");
    }

    // Test 7: Strict price calculation (sensitive to small changes)
    @Test
    public void testStrictPriceCalculation() {
        SupplierItem item = new SupplierItem(100, 200, "Widget", 50.0, "Company");

        // add two discounts
        item.addQuantityDiscount(new QuantityDiscount(10, 10)); // 10%
        item.addQuantityDiscount(new QuantityDiscount(50, 20)); // 20%

        // Notice the 3rd parameter (0.001) - it's the allowed delta for double comparisons
        assertEquals(45.0, item.getEffectivePrice(10), 0.001, "10 units gives 10% discount");
        assertEquals(40.0, item.getEffectivePrice(50), 0.001, "50 units gives best discount (20%)");
    }

    // Test 8: Add an item to a SupplierAgreement
    @Test
    public void testAddItemToAgreement() {
        SupplierAgreement agreement = new SupplierAgreement(SupplierAgreement.SupplyMethod.ON_ORDER);
        SupplierItem item = new SupplierItem(101, 201, "Bolt", 1.5, "BoltCo");
        
        agreement.addItem(item);
        
        assertEquals(1, agreement.getItems().size(), "Item should be added to agreement");
    }

    // Test 9: SupplierAgreement rejects invalid supply day
    @Test
    public void testAgreementRejectsInvalidDay() {
        SupplierAgreement agreement = new SupplierAgreement(SupplierAgreement.SupplyMethod.FIXED_DAYS);
        
        assertThrows(IllegalArgumentException.class, () -> {
            agreement.addFixedSupplyDay(8); // invalid, must be 1-7
        }, "Day 8 is invalid and should throw exception");
    }

    // Test 10: SupplierManager add and find supplier
    @Test
    public void testManagerAddAndFind() {
        SupplierManager manager = new SupplierManager();
        Supplier s = manager.addSupplier("CMP-100", "Alpha", "bank1", "Net 30");
        
        assertEquals(s, manager.getSupplier(s.getSupplierId()), "Manager should return the exact same supplier by ID");
    }
}