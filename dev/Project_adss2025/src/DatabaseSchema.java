import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the database schema if it does not already exist.
 *
 * The application stores all data in a local SQLite file ("database.db"). Calling
 * {@link #ensure()} once at startup guarantees every table exists, so the program runs
 * on a clean install (empty or missing database file) without shipping a prebuilt DB.
 * Existing data is never touched (CREATE TABLE IF NOT EXISTS is a no-op when the table is there).
 */
public final class DatabaseSchema {

    private static final String URL = "jdbc:sqlite:database.db";

    private DatabaseSchema() {}

    private static final String[] TABLES = {
        """
        CREATE TABLE IF NOT EXISTS "Categories" (
            "category_id" TEXT, "name" TEXT, "type" TEXT,
            PRIMARY KEY("category_id"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "CategoryHierarchy" (
            "parent_id" TEXT, "sub_category_id" TEXT,
            PRIMARY KEY("parent_id","sub_category_id"),
            FOREIGN KEY("parent_id") REFERENCES "Categories"("category_id"),
            FOREIGN KEY("sub_category_id") REFERENCES "Categories"("category_id"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "Products" (
            "catalog_number" TEXT, "name" TEXT, "mainCategory" TEXT, "subCategory" TEXT,
            "subSubCategory" TEXT, "warehouse" NUMERIC, "location" NUMERIC, "manufacturer" TEXT,
            "amount_on_shelves" INTEGER, "amount_on_stock" INTEGER, "price_to_consumer" REAL,
            "price_to_supply" REAL, "supplier_discount" REAL, "minAmountAlert" TEXT,
            PRIMARY KEY("catalog_number"),
            FOREIGN KEY("mainCategory") REFERENCES "Categories"("category_id"),
            FOREIGN KEY("subCategory") REFERENCES "Categories"("category_id"),
            FOREIGN KEY("subSubCategory") REFERENCES "Categories"("category_id"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "PromotionProducts" (
            "id" TEXT, "discountPre" REAL, "catalog_number" TEXT, "endDate" TEXT, "scope" TEXT,
            PRIMARY KEY("id"),
            FOREIGN KEY("catalog_number") REFERENCES "Products"("catalog_number"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "PromotionCategories" (
            "id" TEXT, "discountPre" REAL, "category_id" TEXT, "endDate" TEXT, "scope" TEXT,
            PRIMARY KEY("id"),
            FOREIGN KEY("category_id") REFERENCES "Categories"("category_id"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "FaultyProductReports" (
            "report_id" INTEGER, "name" TEXT, "catalog_number" TEXT, "location" TEXT,
            "description" TEXT, "dateOnReport" TEXT,
            PRIMARY KEY("report_id"),
            FOREIGN KEY("catalog_number") REFERENCES "Products"("catalog_number"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "Suppliers" (
            "supplierId" TEXT, "name" TEXT, "regNumber" TEXT, "bankAccount" TEXT,
            "payTerms" TEXT, "ddc" TEXT,
            PRIMARY KEY("supplierId"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "ContactInfo" (
            "supplierId" TEXT, "name" TEXT, "email" TEXT, "phoneNumber" TEXT,
            PRIMARY KEY("supplierId","name"),
            FOREIGN KEY("supplierId") REFERENCES "Suppliers"("supplierId"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "SupplierAgreements" (
            "supplierId" TEXT, "catalog_number" TEXT, "price" REAL,
            PRIMARY KEY("supplierId","catalog_number"),
            FOREIGN KEY("supplierId") REFERENCES "Suppliers"("supplierId"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "DiscountRules" (
            "supplierId" TEXT, "name" TEXT, "catalog_number" TEXT, "discount_pre" REAL, "minAmount" INTEGER,
            PRIMARY KEY("supplierId","name"),
            FOREIGN KEY("supplierId") REFERENCES "Suppliers"("supplierId"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "SupplierOrders" (
            "orderId" TEXT, "supplierId" TEXT, "orderDate" TEXT, "orderStatus" TEXT,
            PRIMARY KEY("orderId"),
            FOREIGN KEY("supplierId") REFERENCES "Suppliers"("supplierId"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "SupplierItems" (
            "orderId" TEXT, "catalog_number" TEXT, "amount" INTEGER, "price" REAL,
            PRIMARY KEY("orderId","catalog_number"),
            FOREIGN KEY("orderId") REFERENCES "SupplierOrders"("orderId"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "BuyOrders" (
            "boId" TEXT, "supId" TEXT, "regularDays" TEXT, "nextDeliveryDate" TEXT,
            PRIMARY KEY("boId"),
            FOREIGN KEY("supId") REFERENCES "Suppliers"("supplierId"))
        """,
        """
        CREATE TABLE IF NOT EXISTS "BuyOrderItems" (
            "boId" TEXT, "catalog_number" TEXT, "amount" INTEGER,
            PRIMARY KEY("boId","catalog_number"),
            FOREIGN KEY("boId") REFERENCES "BuyOrders"("boId"))
        """
    };

    /** Creates any missing tables. Safe to call on every startup. */
    public static void ensure() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement st = conn.createStatement()) {
            for (String ddl : TABLES) {
                st.executeUpdate(ddl);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DatabaseSchema:ensure - failed to create schema: " + e.getMessage(), e);
        }
    }
}
