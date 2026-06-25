package InventoryModule.PresentationLayer;

import CrossCuttingPackage.Notification;
import CrossCuttingPackage.Report;
import CrossCuttingPackage.Response;
import InventoryModule.ServiceLayer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InventoryCLI {

    private final Scanner scanner;
    private final ProductServices productServices;
    private final CategoryServices categoryServices;


    public InventoryCLI(Boolean shouldLoad,Scanner scanner){
        this.scanner = scanner;
        this.categoryServices=CategoryServices.GetInstance();
        this.productServices = ProductServices.getInstance();
        if(shouldLoad)
        {
            Response<String> res= productServices.Load();
            Response<String> res1= categoryServices.Load();
            if(res.isError()||res1.isError())
                throw new RuntimeException("Could not build data - " + res.getErrorMsg());
        }
    }

    public void start() {
        System.out.println("--- Welcome to ADSS Inventory System ---");
        while (true) {
            try {
                this.handleViewLowStock();
                displayMenu();

                String choice = scanner.nextLine();

                if (choice.equals("0")) {
                    System.out.println("Exiting system");
                    break;
                } else {
                    handleChoice(choice);
                }

            }
            catch (Exception e)
            {
                if (e.getMessage()!= null)
                    System.out.println("Client error:" + e.getMessage()); //catches client errors with scanner for instance
            }
        }
    }

    public void Clean()
    {
        productServices.Clean();
        categoryServices.Clean();
    }

    private void displayMenu(){
        System.out.println("\nChoose an option:");
        System.out.println("1.  Add Product");
        System.out.println("2   Add Category/ SubCategory/ SubsubCategory.");
        System.out.println("3.  Add faulty Product (Report Damage)");
        System.out.println("4.  Update Product");
        System.out.println("5.  Add Product/Category Discount");
        System.out.println("6.  View Stock Alerts (Products running out)");
        System.out.println("7.  Export Faulty Inventory Report by Dates");
        System.out.println("8.  Add Supplier Discount per Product");
        System.out.println("9.  Get inventory report by categories.");
        System.out.println("10. show all products");
        System.out.println("11. Purchase Product from Shelves/Stock");
        System.out.println("12. Find product final price to supply/customer");
        System.out.println("13. Create TEST data.");


        System.out.println("0.  Exit");
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
                handleFaultyInventoryReport();
                break;

            case "8":
                handleSupplierDiscount();
                break;

            case "9":
                HandleInventoryReport();
                break;

            case "10":
                displayAllProducts();
                break;

            case "11":
                handlePurchaseProduct();
                break;
            case "12":
                HandlePrice();
                break;
            case "13":
                CreateTestData();
                break;


            default:
                System.out.println("Invalid input. Please choose a number between 0 and 12.");
                break;
        }
    }

    private void handlePurchaseProduct() {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Purchase Product (Update Inventory)");
        System.out.println("-----------------------------------------");


        System.out.print("Enter Product Catalog Number: ");
        String catalogNum = scanner.nextLine();

        Response<ProductSL> productRes = this.productServices.getProductByCatalogNumber(catalogNum);




        if (productRes.isError()) {
            System.out.println("[!] ERROR: " + productRes.getErrorMsg());
            return;
        }

        ProductSL product = productRes.getReturnValue();

        System.out.println("\nProduct Found: " + product.name);
        System.out.println("Current Status:");
        System.out.println("   [1] Shelves: " + product.amount_on_shelves);
        System.out.println("   [2] Stock: " + product.amount_on_stock);
        System.out.println("-----------------------------------------");

        System.out.println("Select source to decrease from:");
        System.out.println("1. Shelves Only");
        System.out.println("2. Stock Only");
        System.out.println("3. Transfer from Stock to Shelves (Refill)");
        int sourceChoice = getIntInput("Your choice: ");


        int actionChoice=0;
        if(sourceChoice==1||sourceChoice==2) {
            System.out.println("1. Decrease Inventory (Purchase/Loss)");
            System.out.println("2. Increase Inventory (New Stock/Return)");
            actionChoice = getIntInput("Choose action: ");

        }



        int amountToShelves = 0;
        int amountToStock = 0;
        int amount = 0;


        int factor = (actionChoice == 1) ? 1 : -1;

        switch (sourceChoice) {
            case 1:
                amount = getIntInput("Enter amount: ");
                amountToShelves = amount * factor;
                break;
            case 2:
                amount = getIntInput("Enter amount: ");
                amountToStock = amount * factor;
                break;
            case 3:
                amount = getIntInput("Enter amount to transfer from Stock to Shelves: ");
                amountToStock = amount;
                amountToShelves = -amount;
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        Response<String> purchaseRes = this.productServices.PurchaseProduct(catalogNum, amountToShelves, amountToStock);

        if (purchaseRes.isError()) {
            System.out.println("[!] PURCHASE FAILED: " + purchaseRes.getErrorMsg());
        } else {
            System.out.println("[V] Inventory updated successfully!");

        }
        System.out.println("-----------------------------------------");
    }

    private void displayAllProducts() {
        Response<List<ProductSL>> res = this.productServices.getAllProducts();

        int tableWidth = 190;
        System.out.println("\n" + "=".repeat(tableWidth));
        System.out.printf("| %-190s |%n", "                                                        FULL INVENTORY DETAILED REPORT");
        System.out.println("=".repeat(tableWidth));

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            List<ProductSL> products = res.getReturnValue();

            if (products == null || products.isEmpty()) {
                System.out.printf("| %-190s |%n", "No products found in the system.");
            } else {
                System.out.printf("| %-10s | %-18s | %-15s | %-8s | %-15s | %-7s | %-7s | %-15s | %-15s | %-8s | %-12s | %-15s | %-15s | %-18s |%n",
                        "Catalog #", "Name", "Warehouse", "Loc.", "Manufacturer", "Shelf", "Stock", "C.Price(before)", "S.Price(before)", "S.Disc","P.Disc", "Main Cat", "Sub Cat", "SubSub");
                System.out.println("-".repeat(tableWidth));

                for (ProductSL p : products) {
                    System.out.printf("| %-10s | %-18s | %-15s | %-8s | %-15s | %-7d | %-7d | %-15.2f | %-15.2f | %-7.1f%% | %-7.1f%% | %-12s | %-15s | %-18s |%n",
                            p.catalog_number,
                            truncate(p.name, 18),
                            truncate(p.warehouse != null ? p.warehouse.getName() : "N/A", 15),
                            p.location,
                            truncate(p.manufacturer, 15),
                            p.amount_on_shelves,
                            p.amount_on_stock,
                            p.price_to_consumer,
                            p.price_to_supply,
                            p.supplier_discount * 100,
                            p.getTotalProductDiscount() * 100,
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
            throw new RuntimeException();
        }

        List<CategorySL> categories = res.getReturnValue();
        if (categories == null || categories.isEmpty()) {
            System.out.println("[!] No categories available in the system.");
            System.out.println("[*] Please go to Option 2 in the main menu to add a category first.");
            throw new RuntimeException();
        }

        try {
            int choice = 0;
            do {
                System.out.println("Select a main category:\n");
                for (int i = 0; i < categories.size(); i++) {
                    System.out.println(i + 1 + "." + categories.get(i).name);
                }
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice < 1 || choice > categories.size())
                    System.out.println("Choice is not in allowed range. try again:");
            } while (choice < 1 || choice > categories.size());
            return categories.get(choice - 1);
        }
        catch (Exception e)
        {
            System.out.println("Error: Failed to read bad input, returning to menu...");
            scanner.nextLine(); // clears buffer.
            throw new RuntimeException();
        }
    }

    private CategorySL HandleSubCategoryChoice(CategorySL root)
    {
        Response<List<CategorySL>> res = this.categoryServices.GetSubCategories(root.Id);
        if(res.isError()){
            System.out.println("[!] Error fetching categories: " + res.getErrorMsg());
            throw new RuntimeException();
        }
        List<CategorySL> categories = res.getReturnValue();
        if (categories == null || categories.isEmpty()) {
            System.out.println("[!] No sub categories available in the system.");
            System.out.println("[*] Please use Option 2 -> 'Create a sub category' and link it to '" + root.name + "'.");
            throw new RuntimeException();
        }

        try {
            int choice = 0;
            do {
                System.out.println("Select a sub category to the category " + root.name + ":");
                for (int i = 0; i < categories.size(); i++) {
                    System.out.println(i + 1 + "." + categories.get(i).name);
                }
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice < 1 || choice > categories.size())
                    System.out.println("Choice is not in allowed range. try again:");
            } while (choice < 1 || choice > categories.size());
            return categories.get(choice - 1);
        }
        catch (Exception e)
        {
            System.out.println("Error: Failed to read bad input, returning to menu...");
            scanner.nextLine(); // clears buffer.
            throw new RuntimeException();
        }
    }

    private CategorySL HandleCategoryChoice()
    {
        Response<List<CategorySL>> res = this.categoryServices.GetAllCategories();
        if(res.isError()){
            System.out.println("[!] Error fetching categories: " + res.getErrorMsg());
            throw new RuntimeException();
        }
        List<CategorySL> categories = res.getReturnValue();
        if (categories == null || categories.isEmpty()) {
            System.out.println("[!] No  categories available in the system.");
            System.out.println("[*] Please use Option 1 -> 'Create a category");
            throw new RuntimeException();
        }

        try {
            int choice = 0;
            do {
                System.out.println("Select a category:");
                for (int i = 0; i < categories.size(); i++) {
                    System.out.println(i + 1 + "." + categories.get(i).Id);
                }
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice < 1 || choice > categories.size())
                    System.out.println("Choice is not in allowed range. try again:");
            } while (choice < 1 || choice > categories.size());
            return categories.get(choice - 1);
        }
        catch (Exception e)
        {
            System.out.println("Error: Failed to read bad input, returning to menu...");
            scanner.nextLine(); // clears buffer.
            throw new RuntimeException();
        }
    }


    private void handleAddProduct() {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Adding a New Product");
        System.out.println("-----------------------------------------");

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Catalog Number: ");
        String catalogNumber = scanner.nextLine();
        String subsubId = null;
        String choice = "";

        CategorySL main = this.HandleMainCategoryChoice();
        CategorySL sub = this.HandleSubCategoryChoice(main);
        System.out.println("Do you wish to add a sub-sub category? (y/n)");

        do {
            choice = scanner.nextLine();
        } while(!choice.equals("y") && !choice.equals("n"));
        if(choice.equals("y"))
            subsubId = this.HandleSubCategoryChoice(sub).Id;

        System.out.print("Enter Warehouse Name (e.g., Main Warehouse): ");
        String warehouseName = scanner.nextLine();

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

        Response<String> res = this.productServices.addProduct(name, catalogNumber, main.Id, sub.Id, subsubId, warehouseName, location, manu,
                amountOnShelves, amountOnStock, consumerPrice, supplyPrice, minAmount);

        if (res.isError()) {
            System.out.println("\n[!] FAILURE: Could not add product.");
            System.out.println("Reason: " + res.getErrorMsg());
            throw new RuntimeException();
        }
        else {
            System.out.println("\n[V] SUCCESS: Product '" + name + "' added successfully!");
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
            throw new RuntimeException();
        }
        else {
                System.out.println("\n[V] SUCCESS: Bad Product report was created successfully!, Report ID:" + res.getReturnValue());
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
        System.out.println("2. Warehouse Name");
        System.out.println("3. Storage Location");
        System.out.println("4. Consumer Prices");
        System.out.println("5. Supply Prices");
        System.out.println("6. Shelves Amounts");
        System.out.println("7. Stock Amounts");
        System.out.println("8. Minimum Amount Alert");
        System.out.println("0. Cancel and return to main menu");

        int updateChoice = getIntInput("\nPlease choose an option (0-8): ");

        Response<String> res=null;
        switch (updateChoice){
            case 0:
                System.out.println("Returning to main menu...");
                break;

            case 1:
                System.out.print("Enter New Name: ");
                String name = scanner.nextLine();
                res = this.productServices.update(catalogNumber, name, null, null, null, null, null, null, null);
                break;

            case 2:
                System.out.print("Enter New Warehouse Name: ");
                String warehouse = scanner.nextLine();
                res = this.productServices.update(catalogNumber, null, warehouse, null, null, null, null, null, null);
                break;

            case 3:
                System.out.print("Enter New Storage Location: ");
                String location = scanner.nextLine();
                res = this.productServices.update(catalogNumber, null, null, location, null, null, null, null, null);
                break;

            case 4:
                double consumerPrice = getDoubleInput("Enter New Consumer Price: ");
                res = this.productServices.update(catalogNumber, null, null, null, consumerPrice, null, null, null, null);
                break;

            case 5:
                double supplyPrice = getDoubleInput("Enter New Supply Price: ");
               res =  this.productServices.update(catalogNumber, null, null, null, null, supplyPrice, null, null, null);
                break;

            case 6:
                int shelvesAmount = getIntInput("Enter New Shelves Amount: ");
                res = this.productServices.update(catalogNumber, null, null, null, null, null, shelvesAmount, null, null);
                break;

            case 7:
                int stockAmount = getIntInput("Enter New Stock Amount: ");
               res=  this.productServices.update(catalogNumber, null, null, null, null, null, null, stockAmount, null);
                break;

            case 8:
                int minAlert = getIntInput("Enter New Minimum Amount Alert: ");
                res = this.productServices.update(catalogNumber, null, null, null, null, null, null, null, minAlert);
                break;

            default:
                System.out.println("Invalid input. Please choose a number between 0 and 8.");
                break;
        }
        if(res.isError()){
            System.out.println("[!] FAILURE: " + res.getErrorMsg());
            System.out.println("-----------------------------------------");
        }
    }

    private void catDiscount()
    {
        CategorySL c = this.HandleMainCategoryChoice();
        double discountPercentage = -1;

        do {
            discountPercentage = getDoubleInput("Enter Discount Percentage (from 0 to 1 eg 0.5 for 50%): ");
        } while (discountPercentage < 0 || discountPercentage > 1);

        System.out.print("Enter Promotion End Date (DD/MM/YYYY): ");
        String endDate = scanner.nextLine();

        Response<String> res = this.categoryServices.SetCategoryDiscount(c.Id, discountPercentage, endDate);

        if (res.isError()) {
            System.out.println("[!] FAILURE: " + res.getErrorMsg());
            System.out.println("-----------------------------------------");
            throw new RuntimeException();
        } else {
            System.out.println("[V] SUCCESS: Discount applied successfully!, Promotion ID:" + res.getReturnValue());
        }
        System.out.println("-----------------------------------------");
    }

    private void productDiscount()
    {
        System.out.print("Please enter catalog number: ");
        String cat_number = scanner.nextLine();
        double discountPercentage = -1;

        do {
            discountPercentage = getDoubleInput("Enter Discount Percentage (from 0 to 1 eg 0.5 for 50%): ");
        } while (discountPercentage < 0 || discountPercentage > 1);

        System.out.print("Enter Promotion End Date (DD/MM/YYYY): ");
        String endDate = scanner.nextLine();

        Response<String> res = this.productServices.SetProductDiscount(cat_number, discountPercentage, endDate);

        if (res.isError()) {
            System.out.println("[!] FAILURE: " + res.getErrorMsg());
            System.out.println("-----------------------------------------");
            throw new RuntimeException();
        } else {
            System.out.println("[V] SUCCESS: Discount applied successfully! Promotion id:" + res.getReturnValue());
        }
        System.out.println("-----------------------------------------");
    }


    private void handleDiscountProduct() {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Add Product/Category Discount");
        System.out.println("-----------------------------------------");

        String choice = "";
        String cat = "";
        String pId ="";
        Response<String> res = null;
        Response<Report> repRes = null;
        do {

            System.out.println("Select action:");
            System.out.println("1.Set category discount.");
            System.out.println("2.Set product discount.");
            System.out.println("3.Remove category discount.");
            System.out.println("4.Remove product discount.");
            System.out.println("5.View Category promotions.");
            System.out.println("6.View Product promotions.");
            System.out.println("0.Return to menu.");
            choice = scanner.nextLine();
        }while(!choice.equals("0") && !choice.equals("1")&& !choice.equals("2")&& !choice.equals("3")&& !choice.equals("4")&& !choice.equals("5")&& !choice.equals("6"));
        switch (choice){
            case "1":
                catDiscount();
                break;
            case "2":
                productDiscount();
                break;
            case "3":
                System.out.println("Please enter categoryId:");
                cat = scanner.nextLine();
                System.out.println("Please enter promotion Id:");
                pId = scanner.nextLine();
                res = categoryServices.RemoveProductDiscount(cat,pId);
                if (res.isError()) {
                    System.out.println("[!] ERROR: " + res.getErrorMsg());
                    System.out.println("-----------------------------------------");
                    throw new RuntimeException();
                }
                System.out.println("Promotion:" + pId +" was removed successfully.");
                break;
            case "4":
                System.out.println("Please enter catalog number:");
                cat = scanner.nextLine();
                System.out.println("Please enter promotion Id:");
                pId = scanner.nextLine();
                res = productServices.RemoveProductDiscount(cat,pId);
                if (res.isError()) {
                    System.out.println("[!] ERROR: " + res.getErrorMsg());
                    System.out.println("-----------------------------------------");
                    throw new RuntimeException();
                }
                System.out.println("Promotion:" + pId +" was removed successfully.");
                break;
            case "5":
                System.out.println("Please enter categoryId:");
                cat = scanner.nextLine();
                repRes = categoryServices.GetCategoryPromotions(cat);
                if(repRes.isError()){
                    System.out.println("[!] ERROR: " + repRes.getErrorMsg());
                    System.out.println("-----------------------------------------");
                    throw new RuntimeException();
                }
                System.out.println("Displaying report:\n" + repRes.getReturnValue().GetReport());
                break;
            case "6":
                System.out.println("Please enter catalog number:");
                cat = scanner.nextLine();
                repRes = productServices.GetProductPromotions(cat);
                if(repRes.isError()){
                    System.out.println("[!] ERROR: " + repRes.getErrorMsg());
                    System.out.println("-----------------------------------------");
                    throw new RuntimeException();
                }
                System.out.println("Displaying report:\n" + repRes.getReturnValue().GetReport());
                break;
            case "0":
                return;
        }
    }


    private void handleViewLowStock() {
        int tableWidth = 90;
        System.out.println("\n" + "=".repeat(tableWidth));
        System.out.printf("| %-86s |%n", "                STOCK ALERT: PRODUCTS BELOW MINIMUM AMOUNT");
        System.out.println("=".repeat(tableWidth));

        Response<List<Notification>> res = this.productServices.getLowStockAlerts();

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
            throw new RuntimeException();
        }

        List<Notification> lowStock = res.getReturnValue();

        if (lowStock == null || lowStock.isEmpty()) {
            System.out.printf("| %-86s |%n", "All products are well-stocked. No alerts at this time.");
        }
        else {
            System.out.printf("| %-7s | %-12s | %-20s | %-10s | %-10s | %-10s |%n",
                        "STATUS", "Catalog #", "Product Name", "Current", "Min. Alert", "Location");
            System.out.println("-".repeat(tableWidth));

            for (Notification p : lowStock) {
                int totalCurrent = p.getAmountOnShelf() + p.getAmountInStock();

                System.out.printf("| [!!]   | %-12s | %-20s | %-10d | %-10d | %-10s |%n",
                            p.getCatalog_number(),
                            truncate(p.getpName(), 20),
                            totalCurrent,
                            p.getMin(),
                            p.getLocation());
                }
            System.out.println("-".repeat(tableWidth));
            System.out.println("[*] Summary: Found " + lowStock.size() + " products that require restocking.");
        }
        System.out.println("=".repeat(tableWidth) + "\n");
    }



    private void handleSupplierDiscount() {

        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Add Supplier Discount per Product");
        System.out.println("-----------------------------------------");

        System.out.print("Enter Product Catalog Number: ");
        String catNum = scanner.nextLine();

        double supplierDiscount = getDoubleInput("Enter Supplier Discount Percentage: ");

        Response<String> res = this.productServices.setSupplierDiscount(catNum, supplierDiscount);

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
            System.out.println("-----------------------------------------");
            throw new RuntimeException();
        } else {
            System.out.println("[V] SUCCESS: Supplier discount recorded.");
        }
        System.out.println("-----------------------------------------");
    }

    /*
    Helper method that looks for a string in a list of strings, and returns true if it finds a match.
     */
    private boolean existInList(List<String> s,String value)
    {
        for(String id:s)
            if(id.equals(value))
                return true;
        return false;
    }



    private void handleFaultyInventoryReport() {

        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Export Inventory Report");
        System.out.println("-----------------------------------------");

        System.out.print("Enter Start Date (DD/MM/YYYY): ");
        String startDate = scanner.nextLine();

        System.out.print("Enter End Date (DD/MM/YYYY): ");
        String endDate = scanner.nextLine();


        System.out.println("[*] Generating report for " + startDate + " to " + endDate + "...");

        Response<Report> res = this.productServices.CreateFaultyProductReport(startDate, endDate);

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
            System.out.println("-----------------------------------------");
            throw new RuntimeException();
        } else {
            System.out.println("\n--- Report Content ---");
            System.out.println(res.getReturnValue().GetReport());
        }
        System.out.println("-----------------------------------------");
    }

    public void HandleInventoryReport() {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Show Inventory Report");
        System.out.println("-----------------------------------------");

        List<String> cats = new ArrayList<>();
        String choice = "";
        do {
            choice = "";
            System.out.println("Select Category to add to the report:");
            CategorySL c = this.HandleCategoryChoice();
            if (existInList(cats, c.Id))
                System.out.println("Category already exist in report.");
            else
                cats.add(c.Id);

            do {
                System.out.println("Add another? (y/n)");
                choice = scanner.nextLine();
            } while (!choice.equals("y") && !choice.equals("n"));

        } while (choice.equals("y"));

        Response<Report> res = this.productServices.GetInventoryReport(cats);
        if (res.isError()){
            System.out.println("[!] ERROR: " + res.getErrorMsg());
            System.out.println("-----------------------------------------");
            throw new RuntimeException();
        }

        System.out.println("Displaying Report:\n\n" + res.getReturnValue().GetReport());
    }

    private void handleCategoryCreation()
    {

        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Add Category");
        System.out.println("-----------------------------------------");


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
            choice = getIntInput("Please enter your choice: ");
            switch (choice) {
                case 1:
                    System.out.println("Enter category name:");
                    catName =scanner.nextLine();


                    System.out.println("Enter discount (from 0 to 1 , eg 0.5 for 50%):");
                    double dicount = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Enter discount end date (DD/MM/YYYY): ");
                    String endDate = scanner.nextLine();

                    res = this.categoryServices.CreateCategory(catName, dicount,endDate);

                    if (res.isError()) {
                        System.out.println("[!] ERROR: " + res.getErrorMsg());
                        throw new RuntimeException();
                    } else {
                        System.out.println("[V] Category '" + catName + "' was created successfully.");
                    }
                    break;
                case 2:
                    CategorySL c = this.HandleMainCategoryChoice();
                    if (c == null) break;
                    System.out.println("Enter Sub-category name:");
                    catName = scanner.nextLine();

                    res = this.categoryServices.CreateSubCategory(catName, 0, null,c.Id); //subcategory does not hold discount
                    if (res.isError()) {
                        System.out.println("[!] ERROR: " + res.getErrorMsg());
                        throw new RuntimeException();
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
                    res = this.categoryServices.CreateSubCategory(catName, 0,null, sub.Id); //subcategory does not hold discount
                    if (res.isError()) {
                        System.out.println("[!] ERROR: " + res.getErrorMsg());
                        throw new RuntimeException();
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
        }
        while (choice <0 || choice > 4);
    }

    private void HandlePrice()
    {
        String choice ="";
        System.out.println("Please select an option:");
        System.out.println("1.Final price to consumer.");
        System.out.println("2.Final price to supply");
        System.out.println("0.Back to menu");
        do {
            choice = scanner.nextLine();
        }while(!choice.equals("0")&&!choice.equals("1")&&!choice.equals("2"));
        String id;
        Response<Double> res;
        switch (choice)
        {
            case "1":
                System.out.println("Please enter catalog number:");
                id = scanner.nextLine();

                res =this.productServices.GetProductPrice(id);
                if(res.isError())
                {
                    System.out.println("Error: could not get price");
                    System.out.println("Reason:"+res.getErrorMsg());
                    System.out.println("-----------------------------------------");
                    throw new RuntimeException();
                }
                System.out.println("Price for customer for product:"+id+", is:"+res.getReturnValue());
                System.out.println("-----------------------------------------");
                break;
            case "2":
                System.out.println("Please enter catalog number:");
                id = scanner.nextLine();

                res =this.productServices.GetProductSupplyPrice(id);
                if(res.isError())
                {
                    System.out.println("Error: could not get price");
                    System.out.println("Reason:"+res.getErrorMsg());
                    System.out.println("-----------------------------------------");
                    throw new RuntimeException();
                }

                System.out.println("Price for customer for product:"+id+", is:"+res.getReturnValue());
                System.out.println("-----------------------------------------");
                break;

        }

    }

    public void CreateTestData()
    {
        Response<String> res;
        try {
            String defaultEndDate = "31/12/2030";

            res = this.categoryServices.CreateCategory("Beverages", 0.05, defaultEndDate);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String beveragesId = res.getReturnValue();

            res = this.categoryServices.CreateCategory("Bakery", 0.075, defaultEndDate);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String bakeryId = res.getReturnValue();

            res = this.categoryServices.CreateCategory("Household", 0.1, defaultEndDate);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String householdId = res.getReturnValue();

        /*
        Subcategories
         */
            res = this.categoryServices.CreateSubCategory("Soft Drinks", 0, defaultEndDate, beveragesId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String softDrinksId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Juices", 0, defaultEndDate, beveragesId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String juicesId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Bread", 0, defaultEndDate, bakeryId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String breadId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Pastries", 0, defaultEndDate, bakeryId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String pastriesId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Cleaning", 0, defaultEndDate, householdId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String cleaningId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Laundry", 0, defaultEndDate, householdId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String laundryId = res.getReturnValue();

        /*
        SubSubCategories
         */
            res = this.categoryServices.CreateSubCategory("250 ml", 0, defaultEndDate, softDrinksId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String softDrinks250mlId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("1 L", 0, defaultEndDate, softDrinksId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String softDrinks1LId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("500 ml", 0, defaultEndDate, juicesId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String juices500mlId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("1 L", 0, defaultEndDate, juicesId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String juices1LId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Small Loaf", 0, defaultEndDate, breadId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String breadSmallLoafId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Large Loaf", 0, defaultEndDate, breadId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String breadLargeLoafId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Single", 0, defaultEndDate, pastriesId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String pastriesSingleId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Pack of 4", 0, defaultEndDate, pastriesId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String pastriesPack4Id = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("500 ml", 0, defaultEndDate, cleaningId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String cleaning500mlId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("1 L", 0, defaultEndDate, cleaningId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String cleaning1LId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("1 L", 0, defaultEndDate, laundryId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String laundry1LId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("2 L", 0, defaultEndDate, laundryId);
            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
            String laundry2LId = res.getReturnValue();

            Response<String> pres;

            pres = this.productServices.addProduct(
                    "Coca Cola", "BEV-001", beveragesId, softDrinksId, softDrinks250mlId,
                    "Main Warehouse", "A-23", "Coca Cola", 30, 120, 2.5, 4.5, 20
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Sprite", "BEV-002", beveragesId, softDrinksId, softDrinks1LId,
                    "Main Warehouse", "A-56", "Coca Cola", 25, 80, 4.0, 6.5, 15
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Orange Juice", "BEV-003", beveragesId, juicesId, juices1LId,
                    "Main Warehouse", "d-23", "Tropicana", 20, 60, 5.0, 8.0, 12
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Apple Juice", "BEV-004", beveragesId, juicesId, juices500mlId,
                    "Main Warehouse", "g-4", "Prigat", 18, 50, 3.5, 6.0, 10
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "White Bread", "BAK-001", bakeryId, breadId, breadLargeLoafId,
                    "Main Warehouse", "w-65", "Angel", 15, 40, 4.0, 6.5, 10
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Whole Wheat Bread", "BAK-002", bakeryId, breadId, breadSmallLoafId,
                    "Main Warehouse", "e-5", "Angel", 12, 35, 3.5, 5.8, 8
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Butter Croissant", "BAK-003", bakeryId, pastriesId, pastriesSingleId,
                    "Main Warehouse", "c-23", "Bakery House", 20, 30, 2.0, 3.8, 10
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Chocolate Muffin Pack", "BAK-004", bakeryId, pastriesId, pastriesPack4Id,
                    "Main Warehouse", "A-14", "Bakery House", 10, 25, 6.0, 10.0, 6
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Dish Soap", "HOU-001", householdId, cleaningId, cleaning500mlId,
                    "Main Warehouse", "s-14", "Fairy", 16, 45, 5.5, 8.9, 10
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Floor Cleaner", "HOU-002", householdId, cleaningId, cleaning1LId,
                    "Main Warehouse", "k-23", "Sano", 14, 40, 7.0, 11.5, 8
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Laundry Detergent", "HOU-003", householdId, laundryId, laundry2LId,
                    "Main Warehouse", "y-4", "Ariel", 10, 35, 12.0, 18.5, 7
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Fabric Softener", "HOU-004", householdId, laundryId, laundry1LId,
                    "Main Warehouse", "w-7", "Lenor", 11, 28, 8.0, 13.0, 6
            );
            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());

            System.out.println("[V] Inventory test data loaded successfully!");
        }
        catch (Exception e) {
            System.out.println("Error creating data: " + e.getMessage());
        }
    }
//
//    private void CreateTestData()
//    {
//        Response<String> res;
//        try {
//            String defaultEndDate = "31/12/2030";
//
//            res = this.categoryServices.CreateCategory("Beverages", 0.05, defaultEndDate);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String beveragesId = res.getReturnValue();
//
//            res = this.categoryServices.CreateCategory("Bakery", 0.075, defaultEndDate);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String bakeryId = res.getReturnValue();
//
//            res = this.categoryServices.CreateCategory("Household", 0.1, defaultEndDate);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String householdId = res.getReturnValue();
//
//
//            res = this.categoryServices.CreateCategory("aaa", 0.0, defaultEndDate);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String aaa = res.getReturnValue();
//            /*
//            Subcategories
//             */
//
//            res = this.categoryServices.CreateSubCategory("bbb", 0, defaultEndDate, aaa);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String bbb = res.getReturnValue();
//
//
//            res = this.categoryServices.CreateSubCategory("Soft Drinks", 0, defaultEndDate, beveragesId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String softDrinksId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("Juices", 0, defaultEndDate, beveragesId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String juicesId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("Bread", 0, defaultEndDate, bakeryId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String breadId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("Pastries", 0, defaultEndDate, bakeryId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String pastriesId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("Cleaning", 0, defaultEndDate, householdId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String cleaningId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("Laundry", 0, defaultEndDate, householdId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String laundryId = res.getReturnValue();
//
//            /*
//            SubSubCategories
//             */
//            res = this.categoryServices.CreateSubCategory("250 ml", 0, defaultEndDate, softDrinksId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String softDrinks250mlId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("1 L", 0, defaultEndDate, softDrinksId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String softDrinks1LId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("500 ml", 0, defaultEndDate, juicesId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String juices500mlId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("1 L", 0, defaultEndDate, juicesId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String juices1LId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("Small Loaf", 0, defaultEndDate, breadId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String breadSmallLoafId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("Large Loaf", 0, defaultEndDate, breadId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String breadLargeLoafId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("Single", 0, defaultEndDate, pastriesId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String pastriesSingleId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("Pack of 4", 0, defaultEndDate, pastriesId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String pastriesPack4Id = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("500 ml", 0, defaultEndDate, cleaningId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String cleaning500mlId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("1 L", 0, defaultEndDate, cleaningId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String cleaning1LId = res.getReturnValue();
//
//            res = this.categoryServices.CreateSubCategory("1 L", 0, defaultEndDate, laundryId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String laundry1LId = res.getReturnValue();
//
//
//            res = this.categoryServices.CreateSubCategory("2 L", 0, defaultEndDate, laundryId);
//            if(res.isError()) throw new RuntimeException(res.getErrorMsg());
//            String laundry2LId = res.getReturnValue();
//            Response<String> pres;
//
//            pres = this.productServices.addProduct(
//                    "Coca Cola", "BEV-001", beveragesId, softDrinksId, softDrinks250mlId,
//                    "Main Warehouse", "A-23", "Coca Cola", 30, 120, 2.5, 4.5, 20
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Sprite", "BEV-002", beveragesId, softDrinksId, softDrinks1LId,
//                    "Main Warehouse", "A-56", "Coca Cola", 25, 80, 4.0, 6.5, 15
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Orange Juice", "BEV-003", beveragesId, juicesId, juices1LId,
//                    "Main Warehouse", "d-23", "Tropicana", 20, 60, 5.0, 8.0, 12
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Apple Juice", "BEV-004", beveragesId, juicesId, juices500mlId,
//                    "Main Warehouse", "g-4", "Prigat", 18, 50, 3.5, 6.0, 10
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "White Bread", "BAK-001", bakeryId, breadId, breadLargeLoafId,
//                    "Main Warehouse", "w-65", "Angel", 15, 40, 4.0, 6.5, 10
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Whole Wheat Bread", "BAK-002", bakeryId, breadId, breadSmallLoafId,
//                    "Main Warehouse", "e-5", "Angel", 12, 35, 3.5, 5.8, 8
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Butter Croissant", "BAK-003", bakeryId, pastriesId, pastriesSingleId,
//                    "Main Warehouse", "c-23", "Bakery House", 20, 30, 2.0, 3.8, 10
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Chocolate Muffin Pack", "BAK-004", bakeryId, pastriesId, pastriesPack4Id,
//                    "Main Warehouse", "A-14", "Bakery House", 10, 25, 6.0, 10.0, 6
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Dish Soap", "HOU-001", householdId, cleaningId, cleaning500mlId,
//                    "Main Warehouse", "s-14", "Fairy", 16, 45, 5.5, 8.9, 10
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Floor Cleaner", "HOU-002", householdId, cleaningId, cleaning1LId,
//                    "Main Warehouse", "k-23", "Sano", 14, 40, 7.0, 11.5, 8
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Laundry Detergent", "HOU-003", householdId, laundryId, laundry2LId,
//                    "Main Warehouse", "y-4", "Ariel", 10, 35, 12.0, 18.5, 7
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            pres = this.productServices.addProduct(
//                    "Fabric Softener", "HOU-004", householdId, laundryId, laundry1LId,
//                    "Main Warehouse", "w-7", "Lenor", 11, 28, 8.0, 13.0, 6
//            );
//            if(pres.isError()) throw new RuntimeException(pres.getErrorMsg());
//
//            System.out.println("[V] Test data loaded successfully!");
//        }
//        catch (Exception e) {
//            System.out.println("Error creating data. " + e.getMessage());
//        }
//    }
}
