package InventoryModule.PresentationLayer;

import SupplierModule.PresentationLayer.supplierCLI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        InventoryCLI inventory = new InventoryCLI();
        supplierCLI supplier = new supplierCLI();
        System.out.println("====== Welcome to ADSS Management System ======");

        while (true) {
            System.out.println("\nSelect Module to Enter:");
            System.out.println("1. Inventory Management System");
            System.out.println("2. Supplier Management System");
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
            } else {
                System.out.println("[!] Invalid choice. Please select 1, 2, or 0.");
            }
        }

        scanner.close();
    }
}