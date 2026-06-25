package SupplierModule.DomainLayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServicesTest {

    private OrderFacade orderFacade;
    private SupplierFacade supplierFacade;

    @BeforeEach
    public void setUp() {
        orderFacade = new OrderFacade();
        supplierFacade = new SupplierFacade();

        orderFacade.CleanData();
        supplierFacade.CleanData();
    }

    @Test
    public void test1_CreateBuyOrderSuccess() {
        HashMap<String, Double> items = new HashMap<>();
        items.put("PROD_100", 15.5);
        supplierFacade.AddSupplier("SUP_01", "51122", "Supplier Main", "Bank12", "Net30", items);

        List<DayOfWeek> days = List.of(DayOfWeek.SUNDAY, DayOfWeek.WEDNESDAY);
        HashMap<String, Integer> amounts = new HashMap<>();
        amounts.put("PROD_100", 50);

        String boId = orderFacade.CreateBuyOrder("SUP_01", amounts, days);

        assertNotNull(boId, "מזהה ה-BuyOrder שנוצר לא אמור להיות null");
    }

    @Test
    public void test2_CreateBuyOrderSupplierDoesNotExist() {
        List<DayOfWeek> days = List.of(DayOfWeek.MONDAY);
        HashMap<String, Integer> amounts = new HashMap<>();
        amounts.put("PROD_100", 10);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderFacade.CreateBuyOrder("NON_EXIST_SUP", amounts, days);
        });

        assertTrue(exception.getMessage().contains("does not exist") || exception.getMessage().contains("found"));
    }

    @Test
    public void test3_UpdateItemInBuyOrderSuccess() {
        HashMap<String, Double> items = new HashMap<>();
        items.put("PROD_200", 10.0);
        supplierFacade.AddSupplier("SUP_02", "123", "Supplier 2", "Bank1", "Cash", items);

        DayOfWeek safeDay = LocalDate.now().plusDays(3).getDayOfWeek();
        List<DayOfWeek> days = List.of(safeDay);

        HashMap<String, Integer> amounts = new HashMap<>();
        amounts.put("PROD_200", 20);

        String boId = orderFacade.CreateBuyOrder("SUP_02", amounts, days);

        assertDoesNotThrow(() -> orderFacade.UpdateItemInBO(boId, "PROD_200", 100));
    }

    @Test
    public void test4_RemoveDayFromBuyOrderThrowsWhenNoDaysLeft() {
        HashMap<String, Double> items = new HashMap<>();
        items.put("PROD_200", 10.0);
        supplierFacade.AddSupplier("SUpP_02", "123", "Supplier 2", "Bank1", "Cash", items);

        List<DayOfWeek> days = new ArrayList<>(List.of(DayOfWeek.TUESDAY));
        HashMap<String, Integer> amounts = new HashMap<>();
        amounts.put("PROD_200", 5);
        String boId = orderFacade.CreateBuyOrder("SUpP_02", amounts, days);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderFacade.RemoveDayFromBuyOrder(boId, DayOfWeek.TUESDAY);
        });

        assertTrue(exception.getMessage().contains("no regular delivery days"));
    }

    @Test
    public void test5_FindBestSupplierForDeficiencyBasedOnLowestPrice() {
        HashMap<String, Double> expensiveItems = new HashMap<>();
        expensiveItems.put("PROD_MIN_01", 25.0);
        supplierFacade.AddSupplier("SUP_EXPENSIVE", "11111", "Expensive Corp", "Bank1", "Net30", expensiveItems);

        HashMap<String, Double> cheapItems = new HashMap<>();
        cheapItems.put("PROD_MIN_01", 18.5);
        supplierFacade.AddSupplier("SUP_CHEAP", "22222", "Cheap Corp", "Bank2", "Net30", cheapItems);

        InventoryModule.DomainLayer.ShelfLocation mockLocation = new InventoryModule.DomainLayer.ShelfLocation("a-12");

        CrossCuttingPackage.Notification inventoryAlert = new CrossCuttingPackage.Notification(
                "Milk 3%", "PROD_MIN_01", mockLocation, 100, 10, 20
        );

        String bestSupplier = supplierFacade.FindBestSupplier(inventoryAlert);

        assertEquals("SUP_CHEAP", bestSupplier);
    }

    @Test
    public void test6_FindBestSupplierWithQuantityDiscount() {
        HashMap<String, Double> itemsA = new HashMap<>();
        itemsA.put("PROD_BULK", 12.0);
        supplierFacade.AddSupplier("SUP_A", "333", "Supplier A", "Bank1", "Cash", itemsA);

        HashMap<String, Double> itemsB = new HashMap<>();
        itemsB.put("PROD_BULK", 14.0);
        supplierFacade.AddSupplier("SUP_B", "444", "Supplier B", "Bank2", "Cash", itemsB);

        supplierFacade.AddDiscountRule("SUP_B", "PROD_BULK", "BulkDiscount", 0.25, 50);

        InventoryModule.DomainLayer.ShelfLocation mockLocation = new InventoryModule.DomainLayer.ShelfLocation("a-12");

        CrossCuttingPackage.Notification largeAlert = new CrossCuttingPackage.Notification(
                "Bulk Item", "PROD_BULK", mockLocation, 150, 40, 20
        );

        String bestSupplier = supplierFacade.FindBestSupplier(largeAlert);

        assertEquals("SUP_B", bestSupplier);
    }

    @Test
    public void test7_DeficiencyAlertWhenNoSupplierOffersProduct() {
        InventoryModule.DomainLayer.ShelfLocation mockLocation = new InventoryModule.DomainLayer.ShelfLocation("a-12");

        CrossCuttingPackage.Notification ghostProductAlert = new CrossCuttingPackage.Notification(
                "Ghost Item", "NOT_A_REAL_PRODUCT", mockLocation, 50, 5, 5
        );

        assertThrows(RuntimeException.class, () -> {
            supplierFacade.FindBestSupplier(ghostProductAlert);
        });
    }

    @Test
    public void test8_CreateUrgentOrderOnNonDeliveryDaySuccess() {
        HashMap<String, Double> items = new HashMap<>();
        items.put("PROD_400", 10.0);
        supplierFacade.AddSupplier("SUP_FIXED", "777", "Fixed Days Inc", "Bank3", "Net60", items);

        DayOfWeek nonToday = LocalDate.now().plusDays(2).getDayOfWeek();
        supplierFacade.AddFixedDelDay("SUP_FIXED", nonToday);

        HashMap<String, Integer> amounts = new HashMap<>();
        amounts.put("PROD_400", 15);

        String orderId = orderFacade.CreateOrder("SUP_FIXED", true, amounts);
        assertNotNull(orderId);
    }

    @Test
    public void test9_CreateNonUrgentOrderOnWrongDayThrowsException() {
        HashMap<String, Double> items = new HashMap<>();
        items.put("PROD_450", 10.0);
        supplierFacade.AddSupplier("SUP_STRICT", "888", "Strict Days Inc", "Bank4", "Net60", items);

        DayOfWeek nonToday = LocalDate.now().plusDays(2).getDayOfWeek();
        supplierFacade.AddFixedDelDay("SUP_STRICT", nonToday);

        HashMap<String, Integer> amounts = new HashMap<>();
        amounts.put("PROD_450", 15);

        assertThrows(RuntimeException.class, () -> {
            orderFacade.CreateOrder("SUP_STRICT", false, amounts);
        });
    }

    @Test
    public void test10_OrderLifecycleTransitionsAndDelivery() {
        HashMap<String, Double> items = new HashMap<>();
        items.put("PROD_500", 5.0);
        supplierFacade.AddSupplier("SUP_LIFECYCLE", "555", "Lifecycle Supplier", "Bank5", "Cash", items);

        HashMap<String, Integer> amounts = new HashMap<>();
        amounts.put("PROD_500", 30);

        String orderId = orderFacade.CreateOrder("SUP_LIFECYCLE", true, amounts);
        assertNotNull(orderId);

        assertDoesNotThrow(() -> orderFacade.PrepareOrder(orderId));
        assertDoesNotThrow(() -> orderFacade.SendOrder(orderId));

        HashMap<String, Integer> deliveredItems = orderFacade.DeliverOrder(orderId);
        assertNotNull(deliveredItems);
        assertEquals(30, deliveredItems.get("PROD_500"));
    }
}