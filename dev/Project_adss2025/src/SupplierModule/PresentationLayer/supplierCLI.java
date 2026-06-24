package SupplierModule.PresentationLayer;

import CrossCuttingPackage.Report;
import CrossCuttingPackage.Response;
import InventoryModule.ServiceLayer.ProductServices;
import SupplierModule.ServiceLayer.OrderServices;
import SupplierModule.ServiceLayer.SupplierServices;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class supplierCLI {
    private final Scanner scanner;
    private final SupplierServices supplierServices;
    private final OrderServices orderServices;

    public supplierCLI(Boolean shouldLoad,Scanner scanner){
        this.scanner = scanner;
        this.supplierServices = SupplierServices.getInstance();
        this.orderServices = OrderServices.getInstance();


        if(shouldLoad)
        {
            Response<String> res= supplierServices.Load();
            if(res.isError())
                throw new RuntimeException("Could not build data - " + res.getErrorMsg());
            res= orderServices.LoadData();
            if(res.isError())
                throw new RuntimeException("Could not build data - " + res.getErrorMsg());
        }
    }

    public void Clean()
    {
        supplierServices.Clean();
        orderServices.Clean();
    }
    public void start() {
        System.out.println("--- Welcome to ADSS Supplier Management System ---");
        while (true) {
            try {
                displayMenu();
                String choice = scanner.nextLine();
                if (choice.equals("0")) {
                    System.out.println("Exiting Supplier System...");
                    break;
                } else {
                    handleChoice(choice);
                }
            } catch (Exception e) {
                if (e.getMessage() != null)
                    System.out.println("Client error: " + e.getMessage());
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1.  Add New Supplier");
        System.out.println("2.  Remove Supplier");
        System.out.println("3.  Add Contact Person to Supplier");
        System.out.println("4.  Remove Contact Person");
        System.out.println("5.  Edit Contact Details (Name/Email/Phone)");
        System.out.println("6.  Edit Supplier General Details (Name/Bank/Payment Terms/Reg Number)");
        System.out.println("7.  View Supplier Contacts Report");
        System.out.println("8.  Add Fixed Delivery Day");
        System.out.println("9.  Remove Fixed Delivery Day");
        System.out.println("10. Add Item to Agreement");
        System.out.println("11. Remove Item from Agreement");
        System.out.println("12. Update Item Price in Agreement");
        System.out.println("13. Order Management Menu");
        System.out.println("14. View All Suppliers");
        System.out.println("15. Discount Rule Menu");
        System.out.println("16. Load Supplier Test Data");
        System.out.println("0.  Exit");
        System.out.print("Please enter your choice: ");
    }

    private void handleChoice(String num) {
        switch (num) {
            case "1":
                handleAddSupplier();
                break;
            case "2":
                handleRemoveSupplier();
                break;
            case "3":
                handleAddContact();
                break;
            case "4":
                handleRemoveContact();
                break;
            case "5":
                handleEditContactMenu();
                break;
            case "6":
                handleEditSupplierMenu();
                break;
            case "7":
                handleViewContacts();
                break;
            case "8":
                handleAddDeliveryDay();
                break;
            case "9":
                handleRemoveDeliveryDay();
                break;
            case "10":
                handleAddItemToAgreement();
                break;
            case "11":
                handleRemoveItemFromAgreement();
                break;
            case "12":
                handleUpdateItemPriceInAgreement();
                break;
            case "13":
                handleOrderMenu();
                break;
            case "14":
                HandleSuppliersReport();
                break;
            case "15":
                HandleDiscountMenu();
                break;
            case "16":
                CreateSupplierTestData();
                break;
            default:
                System.out.println("Invalid input. Please choose a number between 0 and 9.");
                break;
        }
    }

    private void handleOrderMenu() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println(">>> Order Management Menu");
            System.out.println("=========================================");
            System.out.println("1. Create New Order");
            System.out.println("2. Remove/Delete Order");
            System.out.println("3. View All System Orders");
            System.out.println("4. View Orders By Supplier ID");
            System.out.println("5. View Orders By Date Range");
            System.out.println("6. Update Order Status (Prepare/Send/Cancel/Deliver)");
            System.out.println("0. Back to Supplier Main Menu");
            System.out.print("Please enter your choice: ");

            String choice = scanner.nextLine();
            if (choice.equals("0")) {
                break;
            }

            handleOrderChoice(choice);
        }
    }


    private void handleOrderChoice(String choice) {
        switch (choice) {
            case "1":
                handleCreateOrder();
                break;
            case "2":
                handleRemoveOrder();
                break;
            case "3":
                handleViewAllOrders();
                break;
            case "4":
                handleViewOrdersBySupplier();
                break;
            case "5":
                handleViewOrdersByDateRange();
                break;
            case "6":
                handleUpdateOrderStatusMenu();
                break;
            default:
                System.out.println("[!] Invalid choice. Please select 0-6.");
                break;
        }
    }

    private void handleCreateOrder() {
        System.out.println("\n--- Create New Order ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();

        Response<HashMap<String, Double>> itemsRes = supplierServices.getSupplierItems(supId);

        if (itemsRes.isError() || itemsRes.getReturnValue() == null || itemsRes.getReturnValue().isEmpty()) {
            System.out.println("[!] ERROR: Could not find items for this supplier: " + itemsRes.getErrorMsg());
            return;
        }

        HashMap<String, Double> catalogMap = itemsRes.getReturnValue();

        ArrayList<String> availableItems = new ArrayList<>(catalogMap.keySet());

        System.out.print("Is this order urgent? (yes/no): ");
        boolean isUrgent = scanner.nextLine().equalsIgnoreCase("yes");

        HashMap<String, Integer> amounts = new HashMap<>();

        System.out.println("\n--- Available Items for Supplier " + supId + " ---");

        while (true) {
            System.out.println("Select an item by its number (or type 'done' to finish):");
            for (int i = 0; i < availableItems.size(); i++) {
                String itemId = availableItems.get(i);
                double itemPrice = catalogMap.get(itemId);

                System.out.println((i + 1) + ". " + itemId + " - Price: " + itemPrice);
            }

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("done")) {
                if (amounts.isEmpty()) {
                    System.out.println("[!] You must add at least one item to create an order.");
                    continue;
                }
                break;
            }

            int itemIndex;
            try {
                itemIndex = Integer.parseInt(choice) - 1;
                if (itemIndex < 0 || itemIndex >= availableItems.size()) {
                    System.out.println("[!] Invalid option. Please select a number from the list.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a number or 'done'.");
                continue;
            }

            String itemId = availableItems.get(itemIndex);

            if (amounts.containsKey(itemId)) {
                System.out.println("[!] This item is already in your order. Overwriting existing details.");
            }

            System.out.print("Enter Quantity for " + itemId + ": ");
            int amount;
            try {
                amount = Integer.parseInt(scanner.nextLine());
                if (amount <= 0) {
                    System.out.println("[!] Quantity must be greater than 0. Item not added.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid quantity format. Item not added.");
                continue;
            }

            amounts.put(itemId, amount);
            System.out.println("[V] Added " + amount + " units of " + itemId + " to the order.\n");
        }


        Response<String> res = orderServices.CreateOrder(supId, isUrgent, amounts);


        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("[V] SUCCESS: Order created! Order ID: " + res.getReturnValue());
        }
    }

    private void handleRemoveOrder() {
        System.out.println("\n--- Remove Order ---");
        System.out.print("Enter Order ID to remove: ");
        String orderId = scanner.nextLine();

        Response<String> res = orderServices.RemoveOrder(orderId);
        printOrderResponseResult(res, "Order removed successfully!");
    }

    private void handleViewAllOrders() {
        System.out.println("\n--- View All System Orders ---");
        Response<Report> res = orderServices.ViewAllOrders();
        printReportResult(res);
    }

    private void handleViewOrdersBySupplier() {
        System.out.println("\n--- View Orders By Supplier ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();

        Response<Report> res = orderServices.ViewOrdersBySupplier(supId);
        printReportResult(res);
    }

    private void handleViewOrdersByDateRange() {
        System.out.println("\n--- View Orders By Date Range ---");
        try {
            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            LocalDate start = LocalDate.parse(scanner.nextLine());
            System.out.print("Enter End Date (YYYY-MM-DD): ");
            LocalDate end = LocalDate.parse(scanner.nextLine());

            Response<Report> res = orderServices.ViewOrdersByDateRange(start, end);
            printReportResult(res);
        } catch (Exception e) {
            System.out.println("[!] Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    private void handleUpdateOrderStatusMenu() {
        System.out.println("\n--- Update Order Status ---");
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();

        System.out.println("Select New Status:");
        System.out.println("1. Prepare");
        System.out.println("2. Cancel");
        System.out.println("3 .Send");
        System.out.println("4. Deliver");
        System.out.print("Your choice: ");
        String statusChoice = scanner.nextLine();

        Response<String> res;
        switch (statusChoice) {
            case "1":
                res = orderServices.PrepareOrder(orderId);
                printOrderResponseResult(res, "Order status updated to PREPARE!");
                break;
            case "2":
                res = orderServices.CancelOrder(orderId);
                printOrderResponseResult(res, "Order successfully CANCELED!");
                break;
            case "4":
                Response<HashMap<String,Integer>> amounts = orderServices.DeliverOrder(orderId);
                if(amounts.isError())
                {
                    System.out.println("ERROR:" + amounts.getErrorMsg());
                    break;
                }
                System.out.println("Order status updated to Delivered! - restocking:");
                ProductServices.getInstance().ReciveOrder(amounts.getReturnValue());
                break;
            case "3":
                res = orderServices.SendOrder(orderId);
                printOrderResponseResult(res, "Order status updated to SENT!");
                break;
            default:
                System.out.println("[!] Invalid status choice. Operation aborted.");
                break;
        }
    }

    private void printOrderResponseResult(Response<String> res, String successMessage) {
        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("[V] SUCCESS: " + successMessage);
        }
    }

    private void printReportResult(Response<Report> res) {
        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("\n--- Orders Report ---");
            System.out.println(res.getReturnValue().GetReport());
        }
    }


    private void handleAddSupplier() {
        System.out.println("\n--- Add New Supplier ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();
        System.out.print("Enter Company Registration Number: ");
        String regNumber = scanner.nextLine();
        System.out.print("Enter Supplier Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Bank Account Details: ");
        String bank = scanner.nextLine();
        System.out.print("Enter Payment Terms (e.g., Net60, Cash): ");
        String pt = scanner.nextLine();

        HashMap<String, Double> itemsToPrices = new HashMap<>();
        System.out.println("Enter items and prices (You must add at least one item before typing 'done'):");
        while (true) {
            System.out.print("Enter Product Catalog Number (or 'done'): ");
            String itemId = scanner.nextLine();

            if (itemId.equalsIgnoreCase("done")) {
                if (itemsToPrices.isEmpty()) {
                    System.out.println("[!] You must add at least one product before finishing.");
                    continue;
                }
                break;
            }

            System.out.print("Enter Price for " + itemId + ": ");
            try {
                double price = Double.parseDouble(scanner.nextLine());
                if (price <= 0) {
                    System.out.println("[!] Price must be greater than 0. Item not added.");
                    continue;
                }
                itemsToPrices.put(itemId, price);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid price format. Item not added.");
            }
        }

        Response<String> res = supplierServices.AddSupplier(supId, regNumber, name, bank, pt, itemsToPrices);
        printResponseResult(res, "Supplier added successfully!");
    }

    private void handleRemoveSupplier() {
        System.out.println("\n--- Remove Supplier ---");
        System.out.print("Enter Supplier ID to remove: ");
        String supId = scanner.nextLine();

        Response<String> res = supplierServices.RemoveSupplier(supId);
        printResponseResult(res, "Supplier removed successfully!");
        res = orderServices.RemoveAllBOFromSupp(supId);
        printResponseResult(res, "All BuyOrders of " + supId + " were removed successfully!");
    }

    private void handleAddContact() {
        System.out.println("\n--- Add Contact Person ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();
        System.out.print("Enter Contact Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Contact Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Contact Phone: ");
        String phone = scanner.nextLine();

        Response<String> res = supplierServices.AddContact(supId, name, email, phone);
        printResponseResult(res, "Contact person added successfully!");
    }

    private void handleRemoveContact() {
        System.out.println("\n--- Remove Contact Person ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();
        System.out.print("Enter Contact Name to remove: ");
        String name = scanner.nextLine();

        Response<String> res = supplierServices.RemoveContact(supId, name);
        printResponseResult(res, "Contact person removed successfully!");
    }

    private void handleEditContactMenu() {
        System.out.println("\n--- Edit Contact Details ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();
        System.out.print("Enter Contact Name: ");
        String name = scanner.nextLine();

        System.out.println("What would you like to edit?");
        System.out.println("1. Edit Name");
        System.out.println("2. Edit Email");
        System.out.println("3. Edit Phone");
        System.out.print("Your choice: ");
        String choice = scanner.nextLine();

        Response<String> res;
        switch (choice) {
            case "1":
                System.out.print("Enter New Name: ");
                String newName = scanner.nextLine();
                res = supplierServices.EditContactName(supId, name, newName);
                break;
            case "2":
                System.out.print("Enter New Email: ");
                String email = scanner.nextLine();
                res = supplierServices.EditContactEmail(supId, name, email);
                break;
            case "3":
                System.out.print("Enter New Phone: ");
                String phone = scanner.nextLine();
                res = supplierServices.EditContactPhone(supId, name, phone);
                break;
            default:
                System.out.println("[!] Invalid choice.");
                return;
        }
        printResponseResult(res, "Contact details updated successfully!");
    }

    private void handleEditSupplierMenu() {
        System.out.println("\n--- Edit Supplier Details ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();

        System.out.println("What would you like to edit?");
        System.out.println("1. Supplier Name");
        System.out.println("2. Bank Details");
        System.out.println("3. Payment Terms");
        System.out.println("4. Registration Number");
        System.out.print("Your choice: ");
        String choice = scanner.nextLine();

        Response<String> res;
        switch (choice) {
            case "1":
                System.out.print("Enter New Supplier Name: ");
                String name = scanner.nextLine();
                res = supplierServices.EditSupplierName(supId, name);
                break;
            case "2":
                System.out.print("Enter New Bank Account: ");
                String bank = scanner.nextLine();
                res = supplierServices.EditSupplierBank(supId, bank);
                break;
            case "3":
                System.out.print("Enter New Payment Terms: ");
                String pt = scanner.nextLine();
                res = supplierServices.EditSupplierPaymentTerms(supId, pt);
                break;
            case "4":
                System.out.print("Enter New Reg Number: ");
                String reg = scanner.nextLine();
                res = supplierServices.EditSupplierRegNumber(supId, reg);
                break;
            default:
                System.out.println("[!] Invalid choice.");
                return;
        }
        printResponseResult(res, "Supplier details updated successfully!");
    }

    private void handleViewContacts() {
        System.out.println("\n--- View Contacts Report ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();

        Response<Report> res = supplierServices.ViewContacts(supId);
        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("\n--- Contacts Report ---");
            System.out.println(res.getReturnValue().GetReport());
        }
    }

    private void handleAddDeliveryDay() {
        System.out.println("\n--- Add Fixed Delivery Day ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();
        DayOfWeek day = getDayOfWeekInput();

        if (day != null) {
            Response<String> res = supplierServices.AddDelvDay(supId, day);
            printResponseResult(res, "Delivery day added successfully!");
        }
    }

    private void handleRemoveDeliveryDay() {
        System.out.println("\n--- Remove Fixed Delivery Day ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();
        DayOfWeek day = getDayOfWeekInput();

        if (day != null) {
            Response<String> res = supplierServices.RemoveDelvDay(supId, day);
            printResponseResult(res, "Delivery day removed successfully!");
        }
    }

    private DayOfWeek getDayOfWeekInput() {
        System.out.print("Enter day of the week (MONDAY-SUNDAY): ");
        String dayInput = scanner.nextLine().toUpperCase();
        try {
            return DayOfWeek.valueOf(dayInput);
        } catch (IllegalArgumentException e) {
            System.out.println("[!] Invalid day name. Please enter a valid day (e.g., SUNDAY, MONDAY...).");
            return null;
        }
    }

    private void handleAddItemToAgreement() {
        System.out.println("\n--- Add Item in Agreement ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();
        System.out.print("Enter Item Catalog Number: ");
        String itemCatalog = scanner.nextLine();
        System.out.print("Enter Item Price: ");
        try {
            double price = Double.parseDouble(scanner.nextLine());
            Response<String> res = supplierServices.AddItemToAgreement(supId, itemCatalog, price);
            printResponseResult(res, "Item added in supplier agreement!");
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid price format.");
        }
    }

    private void handleRemoveItemFromAgreement() {
        System.out.println("\n--- Remove Item from Agreement ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();
        System.out.print("Enter Item Catalog Number to remove: ");
        String itemCatalog = scanner.nextLine();

        Response<String> res = supplierServices.RemoveItemFromAgreement(supId, itemCatalog);
        printResponseResult(res, "Item removed from supplier agreement successfully!");
    }

    private void handleUpdateItemPriceInAgreement() {
        System.out.println("\n--- Update Item Price in Agreement ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine();
        System.out.print("Enter Item Catalog Number: ");
        String itemCatalog = scanner.nextLine();
        System.out.print("Enter New Item Price: ");
        try {
            double newPrice = Double.parseDouble(scanner.nextLine());
            Response<String> res = supplierServices.UpdateItemPriceInAgreement(supId, itemCatalog, newPrice);
            printResponseResult(res, "Item price updated in supplier agreement!");
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid price format.");
        }
    }

    private void printResponseResult(Response<String> res, String successMessage) {
        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("[V] SUCCESS: " + successMessage);
            if (res.getReturnValue() != null && !res.getReturnValue().isEmpty()) {
                System.out.println("Details: " + res.getReturnValue());
            }
        }
    }



    private void CreateSupplierTestData() {
        System.out.println("\n==================================================");
        System.out.println(">>> [!] Initializing Comprehensive Supplier & Order Test Data...");
        System.out.println("==================================================");

        Response<String> res;
        try {

            String sup1Id = "SUP-001";
            HashMap<String, Double> beveragesCatalog = new HashMap<>();

            beveragesCatalog.put("BEV-001", 2.10);
            beveragesCatalog.put("BEV-002", 4.20);
            beveragesCatalog.put("BEV-003", 5.80);
            beveragesCatalog.put("BEV-004", 3.20);

            System.out.println("[->] Registering Supplier: Beverage Distributors Ltd (SUP-001)...");
            res = this.supplierServices.AddSupplier(
                    sup1Id, "511234567", "Beverage Distributors Ltd",
                    "Bank Leumi (10), Branch 800, Account 123456", "Net30", beveragesCatalog
            );
            if (res.isError()) throw new RuntimeException("Failed to add Supplier 1 base profile: " + res.getErrorMsg());

            this.supplierServices.AddContact(sup1Id, "Dan Drinker", "0501112223", "dan.d@bevdist.co.il");
            this.supplierServices.AddContact(sup1Id, "Maya Soda", "0501112224", "maya.s@bevdist.co.il");
            this.supplierServices.AddDelvDay(sup1Id, DayOfWeek.SUNDAY);
            this.supplierServices.AddDelvDay(sup1Id, DayOfWeek.WEDNESDAY);

            System.out.println("[V] SUP-001 loaded with 4 items, 2 contacts, and 2 delivery days.");


            String sup2Id = "SUP-002";
            HashMap<String, Double> bakeryCatalog = new HashMap<>();

            bakeryCatalog.put("BAK-001", 3.15);
            bakeryCatalog.put("BAK-002", 2.90);
            bakeryCatalog.put("BAK-003", 1.65);
            bakeryCatalog.put("BAK-004", 5.50);

            System.out.println("[->] Registering Supplier: Angel & Sons Bakery (SUP-002)...");
            res = this.supplierServices.AddSupplier(
                    sup2Id, "512345678", "Angel & Sons Bakery",
                    "Bank Hapoalim (12), Branch 612, Account 789101", "Cash", bakeryCatalog
            );
            if (res.isError()) throw new RuntimeException("Failed to add Supplier 2 base profile: " + res.getErrorMsg());

            this.supplierServices.AddContact(sup2Id, "Ronny Rollingpin", "0524445556", "ronny@angel-bakery.co.il");
            this.supplierServices.AddContact(sup2Id, "Beni Baker", "0524445557", "orders@angel-bakery.co.il");
            this.supplierServices.AddDelvDay(sup2Id, DayOfWeek.MONDAY);
            this.supplierServices.AddDelvDay(sup2Id, DayOfWeek.TUESDAY);
            this.supplierServices.AddDelvDay(sup2Id, DayOfWeek.THURSDAY);

            System.out.println("[V] SUP-002 loaded with 4 items, 2 contacts, and 3 delivery days.");



            String sup3Id = "SUP-003";
            HashMap<String, Double> householdCatalog = new HashMap<>();

            householdCatalog.put("HOU-001", 4.80);
            householdCatalog.put("HOU-002", 6.50);
            householdCatalog.put("HOU-003", 11.20);
            householdCatalog.put("HOU-004", 7.50);

            System.out.println("[->] Registering Supplier: Clean & Bright Wholesale (SUP-003)...");
            res = this.supplierServices.AddSupplier(
                    sup3Id, "513456789", "Clean & Bright Wholesale Logistics",
                    "Bank Discount (11), Branch 110, Account 456789", "Net60", householdCatalog
            );
            if (res.isError()) throw new RuntimeException("Failed to add Supplier 3 base profile: " + res.getErrorMsg());

            this.supplierServices.AddContact(sup3Id, "Sara Soap", "0547778889", "sara.s@cleanbright.com");
            this.supplierServices.AddContact(sup3Id, "Gabi Glanz", "039201144", "office@cleanbright.com");
            this.supplierServices.AddDelvDay(sup3Id, DayOfWeek.TUESDAY);

            System.out.println("[V] SUP-003 loaded with 4 items, 2 contacts, and 1 delivery day.");


            String sup4Id = "SUP-004";
            HashMap<String, Double> boutiqueCatalog = new HashMap<>();
            boutiqueCatalog.put("BAK-003", 2.50);

            System.out.println("[->] Registering Supplier: Express Boutique Food (SUP-004)...");
            res = this.supplierServices.AddSupplier(
                    sup4Id, "514567890", "Express Boutique Food",
                    "Bank Yahav (04), Branch 112, Account 998877", "Net15", boutiqueCatalog
            );
            if (res.isError()) throw new RuntimeException("Failed to add Supplier 4 base profile: " + res.getErrorMsg());

            this.supplierServices.AddContact(sup4Id, "Avi Express", "0556667778", "avi@expressboutique.co.il");
            this.supplierServices.AddDelvDay(sup4Id, DayOfWeek.FRIDAY);

            System.out.println("[V] SUP-004 loaded with 1 item, 1 contact, and 1 delivery day.");



            System.out.println("\n[->] Generating Sample Orders across different statuses...");

            HashMap<String, Integer> order1Amounts = new HashMap<>();
            HashMap<String, Double> order1Prices = new HashMap<>();
            order1Amounts.put("BEV-001", 50);
            order1Prices.put("BEV-001", beveragesCatalog.get("BEV-001"));
            order1Amounts.put("BEV-002", 30);
            order1Prices.put("BEV-002", beveragesCatalog.get("BEV-002"));

            res = this.orderServices.CreateOrder(sup1Id, true, order1Amounts);
            if (res.isError()) throw new RuntimeException("Failed to create Order 1: " + res.getErrorMsg());
            System.out.println("[V] Created Order ID: " + res.getReturnValue() + " (Pending - SUP-001)");


            HashMap<String, Integer> order2Amounts = new HashMap<>();
            HashMap<String, Double> order2Prices = new HashMap<>();
            order2Amounts.put("BAK-003", 100);
            order2Prices.put("BAK-003", bakeryCatalog.get("BAK-003"));
            order2Amounts.put("BAK-004", 20);
            order2Prices.put("BAK-004", bakeryCatalog.get("BAK-004"));

            res = this.orderServices.CreateOrder(sup2Id, true, order2Amounts);
            if (res.isError()) throw new RuntimeException("Failed to create Order 2: " + res.getErrorMsg());
            String order2Id = res.getReturnValue();

            Response<String> statusRes = this.orderServices.PrepareOrder(order2Id);
            if (statusRes.isError()) throw new RuntimeException("Failed to update Order 2 to Prepare: " + statusRes.getErrorMsg());
            System.out.println("[V] Created Order ID: " + order2Id + " (Status: PREPARE [Urgent] - SUP-002)");


            HashMap<String, Integer> order3Amounts = new HashMap<>();
            HashMap<String, Double> order3Prices = new HashMap<>();
            order3Amounts.put("BAK-001", 40);
            order3Prices.put("BAK-001", bakeryCatalog.get("BAK-001"));
            order3Amounts.put("BAK-002", 25);
            order3Prices.put("BAK-002", bakeryCatalog.get("BAK-002"));

            res = this.orderServices.CreateOrder(sup2Id, true, order3Amounts);
            if (res.isError()) throw new RuntimeException("Failed to create Order 3: " + res.getErrorMsg());
            String order3Id = res.getReturnValue();

            statusRes = this.orderServices.PrepareOrder(order3Id);
            if (statusRes.isError()) throw new RuntimeException("Failed to update Order 3 to Prepare: " + statusRes.getErrorMsg());
            System.out.println("[V] Created Order ID: " + order3Id + " (Status: PREPARE - SUP-002)");


            HashMap<String, Integer> order4Amounts = new HashMap<>();
            HashMap<String, Double> order4Prices = new HashMap<>();
            order4Amounts.put("HOU-003", 15);
            order4Prices.put("HOU-003", householdCatalog.get("HOU-003"));
            order4Amounts.put("HOU-004", 20);
            order4Prices.put("HOU-004", householdCatalog.get("HOU-004"));

            res = this.orderServices.CreateOrder(sup3Id, true, order4Amounts);
            if (res.isError()) throw new RuntimeException("Failed to create Order 4: " + res.getErrorMsg());
            String order4Id = res.getReturnValue();

            statusRes = this.orderServices.CancelOrder(order4Id);
            if (statusRes.isError()) throw new RuntimeException("Failed to cancel Order 4: " + statusRes.getErrorMsg());
            System.out.println("[V] Created Order ID: " + order4Id + " (Status: CANCELED - SUP-003)");


            HashMap<String, Integer> order5Amounts = new HashMap<>();
            HashMap<String, Double> order5Prices = new HashMap<>();
            order5Amounts.put("BAK-003", 10);
            order5Prices.put("BAK-003", boutiqueCatalog.get("BAK-003"));

            res = this.orderServices.CreateOrder(sup4Id, true, order5Amounts);
            if (res.isError()) throw new RuntimeException("Failed to create Order 5: " + res.getErrorMsg());
            System.out.println("[V] Created Order ID: " + res.getReturnValue() + " (Pending - SUP-004)");

            System.out.println("\n==================================================");
            System.out.println("[V] SUCCESS: All Supplier & Order Test Data Loaded!");
            System.out.println("==================================================");
        }
        catch (Exception e) {
            System.out.println("\n==================================================");
            System.out.println("[!] FATAL SYSTEM ERROR CREATING SUPPLIER/ORDER TEST DATA");
            System.out.println("Context/Message: " + e.getMessage());
            System.out.println("==================================================");
        }
    }

    public void HandleDiscountMenu()
    {
        System.out.println("\nChoose an option:");
        System.out.println("1.  Add New DiscountRule");
        System.out.println("2.  Remove DiscountRule");
        System.out.println("3.  Change DiscountRule condition.");
        System.out.println("4.  Change DiscountRule discount.");

        String choice = scanner.nextLine();
        String supId ="";
        String cat = "";
        String name="";
        String min="";
        String disc="";
        Response<String> res = null;
        switch (choice)
        {
            case "1":
                System.out.println("Enter supplier ID:");
                supId = scanner.nextLine();
                System.out.println("Enter item catalog number:");
                cat= scanner.nextLine();
                System.out.println("Enter rule name:");
                name = scanner.nextLine();
                System.out.println("Enter rule minimal amount:");
                min = scanner.nextLine();
                System.out.println("Enter discount% (0-1 eg 0.5 for 50%):");
                disc = scanner.nextLine();

                res = this.supplierServices.AddDiscountRule(supId,cat,name,Double.parseDouble(disc),Integer.parseInt(min));
                if(res.isError()) {
                    System.out.println("ERROR-" + res.getErrorMsg());
                    break;
                }
                System.out.println("Rule was added successfully!");
                break;
            case "2":
                System.out.println("Enter supplier ID:");
                supId = scanner.nextLine();
                System.out.println("Enter item catalog number:");
                cat = scanner.nextLine();
                System.out.println("Enter rule name:");
                name = scanner.nextLine();

                res = this.supplierServices.RemoveDiscountRule(supId,cat,name);
                if(res.isError()) {
                    System.out.println("ERROR-" + res.getErrorMsg());
                    break;
                }
                System.out.println("Rule was removed successfully!");
                break;
            case "3":
                System.out.println("Enter supplier ID:");
                supId = scanner.nextLine();
                System.out.println("Enter item catalog number:");
                cat = scanner.nextLine();
                System.out.println("Enter rule name:");
                name = scanner.nextLine();
                System.out.println("Enter rule new minimal amount:");
                min = scanner.nextLine();
                res = this.supplierServices.UpdateDiscountRuleMin(supId,cat,name,Integer.parseInt(min));
                if(res.isError()) {
                    System.out.println("ERROR-" + res.getErrorMsg());
                    break;
                }
                System.out.println("Rule was modified successfully, minimal amount was changed!");
                break;
            case "4":
                System.out.println("Enter supplier ID:");
                supId = scanner.nextLine();
                System.out.println("Enter item catalog number:");
                cat = scanner.nextLine();
                System.out.println("Enter rule name:");
                name = scanner.nextLine();
                System.out.println("Enter rule new discount% (0-1):");
                disc = scanner.nextLine();
                res = this.supplierServices.UpdateDiscountRuleDisc(supId,cat,name,Double.parseDouble(disc));
                if(res.isError()) {
                    System.out.println("ERROR-" + res.getErrorMsg());
                    break;
                }
                System.out.println("Rule was modified successfully, discount was changed!");
                break;
            default:
                System.out.println("Invalid choice - choose between 1-4.");

        }
    }

    public void HandleSuppliersReport()
    {
        System.out.println("\n--- View Suppliers Report ---");

        Response<Report> res = supplierServices.ViewAllSuppliers();
        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("\n--- Suppliers Report ---");
            System.out.println(res.getReturnValue().GetReport());
        }
    }
}
