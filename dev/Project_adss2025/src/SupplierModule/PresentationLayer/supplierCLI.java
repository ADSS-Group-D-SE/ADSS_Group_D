package SupplierModule.PresentationLayer;

import CrossCuttingPackage.Report;
import CrossCuttingPackage.Response;
import SupplierModule.ServiceLayer.SupplierServices;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Scanner;

public class supplierCLI {
    private final Scanner scanner;
    private final SupplierServices supplierServices;

    public supplierCLI(){
        this.scanner = new Scanner(System.in);
        this.supplierServices = SupplierServices.getInstance();
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
            default:
                System.out.println("Invalid input. Please choose a number between 0 and 9.");
                break;
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
        System.out.println("4. Registration Number (ח\"פ)");
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
}
