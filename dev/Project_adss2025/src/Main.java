import InventoryModule.PresentationLayer.InventoryCLI;
import SupplierModule.PresentationLayer.supplierCLI;

import javax.swing.text.StyledEditorKit;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Boolean shouldLoad = shouldLoadFromDatabase(scanner);

        InventoryCLI inventory = new InventoryCLI(shouldLoad); // add the boolean later when db func is finished
        supplierCLI supplier = new supplierCLI(shouldLoad);
        System.out.println("====== Welcome to ADSS Management System ======");



        while (true) {
            System.out.println("\nSelect Module to Enter:");
            System.out.println("1. Inventory Management System");
            System.out.println("2. Supplier Management System");
            System.out.println("3. Clear saved data.");
            System.out.println("0. Exit Application");
            System.out.print("Your choice: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                inventory.start();
            } else if (choice.equals("2")) {
                supplier.start();
            } else if (choice.equals("0")) {
                System.out.println("Shutting down ADSS System. Goodbye!");
                break;
            } else if (choice.equals("3")) {
                inventory.Clean();
                supplier.Clean();
                break;
            }
            else {
                System.out.println("[!] Invalid choice. Please select 1, 2, 3 or 0.");
            }
        }

        scanner.close();
    }

    public static boolean shouldLoadFromDatabase(Scanner scanner) {
        System.out.print("Note, that data must be cleaned for safe use, without previous data!\n");
        while (true) {

            System.out.print("Do you want to load data from the database? (yes/no): ");
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                return true;
            }

            if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
                return false;
            }

            System.out.println("Please enter yes or no.");
        }
    }
}

