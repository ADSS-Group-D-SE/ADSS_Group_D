import CrossCuttingPackage.Notification;
import CrossCuttingPackage.Response;
import InventoryModule.PresentationLayer.InventoryCLI;
import InventoryModule.ServiceLayer.ProductServices;
import SupplierModule.PresentationLayer.supplierCLI;
import SupplierModule.ServiceLayer.OrderServices;
import SupplierModule.ServiceLayer.SupplierServices;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        Boolean shouldLoad = shouldLoadFromDatabase(scanner);

        InventoryCLI inventory = new InventoryCLI(shouldLoad,scanner); // add the boolean later when db func is finished
        supplierCLI supplier = new supplierCLI(shouldLoad,scanner);
        System.out.println("====== Welcome to ADSS Management System ======");

        Thread automaticOrdersThread = new Thread(Main::HandleAutomaticOrders);

        automaticOrdersThread.start();


        while (true) {
            System.out.println("\nSelect Module to Enter:");
            System.out.println("1. Inventory Management System");
            System.out.println("2. Supplier Management System");
            System.out.println("3. Clear saved data.");
            System.out.println("4. Load Initial Test Data");
            System.out.println("0. Exit Application");

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
                System.out.println("Shutting down, re-start system");
                break;
            }
            else if (choice.equals("4")) {
                System.out.println("Loading initial system test data...");

                inventory.Clean();
                supplier.Clean();
                inventory.CreateTestData();
                supplier.CreateSupplierTestData();
                System.out.println("System data setup is complete!");
                break;
            }
            else {
                System.out.println("[!] Invalid choice. Please select 1, 2, 3 or 0.");
            }
        }

        automaticOrdersThread.interrupt();
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

    private static void HandleAutomaticOrders() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                HandleBuyOrders();
                HandleNotifications();
            } catch (Exception e) {
                System.out.println("Error in automaticOrderThread: " + e.getMessage());
            }
            try {
                Thread.sleep(12*60 * 60 * 1000L); // 12 hour
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void HandleBuyOrders()
    {
        OrderServices os = OrderServices.getInstance();
        System.out.println("Creating orders from buy orders:");

        Response<List<String>> res= os.CreateOrdersFromBuyOrders();
        if(res.isError())
            throw new RuntimeException(res.getErrorMsg());
        if(!res.getReturnValue().isEmpty()) {
            System.out.println("Orders Created:");
            for (String id : res.getReturnValue())
                System.out.println(id);
        }
        else
            System.out.println("No orders were created from buy orders.");

    }

    private static void HandleNotifications()
    {
        SupplierServices supServices = SupplierServices.getInstance();
        ProductServices productServices = ProductServices.getInstance();
        OrderServices orderServices = OrderServices.getInstance();

        System.out.println("Handling notifications:");

        Response<List<Notification>> res = productServices.getLowStockAlerts();
        if(res.isError())
            throw new RuntimeException(res.getErrorMsg());

        List<Notification> notifications = res.getReturnValue();

        if (notifications.isEmpty())
            System.out.println("No notification for shortage were received.");

        for(Notification n:notifications)
        {
            Response<String> supRes = supServices.FindBestSupplier(n);
            if(supRes.isError()) {
                System.out.println("Cannot find a supplier that sells:" + n.getCatalog_number());
                continue;
            }

            String supId = supRes.getReturnValue();
            HashMap<String,Integer> toOrder = new HashMap<>();
            toOrder.put(n.getCatalog_number(),n.HowManyToRestock());

            Response<String> oIdRes = orderServices.CreateOrder(supId,true,toOrder);
            if(oIdRes.isError())
                throw new RuntimeException(oIdRes.getErrorMsg());

            System.out.println("Order:" + oIdRes.getReturnValue() + " Was created to supplier:"+supId);
            Response<String> r = orderServices.PrepareOrder(oIdRes.getReturnValue());
            if(r.isError())
                throw new RuntimeException(r.getErrorMsg());
            r = orderServices.SendOrder(oIdRes.getReturnValue());
            if(r.isError())
                throw new RuntimeException(r.getErrorMsg());
            Response<HashMap<String,Integer>> re = orderServices.DeliverOrder(oIdRes.getReturnValue()); // auto delivery for inventory updates
            if(re.isError())
                throw new RuntimeException(re.getErrorMsg());
            r = productServices.ReciveOrder(re.getReturnValue());
            if(r.isError())
                throw new RuntimeException(r.getErrorMsg());
        }
    }
}

