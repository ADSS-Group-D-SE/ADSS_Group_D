package PresentationLayer;

import ServiceLayer.CategorySL;
import ServiceLayer.CategoryServices;
import ServiceLayer.ProductServices;
import ServiceLayer.Response;

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
        System.out.println("2. Add faulty Product (Report Damage)");
        System.out.println("3. Update Product");
        System.out.println("4. Add Product/Category Discount");
        System.out.println("5. View Stock Alerts (Products running out)");
        System.out.println("6. Export Faulty Inventory Report by Dates");
        System.out.println("7. Add Supplier Discount per Product");
        System.out.println("8  Get inventory report by categories.");

        System.out.println("0. Exit");
        System.out.print("Please enter your choice: ");
    }

    private void handleChoice(String num){
        switch (num) {
            case "1":
                handleAddProduct();
                break;

            case "2":
                handleAddFaultyProduct();
                break;

            case "3":
                handleUpdateProduct();
                break;

            case "4":
                handleDiscountProduct();
                break;

            case "5":
                handleViewLowStock();
                break;

            case "6":
                handleInventoryReport();
                break;

            case "7":
                handleSupplierDiscount();
                break;

            default:
                System.out.println("Invalid input. Please choose a number between 0 and 7.");
                break;
        }
    }

    private CategorySL HandleMainCategoryChoice()
    {
        Response<List<CategorySL>> res = this.categoryServices.GetMainCategories();
        if(res.isError())
            throw new RuntimeException(res.getErrorMsg());

        List<CategorySL> categories = res.getReturnValue();
        if(categories.isEmpty())
            throw new RuntimeException("No categories to be selected.");

        int choice = 0;
        do {
            System.out.println("Select a main category:");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println(i + 1 + "." + categories.get(i).name + "\n");
            }
            choice = scanner.nextInt();
            if(choice < 1 || choice > categories.size())
                System.out.println("Choice is not in allowed range. try again:");
        }while(choice < 1 || choice > categories.size());
        return categories.get(choice-1);
    }

    private CategorySL HandleSubCategoryChoice(CategorySL root)
    {
        Response<List<CategorySL>> res = this.categoryServices.GetSubCategories(root.Id);
        if(res.isError())
            throw new RuntimeException(res.getErrorMsg());

        List<CategorySL> categories = res.getReturnValue();
        if(categories.isEmpty())
            throw new RuntimeException("No sub-categories to be selected.");

        int choice = 0;
        do {
            System.out.println("Select a sub category to the category " +root.name +":");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println(i + 1 + "." + categories.get(i).name + "\n");
            }
            choice = scanner.nextInt();
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
            CategorySL sub = this.HandleSubCategoryChoice(main);
            CategorySL subsub = this.HandleSubCategoryChoice(sub);

            String categoriesInput = scanner.nextLine();
            List<String> categories = Arrays.asList(categoriesInput.split("\\s*,\\s*"));

            System.out.print("Enter Storage Location (e.g., A-12): ");
            String location = scanner.nextLine();
            System.out.print("Enter Product manufacturer Location (e.g., A-12): ");
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

        int updateChoice = getIntInput("\nPlease choose an option (0-5): ");

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

            case 8:
                this.HandleInventoryReport();
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

    private void handleViewLowStock() {
        System.out.println("\n-----------------------------------------");
        System.out.println(">>> Action: Viewing Stock Alerts (Running Out)");
        System.out.println("-----------------------------------------");

        Response<String> res = this.productServices.getLowStockAlerts();

        if (res.isError()) {
            System.out.println("[!] ERROR: " + res.getErrorMsg());
        } else {
            System.out.println(res.getReturnValue());
        }
        System.out.println("-----------------------------------------");
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

    private void HandleCategoryCreation()
    {
        int choice;
        System.out.println("Select an option:");
        System.out.println("1.Create a main category");
        System.out.println("2.Create a sub category");
        System.out.println("3.Create a subsub category");
        System.out.println("4.Back to menu.");
        choice = 0;
        String catName = "";
        Response<String> res;
        do {
            choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.println("Enter category name:");
                catName = scanner.nextLine();
                System.out.println("Enter discount (from 0 to 1 , eg 0.5 for 50%):");
                res = this.categoryServices.CreateCategory(catName, scanner.nextDouble());

                if (res.isError())
                    throw new RuntimeException(res.getErrorMsg());
                System.out.println("Category was created successfully");
                break;
            case 2:
                CategorySL c = this.HandleMainCategoryChoice();
                System.out.println("Enter Sub-category name:");
                catName = scanner.nextLine();
                res = this.categoryServices.CreateSubCategory(catName, 0, c.Id); //subcategory does not hold discount
                if (res.isError())
                    throw new RuntimeException(res.getErrorMsg());
                System.out.println("Sub-Category was created successfully");
                break;
            case 3:
                CategorySL main = this.HandleMainCategoryChoice();
                CategorySL sub = this.HandleSubCategoryChoice(main);
                System.out.println("Enter Sub-Sub-category name:");
                catName = scanner.nextLine();
                res = this.categoryServices.CreateSubCategory(catName, 0, sub.Id); //subcategory does not hold discount
                if (res.isError())
                    throw new RuntimeException(res.getErrorMsg());
                System.out.println("Sub-Sub-Category was created successfully");
                break;
            case 4:
                return;
            default:
                System.out.println("Wrong input,try again");
        }
        }while (choice <0 || choice > 4);
    }
}
