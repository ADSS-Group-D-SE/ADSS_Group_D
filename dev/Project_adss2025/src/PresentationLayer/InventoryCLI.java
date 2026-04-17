package PresentationLayer;

import ServiceLayer.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InventoryCLI {

    private final Scanner scanner;
    private final ProductServices productServices;
    private final CategoryServices categoryServices;

    public InventoryCLI(){
        this.scanner = new Scanner(System.in);
        this.productServices = ProductServices.getInstance();
        this.categoryServices=CategoryServices.GetInstance();
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
        System.out.println("2  Add Category/ SubCategory/ SubsubCategory.");
        System.out.println("3. Add faulty Product (Report Damage)");
        System.out.println("4. Update Product");
        System.out.println("5. Add Product/Category Discount");
        System.out.println("6. View Stock Alerts (Products running out)");
        System.out.println("7. Export Faulty Inventory Report by Dates");
        System.out.println("8. Add Supplier Discount per Product");
        System.out.println("9  Get inventory report by categories.");
        System.out.println("10. show all products");


        System.out.println("0. Exit");
        System.out.print("Please enter your choice: ");
    }

    private void handleChoice(String num){
        switch (num) {
            case "1":
                handleAddProduct();
                break;

            case "2":
                handleCategoryCreation();
                break;

            case "3":
                handleAddFaultyProduct();
                break;

            case "4":
                handleUpdateProduct();
                break;

            case "5":
                handleDiscountProduct();
                break;

            case "6":
                handleViewLowStock();
                break;

            case "7":
                handleInventoryReport();
                break;

            case "8":
                handleSupplierDiscount();
                break;

            case "9":
                this.HandleInventoryReport();
                break;

            case "10":
                displayAllProducts();
                break;


            default:
                System.out.println("Invalid input. Please choose a number between 0 and 7.");
                break;
        }
    }

    public void displayAllProducts() {
        Response<List<ProductSL>> res = this.productServices.getAllProducts();

        int tableWidth = 170;
        System.out.println("\n" + "=".repeat(tableWidth));
        System.out.printf("| %-166s |%n", "                                                           FULL INVENTORY DETAILED REPORT");
        System.out.println("=".repeat(tableWidth));

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            List<ProductSL> products = res.getReturnValue();

            if (products == null || products.isEmpty()) {
                System.out.printf("| %-166s |%n", "No products found in the system.");
            } else {
                System.out.printf("| %-10s | %-18s | %-8s | %-15s | %-7s | %-7s | %-9s | %-9s | %-8s | %-8s | %-12s | %-15s | %-18s |%n",
                        "Catalog #", "Name", "Loc.", "Manufacturer", "Shelf", "Stock", "C.Price", "S.Price", "P.Disc", "S.Disc", "Main Cat", "Sub Cat", "SubSub");
                System.out.println("-".repeat(tableWidth));

                for (ProductSL p : products) {
                    System.out.printf("| %-10s | %-18s | %-8s | %-15s | %-7d | %-7d | %-9.2f | %-9.2f | %-7.1f%% | %-7.1f%% | %-12s | %-15s | %-18s |%n",
                            p.catalog_number,
                            truncate(p.name, 18),
                            p.location,
                            truncate(p.manufacturer, 15),
                            p.amount_on_shelves,
                            p.amount_on_stock,
                            p.price_to_consumer,
                            p.price_to_supply,
                            p.product_discount,
                            p.supplier_discount,
                            truncate(p.main_category_id, 12),
                            truncate(p.sub_category_id, 15),
                            truncate(p.subsub_category_id, 18)
                    );
                }
            }
        }
        System.out.println("=".repeat(tableWidth) + "\n");
    }

    private String truncate(String str, int size) {
        if (str == null) return "N/A";
        if (str.length() <= size) return str;
        return str.substring(0, size - 3) + "...";
    }



    private CategorySL HandleMainCategoryChoice()
    {
        Response<List<CategorySL>> res = this.categoryServices.GetMainCategories();
        if(res.isError()){
            System.out.println("[!] Error fetching categories: " + res.getErrorMsg());
            return null;
        }

        List<CategorySL> categories = res.getReturnValue();
        if (categories == null || categories.isEmpty()) {
            System.out.println("[!] No categories available in the system.");
            System.out.println("[*] Please go to Option 2 in the main menu to add a category first.");
            return null;
        }

        int choice = 0;
        do {
            System.out.println("Select a main category:");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println(i + 1 + "." + categories.get(i).name + "\n");
            }
            choice = scanner.nextInt();
            scanner.nextLine();
            if(choice < 1 || choice > categories.size())
                System.out.println("Choice is not in allowed range. try again:");
        }while(choice < 1 || choice > categories.size());
        return categories.get(choice-1);
    }

    private CategorySL HandleSubCategoryChoice(CategorySL root)
    {
        Response<List<CategorySL>> res = this.categoryServices.GetSubCategories(root.Id);
        if(res.isError()){
            System.out.println("[!] Error fetching categories: " + res.getErrorMsg());
            return null;
        }
        List<CategorySL> categories = res.getReturnValue();
        if (categories == null || categories.isEmpty()) {
            System.out.println("[!] No sub categories available in the system.");
            System.out.println("[*] Please use Option 2 -> 'Create a sub category' and link it to '" + root.name + "'.");
            return null;
        }

        int choice = 0;
        do {
            System.out.println("Select a sub category to the category " +root.name +":");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println(i + 1 + "." + categories.get(i).name + "\n");
            }
            choice = scanner.nextInt();
            scanner.nextLine();
            if(choice < 1 || choice > categories.size())
                System.out.println("Choice is not in allowed range. try again:");
        }while(choice < 1 || choice > categories.size());
        return categories.get(choice-1);
    }

    private void handleAddProduct() {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Adding a New Product");
        System.out.println("-----------------------------------------");

        try {
            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Catalog Number: ");
            String catalogNumber = scanner.nextLine();

            CategorySL main = this.HandleMainCategoryChoice();
            if (main == null) return;
            CategorySL sub = this.HandleSubCategoryChoice(main);
            if (sub == null) return;
            CategorySL subsub = this.HandleSubCategoryChoice(sub);
            if (subsub == null) return;


            System.out.print("Enter Storage Location (e.g., A-12): ");
            String location = scanner.nextLine();
            System.out.print("Enter Product manufacturer: ");
            String manu = scanner.nextLine();

            int amountOnShelves = getIntInput("Enter Amount on Shelves: ");
            int amountOnStock = getIntInput("Enter Amount in Stock: ");
            int minAmount = getIntInput("Enter Minimum Amount Alert: ");
            double supplyPrice = getDoubleInput("Enter Supply Price: ");
            double consumerPrice = getDoubleInput("Enter Consumer Price: ");

            System.out.println("\n[*] Sending data to system...");

            Response<String> res = this.productServices.addProduct(name, catalogNumber, main.Id,sub.Id,subsub.Id, location,manu,
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

    private void handleAddFaultyProduct() {
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

            Response<Integer> res = this.productServices.ReportFaultyProduct( catalogNumber,locationProduct,description);

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



    private void handleUpdateProduct() {

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

        int updateChoice = getIntInput("\nPlease choose an option (0-7): ");

        switch (updateChoice){
            case 0:
                System.out.println("Returning to main menu...");
                break;

            case 1:
                System.out.print("Enter New Name: ");
                String name = scanner.nextLine();
                this.productServices.update(catalogNumber, name, null, null, null, null, null, null);
                break;

            case 2:
                System.out.print("Enter New Storage Location: ");
                String location = scanner.nextLine();
                this.productServices.update(catalogNumber, null, location, null, null, null, null, null);
                break;

            case 3:
                System.out.print("Enter New Consumer Price: ");
                double consumerPrice = scanner.nextDouble();
                scanner.nextLine();
                this.productServices.update(catalogNumber, null, null, consumerPrice, null, null, null, null);
                break;

            case 4:
                System.out.print("Enter New Supply Price: ");
                double supplyPrice = scanner.nextDouble();
                scanner.nextLine();
                this.productServices.update(catalogNumber, null, null, null, supplyPrice, null, null, null);
                break;

            case 5:
                System.out.print("Enter New Shelves Amount: ");
                int shelvesAmount = scanner.nextInt();
                scanner.nextLine();
                this.productServices.update(catalogNumber, null, null, null, null, shelvesAmount, null, null);
                break;

            case 6:
                System.out.print("Enter New Stock Amount: ");
                int stockAmount = scanner.nextInt();
                scanner.nextLine();
                this.productServices.update(catalogNumber, null, null, null, null, null, stockAmount, null);
                break;

            case 7:
                System.out.print("Enter New Minimum Amount Alert: ");
                int minAlert = scanner.nextInt();
                scanner.nextLine();
                this.productServices.update(catalogNumber, null, null, null, null, null, null, minAlert);
                break;


            default:
                System.out.println("Invalid input. Please choose a number between 0 and 7.");
                break;

        }
    }

    private void handleDiscountProduct() {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Add Product/Category Discount");
        System.out.println("-----------------------------------------");

        CategorySL c = this.HandleMainCategoryChoice();
        double discountPercentage=-1;

        do {
            discountPercentage = getDoubleInput("Enter Discount Percentage (from 0 to 1 eg 0.5 for 50%):");
        }while(discountPercentage <0 || discountPercentage >1);

        Response<String> res = this.productServices.SetCategoryDiscount(c.Id, discountPercentage);

        if (res.isError()) {
            System.out.println("[!] FAILURE: " + res.getErrorMsg());
        } else {
            System.out.println("[V] SUCCESS: Discount applied successfully!");
        }
        System.out.println("-----------------------------------------");
    }

//    private void handleViewLowStock() {
//        System.out.println("\n-----------------------------------------");
//        System.out.println(">>> Action: Viewing Stock Alerts (Running Out)");
//        System.out.println("-----------------------------------------");
//
//        Response<String> res = this.productServices.getLowStockAlerts();
//
//        if (res.isError()) {
//            System.out.println("[!] ERROR: " + res.getErrorMsg());
//        } else {
//            System.out.println(res.getReturnValue());
//        }
//        System.out.println("-----------------------------------------");
//    }


    private void handleViewLowStock() {
        // עיצוב כותרת בולטת
        int tableWidth = 90;
        System.out.println("\n" + "=".repeat(tableWidth));
        System.out.printf("| %-86s |%n", "                STOCK ALERT: PRODUCTS BELOW MINIMUM AMOUNT");
        System.out.println("=".repeat(tableWidth));

        Response<List<ProductSL>> res = this.productServices.getLowStockAlerts();

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            List<ProductSL> lowStock = res.getReturnValue();

            if (lowStock == null || lowStock.isEmpty()) {
                System.out.printf("| %-86s |%n", "All products are well-stocked. No alerts at this time.");
            } else {
                // כותרות הטבלה
                System.out.printf("| %-7s | %-12s | %-20s | %-10s | %-10s | %-10s |%n",
                        "STATUS", "Catalog #", "Product Name", "Current", "Min. Alert", "Location");
                System.out.println("-".repeat(tableWidth));

                for (ProductSL p : lowStock) {
                    // חישוב סך הכל מלאי קיים (מדפים + מחסן)
                    int totalCurrent = p.amount_on_shelves + p.amount_on_stock;

                    // הדפסת שורה טבלאית
                    System.out.printf("| [!!]   | %-12s | %-20s | %-10d | %-10d | %-10s |%n",
                            p.catalog_number,
                            truncate(p.name, 20),
                            totalCurrent,
                            p.minAmountAlert,
                            p.location);
                }
                System.out.println("-".repeat(tableWidth));
                System.out.println("[*] Summary: Found " + lowStock.size() + " products that require restocking.");
            }
        }
        System.out.println("=".repeat(tableWidth) + "\n");
    }

    private void handleInventoryReport() {

        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Export Inventory Report");
        System.out.println("-----------------------------------------");

        System.out.print("Enter Start Date (DD/MM/YYYY): ");
        String startDate = scanner.nextLine();

        System.out.print("Enter End Date (DD/MM/YYYY): ");
        String endDate = scanner.nextLine();


        System.out.println("[*] Generating report for " + startDate + " to " + endDate + "...");

        Response<String> res = this.productServices.CreateFaultyProductReport(startDate, endDate);

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("\n--- Report Content ---");
            System.out.println(res.getReturnValue());
        }
        System.out.println("-----------------------------------------");
    }

    private void handleSupplierDiscount() {

        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Add Supplier Discount per Product");
        System.out.println("-----------------------------------------");

        System.out.print("Enter Product Catalog Number: ");
        String catNum = scanner.nextLine();

        int supplierDiscount = getIntInput("Enter Supplier Discount Percentage: ");

        Response<String> res = this.productServices.setSupplierDiscount(catNum, supplierDiscount);

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println("[V] SUCCESS: Supplier discount recorded.");
        }
        System.out.println("-----------------------------------------");
    }

    private boolean existInList(List<String> s,String value)
    {
        for(String id:s)
            if(id.equals(value))
                return true;
        return false;
    }
    public void HandleInventoryReport()
    {
        List<String> cats = new ArrayList<>();
        String choice ="";
        do {
            choice ="";
            System.out.println("Select Category to add to the report:");
            CategorySL c= this.HandleMainCategoryChoice();
            if(existInList(cats,c.Id))
                System.out.println("Category already exist in report.");
            else
                cats.add(c.Id);
            do {
                System.out.println("Add another? (y/n)");
                choice = scanner.nextLine();
            }while (!choice.equals("y") && !choice.equals("n"));

        }while(choice.equals("y"));

        Response<String> res = this.productServices.GetInventoryReport(cats);
        if(res.isError())
            throw new RuntimeException(res.getErrorMsg());
        System.out.println("Displaying Report:\n\n" + res.getReturnValue());
    }

    private void handleCategoryCreation()
    {
        int choice;
        System.out.println("\nSelect an option:");
        System.out.println("1.Create a main category");
        System.out.println("2.Create a sub category");
        System.out.println("3.Create a subsub category");
        System.out.println("4.Back to menu.");
        choice = 0;
        String catName = "";
        Response<String> res;
        do {
            choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1:
                System.out.println("Enter category name:");
                catName =scanner.nextLine();


                System.out.println("Enter discount (from 0 to 1 , eg 0.5 for 50%):");
                res = this.categoryServices.CreateCategory(catName, scanner.nextDouble());
                scanner.nextLine();
                if (res.isError()) {
                    System.out.println("[!] ERROR: " + res.getErrorMsg());
                } else {
                    System.out.println("[V] Category '" + catName + "' was created successfully.");
                }
                break;
            case 2:
                CategorySL c = this.HandleMainCategoryChoice();
                if (c == null) break;
                System.out.println("Enter Sub-category name:");
                catName = scanner.nextLine();
                res = this.categoryServices.CreateSubCategory(catName, 0, c.Id); //subcategory does not hold discount
                if (res.isError()) {
                    System.out.println("[!] ERROR: " + res.getErrorMsg());
                } else {
                    System.out.println("[V] Sub-Category '" + catName + "' added to " + c.name);
                }
                break;
            case 3:
                CategorySL main = this.HandleMainCategoryChoice();
                if (main == null) break;
                CategorySL sub = this.HandleSubCategoryChoice(main);
                if (sub == null) break;
                System.out.println("Enter Sub-Sub-category name:");
                catName = scanner.nextLine();
                res = this.categoryServices.CreateSubCategory(catName, 0, sub.Id); //subcategory does not hold discount
                if (res.isError()) {
                    System.out.println("[!] ERROR: " + res.getErrorMsg());
                } else {
                    System.out.println("[V] Sub-Sub-Category '" + catName + "' added to " + sub.name);
                }
                break;
            case 4:
                System.out.println("Returning to main menu...");
                return;
            default:
                System.out.println("Wrong input,try again");
        }
        }while (choice <0 || choice > 4);
    }
}
