import CrossCuttingPackage.Response;
import SupplierModule.ServiceLayer.SupplierServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.util.HashMap;

/**
 * Seeds example supplier data the first time the system runs on an empty database,
 * so the system's functionality can be exercised out of the box (as required by the
 * assignment). It is a no-op once suppliers already exist, so it never duplicates data.
 *
 * The example set intentionally has the SAME product offered by more than one supplier
 * at different prices (e.g. BEV-001 and BAK-001), which lets the "best supplier" /
 * automatic shortage-order flow be demonstrated.
 */
public final class DataSeeder {

    private static final String URL = "jdbc:sqlite:database.db";

    private DataSeeder() {}

    /** Seeds example suppliers only if the Suppliers table is empty. */
    public static void seedIfEmpty() {
        if (suppliersExist()) {
            return;
        }
        System.out.println("[Seed] Empty database detected - inserting example supplier data...");
        SupplierServices sup = SupplierServices.getInstance();

        // --- Supplier 1: also sells BEV-001 and BAK-001 (overlaps with S002/S003) ---
        HashMap<String, Double> a1 = new HashMap<>();
        a1.put("BEV-001", 5.0);
        a1.put("BEV-002", 4.5);
        a1.put("BAK-001", 3.0);
        check(sup.AddSupplier("S001", "514000001", "Tnuva Distribution", "10-200-111111", "Net 30", a1));
        check(sup.AddContact("S001", "Dana Levi", "dana@tnuva.co.il", "0501112233"));
        check(sup.AddDelvDay("S001", DayOfWeek.SUNDAY));
        check(sup.AddDelvDay("S001", DayOfWeek.WEDNESDAY));
        check(sup.AddDiscountRule("S001", "BEV-001", "Bulk50", 0.10, 50));

        // --- Supplier 2: sells BEV-001 cheaper than S001 (4.2 vs 5.0) ---
        HashMap<String, Double> a2 = new HashMap<>();
        a2.put("BEV-001", 4.2);
        a2.put("BEV-003", 6.0);
        a2.put("BEV-004", 5.5);
        check(sup.AddSupplier("S002", "514000002", "Central Beverages", "10-200-222222", "Net 45", a2));
        check(sup.AddContact("S002", "Yossi Cohen", "yossi@centralbev.co.il", "0502223344"));
        check(sup.AddDelvDay("S002", DayOfWeek.MONDAY));

        // --- Supplier 3: sells BAK-001 cheaper than S001 (2.8 vs 3.0) ---
        HashMap<String, Double> a3 = new HashMap<>();
        a3.put("BAK-001", 2.8);
        a3.put("BAK-002", 3.2);
        a3.put("BAK-003", 1.5);
        check(sup.AddSupplier("S003", "514000003", "Angel Bakeries", "10-200-333333", "EOM", a3));
        check(sup.AddContact("S003", "Rivka Mizrahi", "rivka@angel.co.il", "0503334455"));
        check(sup.AddDelvDay("S003", DayOfWeek.SUNDAY));
        check(sup.AddDelvDay("S003", DayOfWeek.TUESDAY));
        check(sup.AddDiscountRule("S003", "BAK-001", "Bulk100", 0.15, 100));

        System.out.println("[Seed] Example supplier data inserted.");
    }

    private static boolean suppliersExist() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Suppliers")) {
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            // If we cannot tell, do not seed (avoid corrupting an existing DB).
            return true;
        }
    }

    private static void check(Response<String> res) {
        if (res != null && res.isError()) {
            System.out.println("[Seed] warning: " + res.getErrorMsg());
        }
    }
}
