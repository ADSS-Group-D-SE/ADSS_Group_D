package PresentationLayer;

import DomainLayer.*;
import DomainLayer.SupplierAgreement.SupplyMethod;
import ServiceLayer.SupplierService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SuppliersUI {

    private Scanner scanner;
    private boolean isRunning;
    private SupplierService service;

    public SuppliersUI(SupplierService service) {
        this(service, new Scanner(System.in));
    }

    public SuppliersUI(SupplierService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
        this.isRunning = false;
    }

    public void start() {
        isRunning = true;
        while (isRunning) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");
            handleMainMenu(choice);
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("     Suppliers Management System");
        System.out.println("========================================");
        System.out.println("1. Add Supplier");
        System.out.println("2. Remove Supplier");
        System.out.println("3. View Supplier Details");
        System.out.println("4. View All Suppliers");
        System.out.println("5. Edit Supplier");
        System.out.println("6. Manage Contact Persons");
        System.out.println("7. Manage Agreement");
        System.out.println("8. Manage Agreement Items");
        System.out.println("9. Manage Quantity Discounts");
        System.out.println("10. Search Suppliers by Item");
        System.out.println("11. Create Order from Agreement");
        System.out.println("12. Create Urgent Order");
        System.out.println("13. View Order Details");
        System.out.println("14. View Order History (by Supplier)");
        System.out.println("15. View All Orders");
        System.out.println("16. Cancel Order");
        System.out.println("17. Mark Order as Delivered");
        System.out.println("18. Freeze/Unfreeze Agreement");
        System.out.println("19. Add Item to Agreement");
        System.out.println("20. Create Shortage Order (Best Supplier)");
        System.out.println("21. Create Periodic Fixed-Day Order");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private void handleMainMenu(int choice) {
        switch (choice) {
            case 1:
                addSupplier();
                break;
            case 2:
                removeSupplier();
                break;
            case 3:
                viewSupplierDetails();
                break;
            case 4:
                viewAllSuppliers();
                break;
            case 5:
                editSupplier();
                break;
            case 6:
                manageContactPersons();
                break;
            case 7:
                manageAgreement();
                break;
            case 8:
                manageItems();
                break;
            case 9:
                manageQuantityDiscounts();
                break;
            case 10:
                searchSuppliersByItem();
                break;
            case 11:
                createOrder(false);
                break;
            case 12:
                createOrder(true);
                break;
            case 13:
                viewOrderDetails();
                break;
            case 14:
                viewOrderHistory();
                break;
            case 15:
                viewAllOrders();
                break;
            case 16:
                cancelOrderUI();
                break;
            case 17:
                markOrderDeliveredUI();
                break;
            case 18:
                freezeUnfreezeAgreement();
                break;
            case 19:
                addItemDirect();
                break;
            case 20:
                createShortageOrderUI();
                break;
            case 21:
                createPeriodicOrderUI();
                break;
            case 0:
                isRunning = false;
                System.out.println("Exiting Suppliers Management System. Goodbye!");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // ══════════════════════════════════════════════════════════
    // 1. Add Supplier
    // ══════════════════════════════════════════════════════════

    private void addSupplier() {
        System.out.println("\n--- Add New Supplier ---");
        String companyId = readString("Company ID: ");
        String name = readString("Supplier Name: ");
        String bankAccount = readString("Bank Account: ");
        String paymentMethod = readString("Payment Method (e.g. Net, Cash, Credit): ");
        int netDays = readInt("Net Days (e.g. 30, 60): ");

        try {
            PaymentTerms terms = new PaymentTerms(paymentMethod, netDays);
            Supplier supplier = service.addSupplier(companyId, name, bankAccount, terms);
            System.out.println("Supplier added successfully! ID: " + supplier.getSupplierId());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // 2. Remove Supplier
    // ══════════════════════════════════════════════════════════

    private void removeSupplier() {
        System.out.println("\n--- Remove Supplier ---");
        int id = readInt("Enter Supplier ID to remove: ");
        if (service.removeSupplier(id)) {
            System.out.println("Supplier removed successfully.");
        } else {
            System.out.println("Supplier with ID " + id + " not found.");
        }
    }

    // ══════════════════════════════════════════════════════════
    // 3. View Supplier Details
    // ══════════════════════════════════════════════════════════

    private void viewSupplierDetails() {
        System.out.println("\n--- View Supplier Details ---");
        int id = readInt("Enter Supplier ID: ");
        Supplier supplier = service.getSupplier(id);
        if (supplier == null) {
            System.out.println("Supplier with ID " + id + " not found.");
            return;
        }
        printSupplierCard(supplier);
    }

    private void printSupplierCard(Supplier supplier) {
        System.out.println();
        System.out.println("--- Supplier Card ---");
        System.out.println("ID:            " + supplier.getSupplierId());
        System.out.println("Name:          " + supplier.getName());
        System.out.println("Company ID:    " + supplier.getCompanyId());
        System.out.println("Bank Account:  " + supplier.getBankAccount());
        System.out.println("Payment Terms: " + supplier.getPaymentTerms());

        // Contact Persons
        List<ContactPerson> contacts = supplier.getContactPersons();
        System.out.println("Contact Persons (" + contacts.size() + "):");
        if (contacts.isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (ContactPerson cp : contacts) {
                System.out.println("  - " + cp.getName()
                        + " | Phone: " + cp.getPhoneNumber()
                        + " | Email: " + cp.getEmail());
            }
        }

        // Agreement
        SupplierAgreement agreement = supplier.getAgreement();
        if (agreement == null) {
            System.out.println("Agreement:     (none)");
        } else {
            System.out.println("Agreement:");
            System.out.println("  Supply Method:     " + agreement.getSupplyMethod());
            if (agreement.getSupplyMethod() == SupplyMethod.FIXED_DAYS) {
                System.out.println("  Fixed Supply Days: " + formatDays(agreement.getFixedSupplyDays()));
            }
            if (agreement.getSupplyMethod() == SupplyMethod.ON_ORDER) {
                System.out.println("  Delivery Days:     " + agreement.getDeliveryDays());
            }

            List<SupplierItem> items = agreement.getItems();
            System.out.println("  Items (" + items.size() + "):");
            if (items.isEmpty()) {
                System.out.println("    (none)");
            } else {
                for (SupplierItem item : items) {
                    System.out.println("    Catalog#: " + item.getCatalogNumber()
                            + " | Internal ID: " + item.getInternalItemId()
                            + " | " + item.getItemDescription()
                            + " | Price: " + item.getPrice()
                            + " | Manufacturer: " + item.getManufacturer());
                    List<QuantityDiscount> discounts = item.getQuantityDiscounts();
                    if (!discounts.isEmpty()) {
                        for (QuantityDiscount qd : discounts) {
                            System.out.println("      -> " + qd.getMinQuantity() + "+ units: " + qd.getDiscountPercent() + "% off");
                        }
                    }
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    // 4. View All Suppliers
    // ══════════════════════════════════════════════════════════

    private void viewAllSuppliers() {
        List<Supplier> all = service.getAllSuppliers();
        if (all.isEmpty()) {
            System.out.println("\nNo suppliers registered.");
            return;
        }
        System.out.println("\n--- All Suppliers (" + all.size() + ") ---");
        for (Supplier s : all) {
            System.out.println("  [" + s.getSupplierId() + "] " + s.getName()
                    + " (Company ID: " + s.getCompanyId() + ")");
        }
    }

    // ══════════════════════════════════════════════════════════
    // 5. Edit Supplier
    // ══════════════════════════════════════════════════════════

    private void editSupplier() {
        System.out.println("\n--- Edit Supplier ---");
        int id = readInt("Enter Supplier ID: ");
        Supplier supplier = service.getSupplier(id);
        if (supplier == null) {
            System.out.println("Supplier not found.");
            return;
        }

        System.out.println("Current Name: " + supplier.getName());
        System.out.println("Current Company ID: " + supplier.getCompanyId());
        System.out.println("Current Bank Account: " + supplier.getBankAccount());
        System.out.println("Current Payment Terms: " + supplier.getPaymentTerms());
        System.out.println();
        System.out.println("What would you like to edit?");
        System.out.println("1. Name");
        System.out.println("2. Company ID");
        System.out.println("3. Bank Account");
        System.out.println("4. Payment Terms");
        System.out.println("0. Cancel");
        int choice = readInt("Enter your choice: ");

        try {
            switch (choice) {
                case 1:
                    String name = readString("New Name: ");
                    supplier.setName(name);
                    System.out.println("Name updated.");
                    break;
                case 2:
                    String companyId = readString("New Company ID: ");
                    supplier.setCompanyId(companyId);
                    System.out.println("Company ID updated.");
                    break;
                case 3:
                    String bank = readString("New Bank Account: ");
                    supplier.setBankAccount(bank);
                    System.out.println("Bank Account updated.");
                    break;
                case 4:
                    String method = readString("New Payment Method (e.g. Net, Cash): ");
                    int days = readInt("New Net Days: ");
                    supplier.setPaymentTerms(new PaymentTerms(method, days));
                    System.out.println("Payment Terms updated.");
                    break;
                case 0:
                    System.out.println("Edit cancelled.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // 6. Manage Contact Persons
    // ══════════════════════════════════════════════════════════

    private void manageContactPersons() {
        System.out.println("\n--- Manage Contact Persons ---");
        int id = readInt("Enter Supplier ID: ");
        if (service.getSupplier(id) == null) {
            System.out.println("Supplier not found.");
            return;
        }

        System.out.println("1. Add Contact Person");
        System.out.println("2. Remove Contact Person");
        System.out.println("3. View Contact Persons");
        System.out.println("0. Cancel");
        int choice = readInt("Enter your choice: ");

        try {
            switch (choice) {
                case 1:
                    String name = readString("Contact Name: ");
                    String phone = readString("Phone Number: ");
                    String email = readString("Email: ");
                    service.addContactPerson(id, name, phone, email);
                    System.out.println("Contact person added successfully.");
                    break;
                case 2:
                    String removeName = readString("Enter contact name to remove: ");
                    if (service.removeContactPerson(id, removeName)) {
                        System.out.println("Contact person removed.");
                    } else {
                        System.out.println("Contact person not found.");
                    }
                    break;
                case 3:
                    List<ContactPerson> contacts = service.getContactPersons(id);
                    if (contacts.isEmpty()) {
                        System.out.println("No contact persons.");
                    } else {
                        System.out.println("Contact Persons:");
                        for (ContactPerson cp : contacts) {
                            System.out.println("  - " + cp.getName()
                                    + " | Phone: " + cp.getPhoneNumber()
                                    + " | Email: " + cp.getEmail());
                        }
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // 7. Manage Agreement
    // ══════════════════════════════════════════════════════════

    private void manageAgreement() {
        System.out.println("\n--- Manage Agreement ---");
        int id = readInt("Enter Supplier ID: ");
        if (service.getSupplier(id) == null) {
            System.out.println("Supplier not found.");
            return;
        }

        SupplierAgreement agreement = service.getAgreement(id);

        if (agreement == null) {
            System.out.println("No agreement exists for this supplier.");
            System.out.println("Would you like to create one? (y/n)");
            String answer = readString("").toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                createAgreement(id);
            }
        } else {
            System.out.println("Current agreement:");
            System.out.println("  Supply Method: " + agreement.getSupplyMethod());
            if (agreement.getSupplyMethod() == SupplyMethod.FIXED_DAYS) {
                System.out.println("  Fixed Days: " + formatDays(agreement.getFixedSupplyDays()));
            }
            if (agreement.getSupplyMethod() == SupplyMethod.ON_ORDER) {
                System.out.println("  Delivery Days: " + agreement.getDeliveryDays());
            }
            System.out.println();
            System.out.println("1. Update Supply Method");
            System.out.println("2. Set Delivery Days");
            System.out.println("3. Add Fixed Supply Day");
            System.out.println("4. Remove Fixed Supply Day");
            System.out.println("5. Replace Agreement (create new)");
            System.out.println("0. Cancel");
            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        SupplyMethod method = readSupplyMethod();
                        service.updateSupplyMethod(id, method);
                        System.out.println("Supply method updated.");
                        break;
                    case 2:
                        int days = readInt("Enter delivery days: ");
                        service.setDeliveryDays(id, days);
                        System.out.println("Delivery days updated.");
                        break;
                    case 3:
                        int addDay = readInt("Enter day to add (1=Sun, 2=Mon, ..., 7=Sat): ");
                        service.addFixedSupplyDay(id, addDay);
                        System.out.println("Fixed supply day added.");
                        break;
                    case 4:
                        int removeDay = readInt("Enter day to remove (1-7): ");
                        service.removeFixedSupplyDay(id, removeDay);
                        System.out.println("Fixed supply day removed.");
                        break;
                    case 5:
                        createAgreement(id);
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void createAgreement(int supplierId) {
        SupplyMethod method = readSupplyMethod();
        List<Integer> fixedDays = new ArrayList<>();
        int deliveryDays = 0;

        if (method == SupplyMethod.FIXED_DAYS) {
            System.out.println("Enter fixed supply days (1=Sun, 2=Mon, ..., 7=Sat). Enter 0 to finish:");
            while (true) {
                int day = readInt("Day: ");
                if (day == 0) break;
                if (day >= 1 && day <= 7) {
                    fixedDays.add(day);
                } else {
                    System.out.println("Invalid day. Must be 1-7.");
                }
            }
        } else if (method == SupplyMethod.ON_ORDER) {
            deliveryDays = readInt("Enter estimated delivery days: ");
        }

        try {
            service.createAgreement(supplierId, method, fixedDays, deliveryDays);
            System.out.println("Agreement created successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private SupplyMethod readSupplyMethod() {
        System.out.println("Select Supply Method:");
        System.out.println("1. Fixed Days");
        System.out.println("2. On Order");
        System.out.println("3. Self Pickup (Super-Lee collects)");
        int choice = readInt("Enter your choice: ");
        switch (choice) {
            case 1: return SupplyMethod.FIXED_DAYS;
            case 2: return SupplyMethod.ON_ORDER;
            case 3: return SupplyMethod.SELF_PICKUP;
            default:
                System.out.println("Invalid choice, defaulting to On Order.");
                return SupplyMethod.ON_ORDER;
        }
    }

    // ══════════════════════════════════════════════════════════
    // 8. Manage Agreement Items
    // ══════════════════════════════════════════════════════════

    private void manageItems() {
        System.out.println("\n--- Manage Agreement Items ---");
        int id = readInt("Enter Supplier ID: ");
        if (service.getSupplier(id) == null) {
            System.out.println("Supplier not found.");
            return;
        }
        if (service.getAgreement(id) == null) {
            System.out.println("Supplier has no agreement. Please create one first.");
            return;
        }

        System.out.println("1. Add Item");
        System.out.println("2. Remove Item");
        System.out.println("3. View All Items");
        System.out.println("0. Cancel");
        int choice = readInt("Enter your choice: ");

        try {
            switch (choice) {
                case 1:
                    int catalogNum = readInt("Supplier Catalog Number: ");
                    int internalId = readInt("Internal Item ID: ");
                    String desc = readString("Item Description: ");
                    double price = readDouble("Price per unit: ");
                    String manufacturer = readString("Manufacturer: ");
                    service.addItemToAgreement(id, catalogNum, internalId, desc, price, manufacturer);
                    System.out.println("Item added to agreement.");
                    break;
                case 2:
                    int removeCatalog = readInt("Enter catalog number of item to remove: ");
                    if (service.removeItemFromAgreement(id, removeCatalog)) {
                        System.out.println("Item removed.");
                    } else {
                        System.out.println("Item with that catalog number not found.");
                    }
                    break;
                case 3:
                    List<SupplierItem> items = service.getAgreementItems(id);
                    if (items.isEmpty()) {
                        System.out.println("No items in this agreement.");
                    } else {
                        System.out.println("Items in agreement:");
                        for (SupplierItem item : items) {
                            System.out.println("  Catalog#: " + item.getCatalogNumber()
                                    + " | Internal ID: " + item.getInternalItemId()
                                    + " | " + item.getItemDescription()
                                    + " | Price: " + item.getPrice()
                                    + " | Manufacturer: " + item.getManufacturer());
                            List<QuantityDiscount> discounts = item.getQuantityDiscounts();
                            if (!discounts.isEmpty()) {
                                for (QuantityDiscount qd : discounts) {
                                    System.out.println("    -> " + qd.getMinQuantity() + "+ units: " + qd.getDiscountPercent() + "% off");
                                }
                            }
                        }
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // 9. Manage Quantity Discounts
    // ══════════════════════════════════════════════════════════

    private void manageQuantityDiscounts() {
        System.out.println("\n--- Manage Quantity Discounts ---");
        int id = readInt("Enter Supplier ID: ");
        if (service.getSupplier(id) == null) {
            System.out.println("Supplier not found.");
            return;
        }
        if (service.getAgreement(id) == null) {
            System.out.println("Supplier has no agreement.");
            return;
        }

        int catalogNum = readInt("Enter catalog number of the item: ");
        int minQty = readInt("Minimum quantity for discount: ");
        double discountPercent = readDouble("Discount percentage: ");

        try {
            service.addQuantityDiscount(id, catalogNum, minQty, discountPercent);
            System.out.println("Quantity discount added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // 10. Search Suppliers by Item
    // ══════════════════════════════════════════════════════════

    private void searchSuppliersByItem() {
        System.out.println("\n--- Search Suppliers by Item ---");
        int internalItemId = readInt("Enter Internal Item ID: ");
        List<Supplier> suppliers = service.findSuppliersByItem(internalItemId);
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers found for item ID " + internalItemId + ".");
        } else {
            System.out.println("Suppliers for item ID " + internalItemId + ":");
            for (Supplier s : suppliers) {
                SupplierItem item = s.getAgreement().findItemByInternalId(internalItemId);
                System.out.println("  [" + s.getSupplierId() + "] " + s.getName()
                        + " | Catalog#: " + item.getCatalogNumber()
                        + " | Price: " + item.getPrice());
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    // 11/12. Create Order from Agreement (Flow 3 & Flow 5)
    // ══════════════════════════════════════════════════════════

    private void createOrder(boolean isUrgent) {
        System.out.println("\n--- Create " + (isUrgent ? "Urgent " : "") + "Order ---");
        int supplierId = readInt("Enter Supplier ID: ");
        Supplier supplier = service.getSupplier(supplierId);
        if (supplier == null) {
            System.out.println("Supplier not found.");
            return;
        }
        if (supplier.getAgreement() == null) {
            System.out.println("Supplier has no agreement. Cannot create order.");
            return;
        }

        SupplierAgreement agreement = supplier.getAgreement();
        List<SupplierItem> agrItems = agreement.getItems();
        if (agrItems.isEmpty()) {
            System.out.println("No items in supplier's agreement.");
            return;
        }

        try {
            SupplierOrder order = service.createOrder(supplierId, isUrgent);
            System.out.println("Order #" + order.getOrderId() + " created (" + (isUrgent ? "URGENT" : "Regular") + ")");

            boolean addingItems = true;
            while (addingItems) {
                System.out.println("\nAvailable items in agreement:");
                for (SupplierItem item : agrItems) {
                    System.out.println("  Catalog#: " + item.getCatalogNumber()
                            + " | " + item.getItemDescription()
                            + " | Price: " + item.getPrice()
                            + " | Manufacturer: " + item.getManufacturer());
                }
                System.out.println();
                int catalogNum = readInt("Enter catalog number to add (0 to finish): ");
                if (catalogNum == 0) {
                    addingItems = false;
                } else {
                    int quantity = readInt("Enter quantity: ");
                    try {
                        service.addItemToOrder(order.getOrderId(), catalogNum, quantity);
                        System.out.println("Item added to order.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            }

            if (order.getItems().isEmpty()) {
                System.out.println("Order has no items. Cancelling.");
                service.cancelOrder(order.getOrderId());
                return;
            }

            System.out.println("\n--- Order Summary ---");
            printOrderDetails(order);

            String answer = readString("\nFinalize and send this order? (y/n): ").toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                SupplierOrder finalized = service.finalizeOrder(order.getOrderId());
                System.out.println("Order #" + finalized.getOrderId() + " sent successfully!");
                System.out.println("Expected delivery: " + finalized.getExpectedDeliveryDate());
            } else {
                System.out.println("Order kept as pending.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // 13. View Order Details
    // ══════════════════════════════════════════════════════════

    private void createShortageOrderUI() {
        System.out.println("\n--- Create Shortage Order (Best Supplier) ---");
        int internalItemId = readInt("Internal Item ID from inventory shortage: ");
        int quantity = readInt("Required quantity: ");
        String urgentAnswer = readString("Is this urgent? (y/n): ").toLowerCase();
        boolean isUrgent = urgentAnswer.equals("y") || urgentAnswer.equals("yes");

        try {
            Supplier bestSupplier = service.findBestSupplierForItem(internalItemId, quantity);
            if (bestSupplier == null) {
                System.out.println("No supplier found for this item.");
                return;
            }

            SupplierItem item = bestSupplier.getAgreement().findItemByInternalId(internalItemId);
            System.out.println("Best supplier: [" + bestSupplier.getSupplierId() + "] " + bestSupplier.getName());
            System.out.println("Catalog#: " + item.getCatalogNumber()
                    + " | Unit after discount: " + String.format("%.2f", item.getEffectivePrice(quantity))
                    + " | Total: " + String.format("%.2f", item.getEffectivePrice(quantity) * quantity));

            SupplierOrder order = service.createShortageOrder(internalItemId, quantity, isUrgent);
            System.out.println("Shortage order created and sent.");
            printOrderDetails(order);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void createPeriodicOrderUI() {
        System.out.println("\n--- Create Periodic Fixed-Day Order ---");
        int supplierId = readInt("Enter fixed-days Supplier ID: ");
        Supplier supplier = service.getSupplier(supplierId);
        if (supplier == null) {
            System.out.println("Supplier not found.");
            return;
        }
        if (supplier.getAgreement() == null) {
            System.out.println("Supplier has no agreement. Cannot create periodic order.");
            return;
        }

        Map<Integer, Integer> internalItemQuantities = new LinkedHashMap<>();
        while (true) {
            int internalItemId = readInt("Internal Item ID to order (0 to finish): ");
            if (internalItemId == 0) {
                break;
            }
            int quantity = readInt("Quantity: ");
            internalItemQuantities.put(internalItemId, quantity);
        }

        try {
            SupplierOrder order = service.createPeriodicOrderForSupplier(supplierId, internalItemQuantities);
            System.out.println("Periodic order created and sent.");
            printOrderDetails(order);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewOrderDetails() {
        System.out.println("\n--- View Order Details ---");
        int orderId = readInt("Enter Order ID: ");
        SupplierOrder order = service.getOrder(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        printOrderDetails(order);
    }

    private void printOrderDetails(SupplierOrder order) {
        Supplier supplier = service.getSupplier(order.getSupplierId());
        String supplierName = supplier != null ? supplier.getName() : "Unknown";
        System.out.println("Order ID:          " + order.getOrderId());
        System.out.println("Supplier:          [" + order.getSupplierId() + "] " + supplierName);
        System.out.println("Order Date:        " + order.getOrderDate());
        System.out.println("Expected Delivery: " + (order.getExpectedDeliveryDate() != null ? order.getExpectedDeliveryDate() : "(not set)"));
        System.out.println("Status:            " + order.getStatus());
        System.out.println("Urgent:            " + (order.isUrgent() ? "YES" : "No"));
        System.out.println("Items:");
        List<OrderItem> orderItems = order.getItems();
        if (orderItems.isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (OrderItem oi : orderItems) {
                System.out.println("  Catalog#: " + oi.getCatalogNumber()
                        + " | " + oi.getItemDescription()
                        + " | Qty: " + oi.getQuantity()
                        + " | Unit Price: " + String.format("%.2f", oi.getUnitPrice())
                        + " | Discount: " + oi.getDiscountPercent() + "%"
                        + " | Line Total: " + String.format("%.2f", oi.getTotalPrice()));
            }
        }
        System.out.println("Total Price:       " + String.format("%.2f", order.getTotalPrice()));
    }

    // ══════════════════════════════════════════════════════════
    // 14. View Order History (by Supplier)
    // ══════════════════════════════════════════════════════════

    private void viewOrderHistory() {
        System.out.println("\n--- Order History by Supplier ---");
        int supplierId = readInt("Enter Supplier ID: ");
        Supplier supplier = service.getSupplier(supplierId);
        if (supplier == null) {
            System.out.println("Supplier not found.");
            return;
        }
        List<SupplierOrder> orders = service.getOrdersBySupplier(supplierId);
        if (orders.isEmpty()) {
            System.out.println("No orders found for supplier " + supplier.getName() + ".");
        } else {
            System.out.println("Orders for " + supplier.getName() + " (" + orders.size() + "):");
            for (SupplierOrder order : orders) {
                System.out.println("  [Order #" + order.getOrderId() + "] "
                        + order.getOrderDate()
                        + " | Status: " + order.getStatus()
                        + " | Urgent: " + (order.isUrgent() ? "YES" : "No")
                        + " | Total: " + String.format("%.2f", order.getTotalPrice()));
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    // 15. View All Orders
    // ══════════════════════════════════════════════════════════

    private void viewAllOrders() {
        List<SupplierOrder> orders = service.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("\nNo orders in the system.");
            return;
        }
        System.out.println("\n--- All Orders (" + orders.size() + ") ---");
        for (SupplierOrder order : orders) {
            Supplier supplier = service.getSupplier(order.getSupplierId());
            String supplierName = supplier != null ? supplier.getName() : "Unknown";
            System.out.println("  [Order #" + order.getOrderId() + "] "
                    + supplierName
                    + " | " + order.getOrderDate()
                    + " | Status: " + order.getStatus()
                    + " | Urgent: " + (order.isUrgent() ? "YES" : "No")
                    + " | Total: " + String.format("%.2f", order.getTotalPrice()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // 16. Cancel Order
    // ══════════════════════════════════════════════════════════

    private void cancelOrderUI() {
        System.out.println("\n--- Cancel Order ---");
        int orderId = readInt("Enter Order ID to cancel: ");
        try {
            service.cancelOrder(orderId);
            System.out.println("Order #" + orderId + " has been cancelled.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // 17. Mark Order as Delivered
    // ══════════════════════════════════════════════════════════

    private void markOrderDeliveredUI() {
        System.out.println("\n--- Mark Order as Delivered ---");
        int orderId = readInt("Enter Order ID: ");
        try {
            service.markOrderDelivered(orderId);
            System.out.println("Order #" + orderId + " marked as delivered.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // 18. Freeze/Unfreeze Agreement
    // ══════════════════════════════════════════════════════════

    private void freezeUnfreezeAgreement() {
        System.out.println("\n--- Freeze/Unfreeze Agreement ---");
        int supplierId = readInt("Enter Supplier ID: ");
        if (service.getSupplier(supplierId) == null) {
            System.out.println("Supplier not found.");
            return;
        }
        try {
            boolean isFrozen = service.isAgreementFrozen(supplierId);
            System.out.println("Agreement is currently: " + (isFrozen ? "FROZEN" : "ACTIVE"));
            System.out.println("1. Freeze Agreement");
            System.out.println("2. Unfreeze Agreement");
            System.out.println("0. Cancel");
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    service.freezeAgreement(supplierId);
                    System.out.println("Agreement frozen.");
                    break;
                case 2:
                    service.unfreezeAgreement(supplierId);
                    System.out.println("Agreement unfrozen.");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // 19. Add Item to Agreement (direct access)
    // ══════════════════════════════════════════════════════════

    private void addItemDirect() {
        System.out.println("\n--- Add Item to Agreement ---");
        int id = readInt("Enter Supplier ID: ");
        if (service.getSupplier(id) == null) {
            System.out.println("Supplier not found.");
            return;
        }
        if (service.getAgreement(id) == null) {
            System.out.println("Supplier has no agreement. Please create one first (option 7).");
            return;
        }
        try {
            int catalogNum = readInt("Supplier Catalog Number: ");
            int internalId = readInt("Internal Item ID: ");
            String desc = readString("Item Description: ");
            double price = readDouble("Price per unit: ");
            String manufacturer = readString("Manufacturer: ");
            service.addItemToAgreement(id, catalogNum, internalId, desc, price, manufacturer);
            System.out.println("Item added to agreement successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // Helper: Format days of the week
    // ══════════════════════════════════════════════════════════

    private String formatDays(List<Integer> days) {
        String[] dayNames = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            int d = days.get(i);
            if (d >= 1 && d <= 7) {
                sb.append(dayNames[d]);
            } else {
                sb.append(d);
            }
            if (i < days.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════
    // Input Helpers
    // ══════════════════════════════════════════════════════════

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }
}
