package PresentationLayer;

import DomainLayer.*;
import DomainLayer.SupplierAgreement.SupplyMethod;
import DomainLayer.SupplierOrder.OrderStatus;
import ServiceLayer.SupplierService;

import java.time.LocalDate;
import java.util.Scanner;

public class SystemInitializer {

    private SupplierService service;

    public SystemInitializer(SupplierService service) {
        this.service = service;
    }

    /**
     * Loads sample data into the system for testing.
     * This function is external to the Domain Layer — it uses the service layer
     * to make appropriate calls and does not access domain data directly.
     */
    public void loadSampleData() {
        // ── Supplier 1: "Osem" — Fixed supply days ──────────
        Supplier s1 = service.addSupplier("510000001", "Osem Supplies", "12-345-678901",
                new PaymentTerms("Net", 30));
        int s1Id = s1.getSupplierId();

        service.addContactPerson(s1Id, "Yossi Cohen", "050-1234567", "yossi@osem.co.il");
        service.addContactPerson(s1Id, "Dana Levy", "052-7654321", "dana@osem.co.il");

        java.util.List<Integer> days1 = java.util.Arrays.asList(1, 4); // Sunday, Wednesday
        service.createAgreement(s1Id, SupplyMethod.FIXED_DAYS, days1, 0);

        service.addItemToAgreement(s1Id, 1001, 101, "Bamba Snack 80g", 3.50, "Osem");
        service.addQuantityDiscount(s1Id, 1001, 100, 5);
        service.addQuantityDiscount(s1Id, 1001, 500, 10);
        service.addQuantityDiscount(s1Id, 1001, 1000, 15);

        service.addItemToAgreement(s1Id, 1002, 102, "Bissli Grill 200g", 5.00, "Osem");
        service.addQuantityDiscount(s1Id, 1002, 50, 3);
        service.addQuantityDiscount(s1Id, 1002, 200, 8);

        service.addItemToAgreement(s1Id, 1003, 103, "Ketchup 750ml", 8.90, "Osem");

        // ── Supplier 2: "Tnuva" — On order ─────────────────
        Supplier s2 = service.addSupplier("520000002", "Tnuva Dairy", "12-999-888777",
                new PaymentTerms("Net", 60));
        int s2Id = s2.getSupplierId();

        service.addContactPerson(s2Id, "Avi Amar", "054-1112233", "avi@tnuva.co.il");

        service.createAgreement(s2Id, SupplyMethod.ON_ORDER, null, 3);

        service.addItemToAgreement(s2Id, 2001, 201, "Milk 1L 3%", 5.90, "Tnuva");
        service.addQuantityDiscount(s2Id, 2001, 100, 4);
        service.addQuantityDiscount(s2Id, 2001, 500, 9);

        service.addItemToAgreement(s2Id, 2002, 202, "Cottage Cheese 250g", 7.50, "Tnuva");
        service.addQuantityDiscount(s2Id, 2002, 200, 6);

        // Same internal item (103 = Ketchup) from different supplier
        service.addItemToAgreement(s2Id, 2003, 103, "Ketchup 750ml", 9.20, "Heinz");

        // ── Supplier 3: "Strauss" — Self pickup ─────────────
        Supplier s3 = service.addSupplier("530000003", "Strauss Group", "10-555-666444",
                new PaymentTerms("Net", 45));
        int s3Id = s3.getSupplierId();

        service.addContactPerson(s3Id, "Miri Ben-David", "053-4445566", "miri@strauss.co.il");
        service.addContactPerson(s3Id, "Ronen Shapira", "050-9998877", "ronen@strauss.co.il");

        service.createAgreement(s3Id, SupplyMethod.SELF_PICKUP, null, 0);

        service.addItemToAgreement(s3Id, 3001, 301, "Elite Coffee 200g", 22.00, "Strauss");
        service.addQuantityDiscount(s3Id, 3001, 50, 5);
        service.addQuantityDiscount(s3Id, 3001, 200, 12);

        service.addItemToAgreement(s3Id, 3002, 302, "Milky Pudding 4-pack", 12.50, "Strauss");

        // ══════════════════════════════════════════════════════════
        // Mock Orders (Order History)
        // ══════════════════════════════════════════════════════════

        // Order 1: Past delivered order from Osem (200 Bamba + 100 Bissli)
        SupplierOrder order1 = service.createOrder(s1Id, false);
        service.addItemToOrder(order1.getOrderId(), 1001, 200); // Bamba, 200 units -> 5% discount
        service.addItemToOrder(order1.getOrderId(), 1002, 100); // Bissli, 100 units -> 3% discount
        SupplierOrder finalized1 = service.finalizeOrder(order1.getOrderId());
        finalized1.setOrderDate(LocalDate.now().minusDays(14)); // Backdate for history
        finalized1.setExpectedDeliveryDate(LocalDate.now().minusDays(12));
        finalized1.markDelivered();

        // Order 2: Past delivered order from Tnuva (500 Milk)
        SupplierOrder order2 = service.createOrder(s2Id, false);
        service.addItemToOrder(order2.getOrderId(), 2001, 500); // Milk, 500 units -> 9% discount
        SupplierOrder finalized2 = service.finalizeOrder(order2.getOrderId());
        finalized2.setOrderDate(LocalDate.now().minusDays(7));
        finalized2.setExpectedDeliveryDate(LocalDate.now().minusDays(4));
        finalized2.markDelivered();

        // Order 3: Current pending order from Osem (1000 Bamba — big order)
        SupplierOrder order3 = service.createOrder(s1Id, false);
        service.addItemToOrder(order3.getOrderId(), 1001, 1000); // Bamba, 1000 units -> 15% discount
        service.addItemToOrder(order3.getOrderId(), 1003, 50);   // Ketchup, 50 units -> no discount
        service.finalizeOrder(order3.getOrderId());

        // Order 4: Urgent order from Strauss (50 Elite Coffee)
        SupplierOrder order4 = service.createOrder(s3Id, true);
        service.addItemToOrder(order4.getOrderId(), 3001, 50); // Coffee, 50 units -> 5% discount
        service.finalizeOrder(order4.getOrderId());

        // ══════════════════════════════════════════════════════════
        // Freeze Strauss agreement to demonstrate the feature
        // ══════════════════════════════════════════════════════════
        service.freezeAgreement(s3Id);

        System.out.println("Sample data loaded successfully.");
        System.out.println("Loaded " + service.getSupplierCount() + " suppliers.");
        System.out.println("Loaded " + service.getOrderCount() + " orders.");
    }

    /**
     * Main entry point for the application.
     */
    public static void main(String[] args) {
        SupplierManager supplierManager = new SupplierManager();
        OrderManager orderManager = new OrderManager();
        SupplierService service = new SupplierService(supplierManager, orderManager);

        Scanner scanner = new Scanner(System.in);
        System.out.println("==========================================");
        System.out.println("  Welcome to Super-Lee Supplier System");
        System.out.println("==========================================");
        System.out.print("Load sample data? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (answer.equals("y") || answer.equals("yes")) {
            SystemInitializer initializer = new SystemInitializer(service);
            initializer.loadSampleData();
        } else {
            System.out.println("Starting with empty system.");
        }

        System.out.println();
        SuppliersUI ui = new SuppliersUI(service, scanner);
        ui.start();
    }
}
