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
            this.handleViewLowStock();
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
        System.out.println("5. Category Creations");
        System.out.println("6. Export Faulty Inventory Report by Dates");
        System.out.println("7. Add Supplier Discount per Product");
        System.out.println("8  Get inventory report by categories.");
        System.out.println("9  Create TEST data.");

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
                HandleCategoryCreation();
                break;

            case "6":
                handleFaultyInventoryReport();
                break;

            case "7":
                handleSupplierDiscount();
                break;
            case "8":
                HandleInventoryReport();
                break;
            case "9":
                CreateTestData();
                break;
            default:
                System.out.println("Invalid input. Please choose a number between 0 and 9.");
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
            System.out.println("Select a main category:\n");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println(i + 1 + "." + categories.get(i).name);
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

    private void handleFaultyInventoryReport() {

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
            scanner.nextLine(); //clears buffer
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
    private void CreateTestData()
    {
        Response<String> res;
        try {

            res = this.categoryServices.CreateCategory("Beverages", 0.05);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());

            String beveragesId = res.getReturnValue();

            res = this.categoryServices.CreateCategory("Bakery", 0.075);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());

            String bakeryId = res.getReturnValue();

            res = this.categoryServices.CreateCategory("Household", 0.1);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String householdId = res.getReturnValue();

            /*
            Subcategories
             */
            res =this.categoryServices.CreateSubCategory("Soft Drinks", 0, beveragesId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());

            String softDrinksId = res.getReturnValue();

            res =this.categoryServices.CreateSubCategory("Juices", 0, beveragesId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String juicesId = res.getReturnValue();


            res = this.categoryServices.CreateSubCategory("Bread", 0, bakeryId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String breadId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Pastries", 0, bakeryId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String pastriesId = res.getReturnValue();

            res =this.categoryServices.CreateSubCategory("Cleaning", 0, householdId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String cleaningId = res.getReturnValue();

            res =this.categoryServices.CreateSubCategory("Laundry", 0, householdId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String laundryId = res.getReturnValue();

            /*
        SubSubCategories
        Created the same way, with subcategory id as rootId
         */
            res = this.categoryServices.CreateSubCategory("250 ml", 0, softDrinksId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String softDrinks250mlId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("1 L", 0, softDrinksId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String softDrinks1LId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("500 ml", 0, juicesId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String juices500mlId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("1 L", 0, juicesId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String juices1LId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Small Loaf", 0, breadId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String breadSmallLoafId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Large Loaf", 0, breadId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String breadLargeLoafId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Single", 0, pastriesId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String pastriesSingleId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("Pack of 4", 0, pastriesId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String pastriesPack4Id = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("500 ml", 0, cleaningId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String cleaning500mlId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("1 L", 0, cleaningId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String cleaning1LId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("1 L", 0, laundryId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String laundry1LId = res.getReturnValue();

            res = this.categoryServices.CreateSubCategory("2 L", 0, laundryId);
            if(res.isError())
                throw new RuntimeException(res.getErrorMsg());
            String laundry2LId = res.getReturnValue();


            Response<String> pres;

            pres = this.productServices.addProduct(
                    "Coca Cola",
                    "BEV-001",
                    beveragesId,
                    softDrinksId,
                    softDrinks250mlId,
                    "A1-S1",
                    "Coca Cola",
                    30,
                    120,
                    2.5,
                    4.5,
                    20
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Sprite",
                    "BEV-002",
                    beveragesId,
                    softDrinksId,
                    softDrinks1LId,
                    "A1-S2",
                    "Coca Cola",
                    25,
                    80,
                    4.0,
                    6.5,
                    15
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Orange Juice",
                    "BEV-003",
                    beveragesId,
                    juicesId,
                    juices1LId,
                    "A1-S3",
                    "Tropicana",
                    20,
                    60,
                    5.0,
                    8.0,
                    12
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Apple Juice",
                    "BEV-004",
                    beveragesId,
                    juicesId,
                    juices500mlId,
                    "A1-S4",
                    "Prigat",
                    18,
                    50,
                    3.5,
                    6.0,
                    10
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "White Bread",
                    "BAK-001",
                    bakeryId,
                    breadId,
                    breadLargeLoafId,
                    "A2-S1",
                    "Angel",
                    15,
                    40,
                    4.0,
                    6.5,
                    10
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Whole Wheat Bread",
                    "BAK-002",
                    bakeryId,
                    breadId,
                    breadSmallLoafId,
                    "A2-S2",
                    "Angel",
                    12,
                    35,
                    3.5,
                    5.8,
                    8
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Butter Croissant",
                    "BAK-003",
                    bakeryId,
                    pastriesId,
                    pastriesSingleId,
                    "A2-S3",
                    "Bakery House",
                    20,
                    30,
                    2.0,
                    3.8,
                    10
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Chocolate Muffin Pack",
                    "BAK-004",
                    bakeryId,
                    pastriesId,
                    pastriesPack4Id,
                    "A2-S4",
                    "Bakery House",
                    10,
                    25,
                    6.0,
                    10.0,
                    6
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Dish Soap",
                    "HOU-001",
                    householdId,
                    cleaningId,
                    cleaning500mlId,
                    "A3-S1",
                    "Fairy",
                    16,
                    45,
                    5.5,
                    8.9,
                    10
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Floor Cleaner",
                    "HOU-002",
                    householdId,
                    cleaningId,
                    cleaning1LId,
                    "A3-S2",
                    "Sano",
                    14,
                    40,
                    7.0,
                    11.5,
                    8
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Laundry Detergent",
                    "HOU-003",
                    householdId,
                    laundryId,
                    laundry2LId,
                    "A3-S3",
                    "Ariel",
                    10,
                    35,
                    12.0,
                    18.5,
                    7
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());

            pres = this.productServices.addProduct(
                    "Fabric Softener",
                    "HOU-004",
                    householdId,
                    laundryId,
                    laundry1LId,
                    "A3-S4",
                    "Lenor",
                    11,
                    28,
                    8.0,
                    13.0,
                    6
            );
            if(pres.isError())
                throw new RuntimeException(pres.getErrorMsg());
        }
        catch (Exception e)
        {
            System.out.println("Error creating data. " + e.getMessage());
        }
    }
}
