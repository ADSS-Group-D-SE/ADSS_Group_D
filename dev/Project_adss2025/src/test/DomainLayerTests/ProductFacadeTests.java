package DomainLayerTests;

import InventoryModule.DataAccessLayer.*;
import InventoryModule.DomainLayer.CategoryFacade;
import CrossCuttingPackage.Notification;
import InventoryModule.DomainLayer.ProductDL;
import InventoryModule.DomainLayer.ProductFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductFacadeTests
{
    private ProductFacade pFacade;
    private final String defaultDate = "31/12/2030";

    @BeforeEach
    void setUp() {
        pFacade = new ProductFacade();
        pFacade.CleanData();
        new FaultyReportDAO().Clean();
        new ProductPromotionDAO().Clean();
        new ProductDAO().Clean();

        new CategoryHierarchyDAO().Clean();
        new CategoryPromotionDAO().Clean();
        new CategoryDAO().Clean();

    }

    @Test
    void testaddProductWithSameCatalogNumber() throws Exception {
        String catalogNumber = "ABC12";
        pFacade.addProduct("Milk", catalogNumber, "Dairy", "F", "T",
                "Main Warehouse", "a-12", "Tnuva", 10, 50, 4.0, 6.0, 5);

        assertThrows(Exception.class, () -> {
            pFacade.addProduct("Milk", catalogNumber, "Dairy", "F", "T",
                    "Main Warehouse", "a-12", "Tnuva", 10, 50, 4.0, 6.0, 5);
        });
    }

    @Test
    void testInvalidLocationFormat() {
        String invalidLocation = "ABC12";
        assertThrows(IllegalArgumentException.class, () -> {
            pFacade.addProduct("Milk", "101", "Dairy", "F", "T",
                    "Main Warehouse", invalidLocation, "Tnuva", 10, 50, 4.0, 6.0, 5);
        });
    }

    @Test
    void testNegativePrices() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            pFacade.addProduct("Milk", "102", "Dairy", "F", "T",
                    "Main Warehouse", "A-12", "Tnuva", 10, 50, 4.0, -1.0, 5);
        });

        pFacade.addProduct("Bread", "101", "Bakery", "F", "T",
                "Main Warehouse", "A-12", "Angel", 10, 50, 4.0, 6.0, 5);

        assertThrows(IllegalArgumentException.class, () -> {
            pFacade.update("101", null, null, null, -5.0, null, null, null, null);
        });
    }

    @Test
    void testNegativeInventoryAmounts() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            pFacade.addProduct("Milk", "103", "Dairy", "F", "T",
                    "Main Warehouse", "A-12", "Tnuva", -5, 50, 4.0, 6.0, 5);
        });

        String catalogNum = "104";
        pFacade.addProduct("Cheese", catalogNum, "Dairy", "F", "T",
                "Main Warehouse", "A-12", "Tnuva", 10, 50, 4.0, 6.0, 5);

        assertThrows(IllegalArgumentException.class, () -> {
            pFacade.update(catalogNum, null, null, null, null, null, -10, null, null);
        });
    }

    @Test
    void testNegativeMinAmounts() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            pFacade.addProduct("Milk", "103", "Dairy", "F", "T",
                    "Main Warehouse", "A-12", "Tnuva", 5, 50, 4.0, 6.0, -5);
        });

        String catalogNum = "104";
        pFacade.addProduct("Cheese", catalogNum, "Dairy", "F", "T",
                "Main Warehouse", "A-12", "Tnuva", 10, 50, 4.0, 6.0, 5);

        assertThrows(IllegalArgumentException.class, () -> {
            pFacade.update(catalogNum, null, null, null, null, null, null, null, -55);
        });
    }

    @Test
    void testAddAndFindProduct() throws Exception {
        String catalogNum = "123";
        pFacade.addProduct("Milk", catalogNum, "Dairy", "Fridge", "Top",
                "Main Warehouse", "A-1", "Tnuva", 10, 50, 4.0, 6.0, 5);

        ProductDL found = pFacade.FindProductByID(catalogNum);

        assertNotNull(found);
        assertEquals("Milk", found.getName());
        assertEquals(10, found.getAmount_on_shelves());
        assertNotNull(found.getWarehouse());
        assertEquals("Main Warehouse", found.getWarehouse().getName());
    }

    @Test
    void testgetAllProducts() throws Exception {
        pFacade.addProduct("Milk", "101", "Dairy", "Fridge", "Top", "Main Warehouse", "A-1", "Tnuva", 10, 50, 4.0, 6.0, 5);
        pFacade.addProduct("Bread", "102", "Bakery", "Shelf", "Bottom", "Main Warehouse", "B-2", "Angel", 5, 20, 2.0, 4.5, 3);
        pFacade.addProduct("Apple", "103", "Fruit", "Stand", "Middle", "Main Warehouse", "C-3", "Farmer", 50, 100, 1.0, 2.5, 10);

        List<ProductDL> allProducts = pFacade.getAllProducts();

        assertEquals(3, allProducts.size(), "The list should contain exactly 3 products");

        boolean found101 = allProducts.stream().anyMatch(p -> p.getCatalog_number().equals("101"));
        boolean found102 = allProducts.stream().anyMatch(p -> p.getCatalog_number().equals("102"));
        boolean found103 = allProducts.stream().anyMatch(p -> p.getCatalog_number().equals("103"));

        assertTrue(found101, "Product 101 was not found in the list");
        assertTrue(found102, "Product 102 was not found in the list");
        assertTrue(found103, "Product 103 was not found in the list");
    }

    @Test
    void testSetProductDiscount() throws Exception {
        pFacade.addProduct("Milk", "101", "Dairy", "Fridge", "Top", "Main Warehouse", "A-1", "Tnuva", 10, 50, 4.0, 10.0, 5);

        pFacade.AddProductDiscountMod("101", 0.1, defaultDate);
        ProductDL p = pFacade.FindProductByID("101");


    }

    @Test
    void testGetProductPrice() throws Exception {
        CategoryFacade cFacade = new CategoryFacade();
        cFacade.AddCategory("DairyPrice", 0, defaultDate);

        pFacade.addProduct("Milk", "101", "DairyPrice", "Fridge", "Top", "Main Warehouse", "A-1", "Tnuva", 10, 50, 10.0, 10.0, 5);

        pFacade.AddProductDiscountMod("101", 0.1, defaultDate);

        assertEquals(9.0, pFacade.GetProductPrice("101"), 0.001);
    }

    @Test
    void testReportFaultyProduct() throws Exception {
        pFacade.addProduct("Milk", "101", "Dairy", "F", "T", "Main Warehouse", "A-12", "Tnuva", 10, 50, 4.0, 6.0, 5);
        pFacade.addProduct("Bread", "123", "Bakery", "S", "B", "Main Warehouse", "B-2", "Angel", 20, 100, 2.0, 4.0, 10);

        Integer reportId1 = pFacade.ReportFaultyProduct("101", "A-12", "Broken bottle");
        Integer reportId2 = pFacade.ReportFaultyProduct("101", "A-12", "Leaking");
        Integer reportId3 = pFacade.ReportFaultyProduct("123", "B-2", "Expired");

        assertNotEquals(reportId1, reportId2);
        assertNotNull(reportId3);

        assertNotNull(pFacade.FindProductByReportId(reportId1));
        assertEquals("Broken bottle", pFacade.FindProductByReportId(reportId1).getDescription());

        assertEquals("101", pFacade.FindProductByReportId(reportId2).getCatalog_number());
        assertEquals("Bread", pFacade.FindProductByReportId(reportId3).getName());
    }

    @Test
    void testCreateFaultyReport() throws Exception {
        String defaultDate = "31/12/2030";

        pFacade.addProduct("Milk", "101", "Dairy", "F", "T", "Main Warehouse", "A-12", "Tnuva", 10, 50, 4.0, 6.0, 5);
        pFacade.addProduct("Bread", "123", "Bakery", "S", "B", "Main Warehouse", "B-2", "Angel", 20, 100, 2.0, 4.0, 10);

        Integer reportId1 = pFacade.ReportFaultyProduct("101", "A-12", "Broken bottle");
        Integer reportId2 = pFacade.ReportFaultyProduct("101", "A-12", "Leaking");
        Integer reportId3 = pFacade.ReportFaultyProduct("123", "B-2", "Expired");

        String report = pFacade.CreateFaultyReport("01/01/2020", "31/12/2030").GetReport();

        assertNotNull(report);
        assertTrue(report.contains("Fault product reports"));
        assertTrue(report.contains("Broken bottle"), "Report should contain 'Broken bottle'");
        assertTrue(report.contains("Leaking"), "Report should contain 'Leaking'");
        assertTrue(report.contains("Expired"), "Report should contain 'Expired'");
        assertTrue(report.contains("101"));
        assertTrue(report.contains("123"));

        assertTrue(report.contains(Integer.toString(reportId1)));
        assertTrue(report.contains(Integer.toString(reportId2)));
        assertTrue(report.contains(Integer.toString(reportId3)));
    }
    @Test
    void testPurchaseProduct() throws Exception {
        String catalogNum = "200";
        pFacade.addProduct("Milk", catalogNum, "Dairy", "F", "T", "Main Warehouse", "A-12", "Tnuva", 10, 50, 4.0, 6.0, 5);

        pFacade.PurchaseProduct(catalogNum, 3, 10);

        ProductDL updatedProduct = pFacade.FindProductByID(catalogNum);
        assertEquals(7, updatedProduct.getAmount_on_shelves(), "Shelves amount should be 7");
        assertEquals(40, updatedProduct.getAmount_on_stock(), "Stock amount should be 40");

        pFacade.PurchaseProduct(catalogNum, -5, 0);

        assertEquals(12, pFacade.FindProductByID(catalogNum).getAmount_on_shelves(), "Shelves amount should increase to 12");
    }

    @Test
    void testGetProductsWarnings() throws Exception {
        pFacade.addProduct("Milk", "101", "Dairy", "F", "T", "Main Warehouse", "A-12", "Tnuva", 10, 50, 4.0, 6.0, 5);
        pFacade.addProduct("Bread", "102", "Bakery", "S", "B", "Main Warehouse", "B-2", "Angel", 1, 2, 2.0, 4.0, 10);
        pFacade.addProduct("Cheese", "103", "Dairy", "F", "T", "Main Warehouse", "C-3", "Tnuva", 2, 3, 5.0, 8.0, 5);

        List<Notification> warnings = pFacade.GetProductsWarnings();

        assertFalse(warnings.isEmpty(), "Warning list should not be empty");

        boolean foundOkItem = warnings.stream().anyMatch(p -> p.getCatalog_number().equals("101"));
        assertFalse(foundOkItem, "Product 101 has enough stock and should NOT be in warnings");

        boolean foundWarningItem = warnings.stream().anyMatch(p -> p.getCatalog_number().equals("102"));
        assertTrue(foundWarningItem, "Product 102 is low on stock and SHOULD be in warnings");

        if (!warnings.isEmpty()) {
            assertNotSame(pFacade.FindProductByID("102"), warnings.get(0));
        }
    }

    @Test
    void testGetInventoryReportByCategory() throws Exception {
        pFacade.addProduct("Milk", "101", "Dairy", "F", "T", "Main Warehouse", "A-1", "Tnuva", 10, 50, 4.0, 6.0, 5);
        pFacade.addProduct("Cheese", "102", "Dairy", "F", "T", "Main Warehouse", "A-2", "Tara", 5, 20, 10.0, 15.0, 2);
        pFacade.addProduct("Bread", "201", "Bakery", "S", "B", "Main Warehouse", "B-1", "Angel", 20, 100, 2.0, 4.0, 10);
        pFacade.addProduct("Apple", "301", "Fruit", "Stand", "M", "Main Warehouse", "C-1", "Farmer", 50, 0, 1.0, 2.0, 10);

        List<String> categoriesToReport = List.of("Dairy", "Bakery");

        String report = pFacade.GetInventoryReportByCategory(categoriesToReport).GetReport();

        assertNotNull(report);
        assertTrue(report.contains("Dairy"));
        assertTrue(report.contains("Bakery"));
        assertTrue(report.contains("Milk"));
        assertTrue(report.contains("Cheese"));
        assertTrue(report.contains("Bread"));

        assertFalse(report.contains("Apple"), "Product from non-requested category should not be in report");

        String reportWithEmptyCat = pFacade.GetInventoryReportByCategory(List.of("Meat")).GetReport();
        assertTrue(reportWithEmptyCat.contains("No Products"), "Should display 'No Products' for empty category");
    }
}