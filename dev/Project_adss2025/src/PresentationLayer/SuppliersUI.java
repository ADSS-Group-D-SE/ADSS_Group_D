package PresentationLayer;

import DomainLayer.*;
import DomainLayer.SupplierAgreement.SupplyMethod;
import ServiceLayer.SupplierService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SuppliersUI {

    private Scanner scanner;
    private boolean isRunning;
    private SupplierService service;

    public SuppliersUI(SupplierService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
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
        String paymentTerms = readString("Payment Terms: ");

        try {
            Supplier supplier = service.addSupplier(companyId, name, bankAccount, paymentTerms);
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
                    String terms = readString("New Payment Terms: ");
                    supplier.setPaymentTerms(terms);
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
