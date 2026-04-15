package PresentationLayer;

import ServiceLayer.ProductServices;
import ServiceLayer.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InventoryCLI {

    private final Scanner scanner;
    private final ProductServices service;

    public InventoryCLI(){
        this.scanner = new Scanner(System.in);
        this.service = ProductServices.getInstance();
    }

    public void start() {
        System.out.println("--- Welcome to ADSS Inventory System ---");
        while (true) {
            displayMenu();

            String choice = scanner.nextLine();
            if (choice.equals("0")) {
                System.out.println("Exiting system");
                break;
            }
            else{
                handleChoice(choice);
            }
        }
    }

    private void displayMenu(){
        System.out.println("\nChoose an option:");
        System.out.println("1. Add Product");
        System.out.println("2. Add faulty Product (Report Damage)");
        System.out.println("3. Update Product");
        System.out.println("4. Add Product/Category Discount");
        System.out.println("5. View Stock Alerts (Products running out)");
        System.out.println("6. Export Inventory Report by Dates");
        System.out.println("7. Add Supplier Discount per Product");

        System.out.println("0. Exit");
        System.out.print("Please enter your choice: ");
    }

    private void handleChoice(String num){
        switch (num) {
            case "1":
                handleAddProduct(scanner, service);
                break;

            case "2":
                handleAddFaultyProduct(scanner, service);
                break;

            case "3":
                handleUpdateProduct(scanner, service);
                break;

            case "4":
                handleDiscountProduct(scanner, service);
                break;

            case "5":
                handleViewLowStock(scanner, service);
                break;

            case "6":
                handleInventoryReport(scanner, service);
                break;

            case "7":
                handleSupplierDiscount(scanner, service);
                break;

            default:
                System.out.println("Invalid input. Please choose a number between 0 and 7.");
                break;
        }
    }

    private void handleAddProduct(Scanner scanner, ProductServices service) {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Adding a New Product");
        System.out.println("-----------------------------------------");

        try {
            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Catalog Number: ");
            String catalogNumber = scanner.nextLine();

            System.out.print("Enter Categories (separated by comma, e.g. Dairy,Milk,Fridge): ");
            String categoriesInput = scanner.nextLine();
            List<String> categories = Arrays.asList(categoriesInput.split("\\s*,\\s*"));

            System.out.print("Enter Storage Location (e.g., A-12): ");
            String location = scanner.nextLine();

            int amountOnShelves = getIntInput("Enter Amount on Shelves: ");
            int amountOnStock = getIntInput("Enter Amount in Stock: ");
            int minAmount = getIntInput("Enter Minimum Amount Alert: ");
            double supplyPrice = getDoubleInput("Enter Supply Price: ");
            double consumerPrice = getDoubleInput("Enter Consumer Price: ");

            System.out.println("\n[*] Sending data to system...");

            Response<String> res = service.addProduct(name, catalogNumber, categories, location,
                    amountOnShelves, amountOnStock, supplyPrice, consumerPrice, minAmount);

            if (res.isError()) {
                System.out.println("\n[!] FAILURE: Could not add product.");
                System.out.println("Reason: " + res.getErrorMsg());
            } else {
                System.out.println("\n[V] SUCCESS: Product '" + name + "' added successfully!");
            }

        }catch (Exception e) {
            System.out.println("\n[!] UNEXPECTED ERROR: " + e.getMessage());
        }

        System.out.println("-----------------------------------------");


    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a whole number.");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a valid price (e.g., 10.5).");
            }
        }
    }

    private void handleAddFaultyProduct(Scanner scanner, ProductServices service) {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Adding a Faulty Product");
        System.out.println("-----------------------------------------");

        try {

            System.out.print("Enter Catalog Number: ");
            String catalogNumber = scanner.nextLine();

            System.out.print("Enter Location Product: ");
            String locationProduct = scanner.nextLine();

            System.out.print("Enter Description: ");
            String description = scanner.nextLine();


            System.out.println("\n[*] Sending data to system...");

            Response<Integer> res = service.ReportFaultyProduct( catalogNumber,locationProduct,description);

            if (res.isError()) {
                System.out.println("\n[!] FAILURE: Could not add product.");
                System.out.println("Reason: " + res.getErrorMsg());
            } else {
                System.out.println("\n[V] SUCCESS: Product '" +  "' added successfully!");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private void handleUpdateProduct(Scanner scanner, ProductServices service) {

        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Update Existing Product");
        System.out.println("-----------------------------------------");

        System.out.print("Enter the Catalog Number of the product to update: ");
        String catalogNumber = scanner.nextLine();


        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Product Name");
        System.out.println("2. Storage Location");
        System.out.println("3. Consumer Prices");
        System.out.println("4. Supply Prices");
        System.out.println("5. Shelves Amounts");
        System.out.println("6. Stock Amounts");
        System.out.println("7. Minimum Amount Alert");
        System.out.println("0. Cancel and return to main menu");

        int updateChoice = getIntInput("\nPlease choose an option (0-5): ");

        switch (updateChoice){
            case 0:
                System.out.println("Returning to main menu...");
                break;

            case 1:
                System.out.print("Enter New Name: ");
                String name = scanner.nextLine();
                service.update(catalogNumber, name, null, null, null, null, null, null);
                break;

            case 2:
                System.out.print("Enter New Storage Location: ");
                String location = scanner.nextLine();
                service.update(catalogNumber, null, location, null, null, null, null, null);
                break;

            case 3:
                System.out.print("Enter New Consumer Price: ");
                double consumerPrice = scanner.nextDouble();
                scanner.nextLine();
                service.update(catalogNumber, null, null, consumerPrice, null, null, null, null);
                break;

            case 4:
                System.out.print("Enter New Supply Price: ");
                double supplyPrice = scanner.nextDouble();
                scanner.nextLine();
                service.update(catalogNumber, null, null, null, supplyPrice, null, null, null);
                break;

            case 5:
                System.out.print("Enter New Shelves Amount: ");
                int shelvesAmount = scanner.nextInt();
                scanner.nextLine();
                service.update(catalogNumber, null, null, null, null, shelvesAmount, null, null);
                break;

            case 6:
                System.out.print("Enter New Stock Amount: ");
                int stockAmount = scanner.nextInt();
                scanner.nextLine();
                service.update(catalogNumber, null, null, null, null, null, stockAmount, null);
                break;

            case 7:
                System.out.print("Enter New Minimum Amount Alert: ");
                int minAlert = scanner.nextInt();
                scanner.nextLine();
                service.update(catalogNumber, null, null, null, null, null, null, minAlert);
                break;

            default:
                System.out.println("Invalid input. Please choose a number between 0 and 7.");
                break;

        }



    }

    private void handleDiscountProduct(Scanner scanner, ProductServices service) {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Add Product/Category Discount");
        System.out.println("-----------------------------------------");

        System.out.print("Enter Category Name): ");
        String target = scanner.nextLine();

        double discountPercentage = getDoubleInput("Enter Discount Percentage (e.g. 15.5): ");
        Response<String> res = service.addDiscount(target, discountPercentage);

        if (res.isError()) {
            System.out.println("[!] FAILURE: " + res.getErrorMsg());
        } else {
            System.out.println("[V] SUCCESS: Discount applied successfully!");
        }
        System.out.println("-----------------------------------------");
    }

    private void handleViewLowStock(Scanner scanner, ProductServices service) {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Viewing Stock Alerts (Running Out)");
        System.out.println("-----------------------------------------");

        Response<String> res = service.getLowStockAlerts();

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println(res.getReturnValue());
        }
        System.out.println("-----------------------------------------");
    }

    private void handleInventoryReport(Scanner scanner, ProductServices service) {

        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Export Inventory Report");
        System.out.println("-----------------------------------------");

        System.out.print("Enter Start Date (DD/MM/YYYY): ");
        String startDate = scanner.nextLine();

        System.out.print("Enter End Date (DD/MM/YYYY): ");
        String endDate = scanner.nextLine();


        System.out.println("[*] Generating report for " + startDate + " to " + endDate + "...");

        Response<String> res = service.CreateFaultyProductReport(startDate, endDate);

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("\n--- Report Content ---");
            System.out.println(res.getReturnValue());
        }
        System.out.println("-----------------------------------------");
    }

    private void handleSupplierDiscount(Scanner scanner, ProductServices service) {

        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Add Supplier Discount per Product");
        System.out.println("-----------------------------------------");

        System.out.print("Enter Product Catalog Number: ");
        String catNum = scanner.nextLine();

        int supplierDiscount = getIntInput("Enter Supplier Discount Percentage: ");

        Response<String> res = service.setSupplierDiscount(catNum, supplierDiscount);

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("[V] SUCCESS: Supplier discount recorded.");
        }
        System.out.println("-----------------------------------------");
    }


}
