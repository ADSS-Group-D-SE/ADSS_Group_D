package DomainLayerTests;

import CrossCuttingPackage.Notification;
import CrossCuttingPackage.Report;
import InventoryModule.DomainLayer.ShelfLocation;
import SupplierModule.DomainLayer.SupplierAgreement;
import SupplierModule.DomainLayer.SupplierFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Facade-level (integration) tests for {@link SupplierFacade}.
 * Each test exercises the facade together with the SQLite-backed DAO layer.
 */
public class SupplierFacadeTests {

    private SupplierFacade facade;

    @BeforeEach
    void setUp() {
        TestSupport.resetState();
        facade = new SupplierFacade();
    }

    private HashMap<String, Double> priceList(String catalog, double price) {
        HashMap<String, Double> m = new HashMap<>();
        m.put(catalog, price);
        return m;
    }

    @Test
    void testAddAndFindSupplier() {
        String id = facade.AddSupplier("S1", "REG1", "Tnuva", "12-345", "Net30", priceList("C1", 10.0));

        assertEquals("S1", id);
        assertTrue(SupplierFacade.IsSupplierExist("S1"));
        assertEquals("Tnuva", facade.FindSupplier("S1").getName());

        // Adding the same id again must fail.
        assertThrows(RuntimeException.class,
                () -> facade.AddSupplier("S1", "REG1", "Tnuva", "12-345", "Net30", priceList("C1", 10.0)));

        // Looking up a missing supplier must fail.
        assertThrows(Exception.class, () -> facade.FindSupplier("DOES_NOT_EXIST"));
    }

    @Test
    void testRemoveSupplier() {
        facade.AddSupplier("S1", "REG1", "Tnuva", "12-345", "Net30", priceList("C1", 10.0));

        facade.RemoveSupplier("S1");

        assertFalse(SupplierFacade.IsSupplierExist("S1"));
        assertThrows(Exception.class, () -> facade.RemoveSupplier("S1"));
    }

    @Test
    void testAgreementItemManagement() {
        facade.AddSupplier("S1", "REG1", "Tnuva", "12-345", "Net30", priceList("C1", 10.0));

        facade.AddItemToAgreement("S1", "C2", 7.5);
        SupplierAgreement agreement = facade.GetAgreement("S1");
        assertTrue(agreement.getItemsCatalogs().contains("C2"));
        assertEquals(7.5, agreement.GetItemPrice("C2"), 0.0001);

        facade.UpdateItemPriceInAgreement("S1", "C2", 9.0);
        assertEquals(9.0, facade.GetAgreement("S1").GetItemPrice("C2"), 0.0001);

        facade.RemoveItemFromAgreement("S1", "C2");
        assertFalse(facade.GetAgreement("S1").getItemsCatalogs().contains("C2"));
    }

    @Test
    void testContactManagement() {
        facade.AddSupplier("S1", "REG1", "Tnuva", "12-345", "Net30", priceList("C1", 10.0));

        facade.AddContactToSupplier("S1", "Dana", "dana@mail.com", "0501234567");
        Report contacts = facade.ViewContacts("S1");
        assertTrue(contacts.GetReport().contains("Dana"));

        facade.RemoveContactFromSupplier("S1", "Dana");
        assertFalse(facade.ViewContacts("S1").GetReport().contains("Dana"));
    }

    @Test
    void testFixedDeliveryDays() {
        facade.AddSupplier("S1", "REG1", "Tnuva", "12-345", "Net30", priceList("C1", 10.0));
        assertFalse(SupplierFacade.IsSupplierOnFixedDays("S1"));

        facade.AddFixedDelDay("S1", DayOfWeek.MONDAY);
        assertTrue(SupplierFacade.IsSupplierOnFixedDays("S1"));
        assertTrue(SupplierFacade.IsDayInSchedule("S1", DayOfWeek.MONDAY));
        assertFalse(SupplierFacade.IsDayInSchedule("S1", DayOfWeek.TUESDAY));

        facade.RemoveFixedDelDay("S1", DayOfWeek.MONDAY);
        assertFalse(SupplierFacade.IsDayInSchedule("S1", DayOfWeek.MONDAY));
    }

    @Test
    void testGetPricesFromAgreement() {
        HashMap<String, Double> prices = new HashMap<>();
        prices.put("C1", 10.0);
        prices.put("C2", 4.0);
        facade.AddSupplier("S1", "REG1", "Tnuva", "12-345", "Net30", prices);

        HashMap<String, Integer> order = new HashMap<>();
        order.put("C1", 2);
        order.put("C2", 5);

        HashMap<String, Double> effective = SupplierFacade.GetPricesFromAgreement("S1", order);

        // No discount rules -> effective price equals the agreement price.
        assertEquals(10.0, effective.get("C1"), 0.0001);
        assertEquals(4.0, effective.get("C2"), 0.0001);
    }

    @Test
    void testFindBestSupplierPicksCheapest() {
        facade.AddSupplier("S1", "REG1", "ExpensiveCo", "11", "Net30", priceList("C1", 10.0));
        facade.AddSupplier("S2", "REG2", "CheapCo", "22", "Net30", priceList("C1", 5.0));

        Notification n = new Notification("Milk", "C1", new ShelfLocation("A-1"), 10, 0, 0);

        assertEquals("S2", facade.FindBestSupplier(n));
    }

    @Test
    void testFindBestSupplierNoneSellsItem() {
        facade.AddSupplier("S1", "REG1", "Tnuva", "12-345", "Net30", priceList("C1", 10.0));
        Notification n = new Notification("Bread", "C999", new ShelfLocation("B-2"), 10, 0, 0);

        assertThrows(RuntimeException.class, () -> facade.FindBestSupplier(n));
    }
}
