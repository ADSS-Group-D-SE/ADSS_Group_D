package ServiceLayer;

import DomainLayer.ProductFacade;
import java.util.List;
import java.time.LocalDateTime;


/**
 * This class implements the Singleton design pattern to ensure only one instance
 * of the service exists.
 */

public class ProductServices {


    private static ProductServices INSTANCE;
    private final ProductFacade pFacade;

    /**
     *Initializes the connection to the Domain Layer with the ProductFacade.
     */
    private ProductServices(){
        this.pFacade=new ProductFacade();
    }


    /**
     * Provides a global point of access to the ProductServices instance.
     * @return the single instance of ProductServices
     */
    public static ProductServices getInstance(){
        if(INSTANCE==null){
            INSTANCE= new ProductServices();
        }
        return INSTANCE;
    }


    /**
     * Adds a new product to the system with the provided details.
     * @param name name of the product
     * @param catalogNumber unique identifier of the product
     * @param categoriesNames a list of category names the product belongs to
     * @param location location of the product
     * @param amountOnShelves the quantity of the product available on shelves
     * @param amountOnStock the quantity of the product available in stock
     * @param supplyPrice the supplier price of the product
     * @param consumerPrice the consumer price of the product
     * @param minAmount the minimum required quantity of the product
     * @return A Response indicating success or an error message
     */
    public Response<String> addProduct(String name, String catalogNumber, List<String> categoriesNames,
                                        String location, int amountOnShelves, int amountOnStock,
                                        double supplyPrice, double consumerPrice, int minAmount) {
        try {
            pFacade.addProduct(name, catalogNumber, categoriesNames, location,
                    amountOnShelves, amountOnStock, supplyPrice, consumerPrice, minAmount);
            return new Response<>(null,null);
        } catch (Exception e) {
            return new Response<>(e.getMessage());
        }
    }

    /**
     * Sets a new minimum amount for a specific product.
     * @param catalogNumber unique identifier of the product
     * @param amount the minimum required quantity of the product
     * @return A Response indicating success or an error message
     */
    public Response<String> setMinAmount(String catalogNumber, int amount){
        try {
            pFacade.setMinAmount(catalogNumber,amount);
            return new Response<>(null,null);
        } catch (Exception e) {
            return new Response<>(e.getMessage());
        }
    }


    /**
     * Updates the price of a specific product.
     * @param catalogNumber unique identifier of the product
     * @param price the consumer price of the product
     * @return A Response indicating success or an error message
     */
    public Response<String> setPrice(String catalogNumber, int price){
        try {
            pFacade.setPrice(catalogNumber,price);
            return new Response<>(null,null);
        } catch (Exception e) {
            return new Response<>(e.getMessage());
        }
    }

    /**
     * Updates the supplier discount for a specific product.
     * @param catalogNumber unique identifier of the product
     * @param SupplierDiscount discount the discount value to be applied to the product
     * @return A Response indicating success or an error message
     */
    public Response<String> setSupplierDiscount(String catalogNumber, int SupplierDiscount){
        try {
            pFacade.setSupplierDiscount(catalogNumber,SupplierDiscount);
            return new Response<>(null,null);
        } catch (Exception e) {
            return new Response<>(e.getMessage());
        }
    }



    /**
    Service method for the product reporting service.
    Returns response: with report ID if operation was successful
                      else, return response with error msg.
     **/
    public Response<Integer> ReportFaultyProduct(String catalog_number, String description, LocalDateTime dateOnReport)
    {
        Response<Integer> res = null;
        try
        {
            res = new Response<>(null,this.pFacade.ReportFaultyProduct(catalog_number,description,dateOnReport));
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /**
    Service that allows removing a faulty product report by its id.
    Returns: Response with null value if operation was successful.
             else: returns a response with error msg.
     **/
    public Response<String> RemoveFaultyReport(int report_id)
    {
        Response<String> res = null;
        try
        {
            this.pFacade.RemoveFaultyReport(report_id);
            res = new Response<>(null,null);
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /**
    Service for the faulty products report service.
    returns response: with the product report if operation was successful.
                      else, returns error msg.

     **/
    public Response<String> CreateFaultyProductReport(LocalDateTime start,LocalDateTime end)
    {
        Response<String> res = null;
        try
        {
            res = new Response<>(null,this.pFacade.CreateFaultyReport(start,end));
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /**
    Service for the purchase method ,mainly for testing purposes.
    Returns response: with null value if operation was successful.
    else : with error msg.
     **/
    public Response<String> PurchaseProduct(String catalog_number,int shelves,int stock)
    {
        Response<String> res = null;
        try
        {
            this.pFacade.PurchaseProduct(catalog_number,shelves,stock);
            res = new Response<>(null,null);
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

}
