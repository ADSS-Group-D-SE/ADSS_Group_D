package DomainLayerTests;

import SupplierModule.DomainLayer.BuyOrder;
import SupplierModule.DomainLayer.OrderFacade;
import SupplierModule.DomainLayer.SupplierFacade;
import SupplierModule.DomainLayer.SupplierOrder;
import SupplierModule.DomainLayer.SupplierOrder.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Facade-level (integration) tests for {@link OrderFacade}.
 * Orders flow through OrderFacade -> SupplierFacade agreement pricing -> SQLite DAO layer,
 * so these tests cover the cross-module order lifecycle end to end.
 */
public class OrderFacadeTests {

    private OrderFacade orderFacade;
    private SupplierFacade supplierFacade;

    @BeforeEach
    void setUp() {
        TestSupport.resetState();
        supplierFacade = new SupplierFacade();
        orderFacade = new OrderFacade();

        HashMap<String, Double> prices = new HashMap<>();
        prices.put("C1", 10.0);
        prices.put("C2", 4.0);
        supplierFacade.AddSupplier("S1", "REG1", "Tnuva", "12-345", "Net30", prices);
    }

    private HashMap<String, Integer> items(String a, int amount) {
        HashMap<String, Integer> m = new HashMap<>();
        m.put(a, amount);
        return m;
    }

    @Test
    void testCreateOrderAndFind() {
        HashMap<String, Integer> toOrder = new HashMap<>();
        toOrder.put("C1", 2);
        toOrder.put("C2", 5);

        String orderId = orderFacade.CreateOrder("S1", true, toOrder);

        SupplierOrder found = orderFacade.FindOrderById(orderId);
        assertEquals(OrderStatus.PENDING, found.getStatus());
        assertEquals(2, found.getItems().size());
    }

    @Test
    void testOrderLifecycle() {
        String orderId = orderFacade.CreateOrder("S1", true, items("C1", 2));

        orderFacade.PrepareOrder(orderId);
        assertEquals(OrderStatus.PREP, orderFacade.FindOrderById(orderId).getStatus());

        orderFacade.SendOrder(orderId);
        assertEquals(OrderStatus.SENT, orderFacade.FindOrderById(orderId).getStatus());

        HashMap<String, Integer> delivered = orderFacade.DeliverOrder(orderId);
        assertEquals(OrderStatus.DELIVERED, orderFacade.FindOrderById(orderId).getStatus());
        assertEquals(2, delivered.get("C1").intValue());
    }

    @Test
    void testCannotSendBeforePrepare() {
        String orderId = orderFacade.CreateOrder("S1", true, items("C1", 2));
        assertThrows(Exception.class, () -> orderFacade.SendOrder(orderId));
    }

    @Test
    void testNonUrgentOrderRejectedOnNonScheduledDay() {
        DayOfWeek notToday = LocalDate.now().getDayOfWeek().plus(1);
        supplierFacade.AddFixedDelDay("S1", notToday);

        assertThrows(RuntimeException.class, () -> orderFacade.CreateOrder("S1", false, items("C1", 2)));
    }

    @Test
    void testCreateOrderForItemNotInAgreement() {
        assertThrows(RuntimeException.class, () -> orderFacade.CreateOrder("S1", true, items("C_UNKNOWN", 2)));
    }

    @Test
    void testRemoveOrder() {
        String orderId = orderFacade.CreateOrder("S1", true, items("C1", 2));

        orderFacade.RemoveOrder(orderId);

        assertFalse(orderFacade.ViewAllOrders().GetReport().contains(orderId));
    }

    @Test
    void testBuyOrderLifecycle() {
        String boId = orderFacade.CreateBuyOrder("S1", items("C1", 3),
                List.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY));

        BuyOrder bo = orderFacade.FindBuyOrderById(boId);
        assertEquals("S1", bo.getSupId());
        assertEquals(3, bo.getItems().get("C1").intValue());

        orderFacade.AddItemToBuyOrder(boId, "C2", 7);
        assertEquals(7, orderFacade.FindBuyOrderById(boId).getItems().get("C2").intValue());

        orderFacade.DeleteBuyOrder(boId);
        assertThrows(Exception.class, () -> orderFacade.FindBuyOrderById(boId));
    }

    @Test
    void testCreateBuyOrderForUnknownSupplierFails() {
        assertThrows(RuntimeException.class,
                () -> orderFacade.CreateBuyOrder("NO_SUCH_SUPPLIER", items("C1", 3), List.of(DayOfWeek.MONDAY)));
    }
}
