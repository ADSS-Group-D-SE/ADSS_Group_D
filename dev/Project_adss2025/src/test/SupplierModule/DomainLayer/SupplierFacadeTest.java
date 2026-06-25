package SupplierModule.DomainLayer;


import CrossCuttingPackage.Report;
import InventoryModule.DataAccessLayer.*;
import SupplierModule.DataAccessLayer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SupplierFacadeTest {

    private SupplierFacade supplierFacade;

    @BeforeEach
    public void setUp() {
        supplierFacade = new SupplierFacade();
        supplierFacade.CleanData();

    }

    @Test
    public void test1_AddSupplierSuccess() {
        HashMap<String, Double> catalog = new HashMap<>();
        catalog.put("PROD_1", 10.0);

        String supId = supplierFacade.AddSupplier(
                "SUP_01", "5112233", "Osem", "BankLeumi-123", "Net30", catalog
        );

        assertEquals("SUP_01", supId);
    }
    @Test
    public void test2_AddSupplierAlreadyExistsThrows() {
        HashMap<String, Double> catalog = new HashMap<>();
        catalog.put("PROD_T2", 10.0);

        supplierFacade.AddSupplier("SUP_02", "5112233", "Osem", "BankLeumi-123", "Net30", catalog);

        assertThrows(RuntimeException.class, () -> {
            supplierFacade.AddSupplier("SUP_02", "999999", "Duplicate", "Bank-1", "Cash", catalog);
        });
    }

    @Test
    public void test3_RemoveSupplierSuccess() {
        HashMap<String, Double> catalog = new HashMap<>();
        catalog.put("PROD_T3", 10.0);
        supplierFacade.AddSupplier("SUP_03", "5112233", "Osem", "BankLeumi-123", "Net30", catalog);

        supplierFacade.RemoveSupplier("SUP_03");

        assertThrows(NoSuchElementException.class, () -> {
            supplierFacade.FindSupplier("SUP_03");
        });
    }

    @Test
    public void test4_AddItemAndUpdatePriceInAgreement() {
        HashMap<String, Double> catalog = new HashMap<>();
        catalog.put("PROD_T4_1", 10.0);
        supplierFacade.AddSupplier("SUP_04", "5112233", "Osem", "BankLeumi-123", "Net30", catalog);

        assertDoesNotThrow(() -> supplierFacade.AddItemToAgreement("SUP_04", "PROD_T4_2", 15.5));
        assertDoesNotThrow(() -> supplierFacade.UpdateItemPriceInAgreement("SUP_04", "PROD_T4_1", 12.5));
    }

    @Test
    public void test5_UpdatePriceForItemNotInAgreementThrows() {
        HashMap<String, Double> catalog = new HashMap<>();
        catalog.put("PROD_T5", 10.0);
        supplierFacade.AddSupplier("SUP_05", "5112233", "Osem", "BankLeumi-123", "Net30", catalog);

        assertThrows(RuntimeException.class, () -> {
            supplierFacade.UpdateItemPriceInAgreement("SUP_05", "GHOST_PROD", 50.0);
        });
    }

    @Test
    public void test6_AddAndRemoveContactSuccess() {
        HashMap<String, Double> catalog = new HashMap<>();
        supplierFacade.AddSupplier("SUP_06", "5112233", "Osem", "BankLeumi-123", "Net30", catalog);

        supplierFacade.AddContactToSupplier("SUP_06", "Yossi", "yossi@osem.com", "0501234567");

        Report contactsReport = supplierFacade.ViewContacts("SUP_06");
        assertNotNull(contactsReport);

        assertTrue(contactsReport.GetReport().contains("Yossi"));

        assertDoesNotThrow(() -> supplierFacade.RemoveContactFromSupplier("SUP_06", "Yossi"));
    }

    @Test
    public void test7_AddAndRemoveFixedDeliveryDays() {
        HashMap<String, Double> catalog = new HashMap<>();
        supplierFacade.AddSupplier("SUP_07", "5112233", "Osem", "BankLeumi-123", "Net30", catalog);

        assertDoesNotThrow(() -> supplierFacade.AddFixedDelDay("SUP_07", DayOfWeek.SUNDAY));
        assertTrue(SupplierFacade.IsDayInSchedule("SUP_07", DayOfWeek.SUNDAY));

        assertDoesNotThrow(() -> supplierFacade.RemoveFixedDelDay("SUP_07", DayOfWeek.SUNDAY));
    }

    @Test
    public void test8_ManageDiscountRulesSuccess() {
        HashMap<String, Double> catalog = new HashMap<>();
        catalog.put("PROD_T8", 100.0);
        supplierFacade.AddSupplier("SUP_08", "5112233", "Osem", "BankLeumi-123", "Net30", catalog);

        assertDoesNotThrow(() -> supplierFacade.AddDiscountRule("SUP_08", "PROD_T8", "QuantityDiscount", 0.10, 20));
        assertDoesNotThrow(() -> supplierFacade.UpdateDiscountRuleDiscount("SUP_08", "PROD_T8", "QuantityDiscount", 0.15));
        assertDoesNotThrow(() -> supplierFacade.UpdateDiscountRuleMin("SUP_08", "PROD_T8", "QuantityDiscount", 30));
        assertDoesNotThrow(() -> supplierFacade.RemoveDiscountRule("SUP_08", "PROD_T8", "QuantityDiscount"));
    }
}