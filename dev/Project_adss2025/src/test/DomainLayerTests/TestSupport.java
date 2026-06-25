package DomainLayerTests;

import InventoryModule.DomainLayer.CategoryFacade;
import SupplierModule.DomainLayer.SupplierFacade;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shared helper that gives every test a clean slate.
 *
 * The facades persist to a single SQLite file ("database.db") and some of them keep
 * static in-memory maps that outlive a single facade instance. Without resetting both,
 * tests interfere with each other (e.g. duplicate primary keys). Call {@link #resetState()}
 * from each test's setup so tests are independent of order.
 */
public final class TestSupport {

    private static final String URL = "jdbc:sqlite:database.db";

    private TestSupport() {}

    /** Empties every database table and clears the facades' static in-memory state. */
    public static void resetState() {
        wipeDatabase();
        clearStaticMap(SupplierFacade.class, "suppliers");
        clearStaticMap(CategoryFacade.class, "categories");
    }

    private static void wipeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement st = conn.createStatement()) {

            List<String> tables = new ArrayList<>();
            try (ResultSet rs = st.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'")) {
                while (rs.next()) {
                    tables.add(rs.getString("name"));
                }
            }
            for (String t : tables) {
                st.executeUpdate("DELETE FROM \"" + t + "\"");
            }
        } catch (Exception e) {
            throw new RuntimeException("TestSupport: failed to wipe database - " + e.getMessage(), e);
        }
    }

    private static void clearStaticMap(Class<?> owner, String fieldName) {
        try {
            Field f = owner.getDeclaredField(fieldName);
            f.setAccessible(true);
            Object value = f.get(null);

            if (value instanceof Map<?, ?>) {
                Map<?, ?> map = (Map<?, ?>) value;
                map.clear();
            }

        } catch (NoSuchFieldException e) {
            // Field renamed/removed - nothing to clear.
        } catch (Exception e) {
            throw new RuntimeException("TestSupport: failed to clear " + owner.getSimpleName()
                    + "." + fieldName + " - " + e.getMessage(), e);
        }
    }
}
