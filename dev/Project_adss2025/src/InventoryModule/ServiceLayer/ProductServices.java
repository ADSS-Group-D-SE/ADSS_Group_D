package InventoryModule.ServiceLayer;

import CrossCuttingPackage.Notification;
import CrossCuttingPackage.Report;
import CrossCuttingPackage.Response;
import InventoryModule.DomainLayer.ProductDL;
import InventoryModule.DomainLayer.ProductFacade;

import java.util.ArrayList;
import java.util.List;


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



    public Response<String> Clean()
    {
        Response<String> res;
        try {
            pFacade.CleanData();
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public Response<List<ProductSL>> getAllProducts() {
        try {
            List<ProductDL> dlProducts = pFacade.getAllProducts();
            List<ProductSL> slProducts = new ArrayList<>();

            for (ProductDL dl : dlProducts) {
                slProducts.add(new ProductSL(dl));
            }

            return new Response<>(null, slProducts);

        } catch (Exception e) {
            return new Response<>(e.getMessage());
        }
    }

    /**
     * Adds a new product to the system with the provided details.
     * @param name name of the product
     * @param catalogNumber unique identifier of the product
     * @param main_id id of the main category
     * @param manu name of product manufacturer.
     * @param location location of the product
     * @param amountOnShelves the quantity of the product available on shelves
     * @param amountOnStock the quantity of the product available in stock
     * @param supplyPrice the supplier price of the product
     * @param consumerPrice the consumer price of the product
     * @param minAmount the minimum required quantity of the product
     * @return A CrossCuttingPackage.Response indicating success or an error message
     */
    public Response<String> addProduct(String name, String catalogNumber, String main_id, String sub_id, String subsub_id,
                                       String warehouseName, String location, String manu, int amountOnShelves, int amountOnStock,
                                       double consumerPrice, double supplyPrice, int minAmount) {
        try {
            pFacade.addProduct(name, catalogNumber, main_id, sub_id, subsub_id, warehouseName, location, manu,
                    amountOnShelves, amountOnStock, consumerPrice, supplyPrice, minAmount);
            return new Response<>(null, null);
        } catch (Exception e) {
            return new Response<>(e.getMessage());
        }
    }

    /**
     * Updates the supplier discount for a specific product.
     * @param catalogNumber unique identifier of the product
     * @param SupplierDiscount discount the discount value to be applied to the product
     * @return A CrossCuttingPackage.Response indicating success or an error message
     */
    public Response<String> setSupplierDiscount(String catalogNumber, double SupplierDiscount){
        try {
            pFacade.setSupplierDiscount(catalogNumber,SupplierDiscount);
            return new Response<>(null,null);
        } catch (Exception e) {
            return new Response<>(e.getMessage());
        }
    }

    /**
     * The price calculation service.
     * Returns a response: With the entered product final price.
     * Else:CrossCuttingPackage.Response with an error msg.
     */
    public Response<Double> GetProductPrice(String catalog_number)
    {
        Response<Double> res = null;
        try
        {
            res = new Response<>(null,this.pFacade.GetProductPrice(catalog_number));
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /**
     * The supply price calculation service.
     * Returns a response: With the entered product final supply price.
     * Else:CrossCuttingPackage.Response with an error msg.
     */
    public Response<Double> GetProductSupplyPrice(String catalog_number)
    {
        Response<Double> res = null;
        try
        {
            res = new Response<>(null,this.pFacade.GetFinalPriceToSupply(catalog_number));
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }


    /**
    Service method for the product reporting service.
    Returns response: with report ID if operation was successful
                      else, return response with error msg.
     **/
    public Response<Integer> ReportFaultyProduct(String catalog_number, String locationProduct, String description)
    {
        Response<Integer> res = null;
        try
        {
            res = new Response<>(null,this.pFacade.ReportFaultyProduct(catalog_number,locationProduct,description));
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /**
    Service that allows removing a faulty product report by its id.
    Returns: CrossCuttingPackage.Response with null value if operation was successful.
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
    public Response<Report> CreateFaultyProductReport(String start, String end)
    {
        Response<Report> res = null;
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
     * The service that operates the inventory report by categories method.
     * Returns:A response with a Report object with inventory report if op was a success
     * Else:returns a response with a error msg.
     * @param cats
     * @return
     */
    public Response<Report> GetInventoryReport(List<String> cats)
    {
        Response<Report> res = null;
        try
        {
            res = new Response<>(null,this.pFacade.GetInventoryReportByCategory(cats));
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
    public Response<String> PurchaseProduct(String catalog_number, int shelves, int stock)
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

    /**
     * Service to update the details of the entered product.
     * Returns CrossCuttingPackage.Response with success message if op was a success, else returns a response with error string.

     * @return
     */
    public Response<String> update(String catalogNumber, String name, String storageWarehouse, String storageLocation,
                                   Double consumerPrice, Double supplyPrice,
                                   Integer shelvesAmount, Integer stockAmount, Integer minAmountAlert) {

        Response<String> res = null;
        try {
            res = new Response<>(null, this.pFacade.update(catalogNumber, name, storageWarehouse, storageLocation, consumerPrice, supplyPrice, shelvesAmount, stockAmount, minAmountAlert));
        } catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /**
     * Service that operates the product notification report
     * Returns:CrossCuttingPackage.Response with product warning report in string if was a success
     * Else:CrossCuttingPackage.Response with an error string
     */
    public Response<List<Notification>> getLowStockAlerts() {
        try {

            return new Response<List<Notification>>(null, this.pFacade.GetProductsWarnings());

        } catch (Exception e) {
            return new Response<>(e.getMessage());
        }
    }

    /**
     * Service that returns a response with productSL of entered product.
     * if op was a failure, returns a response with error string.
     * @param catalogNum
     * @return
     */
    public Response<ProductSL> getProductByCatalogNumber(String catalogNum) {
        try {
            ProductDL dlProducts = this.pFacade.FindProductByID(catalogNum);

            ProductSL slProducts =new ProductSL(dlProducts);
            return new Response<>(null, slProducts);

        } catch (Exception e) {
            return new Response<>(e.getMessage());
        }
    }

    /**
     * Service that operates the product discount method.
     * If op was a success, returns a response with null value,
     * else, returns a response with error string.
     * @param id
     * @param discount
     * @return
     */
    public Response<String> SetProductDiscount(String id, double discount, String endDateStr) {
        Response<String> res = null;
        try {
            this.pFacade.SetProductDiscountMod(id, discount, endDateStr);
            res = new Response<>(null, null);
        } catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /*
    Service to use by UI to verify the the item catalogs exist in inventory.
     */
    public Response<String> VerifyItemCatalogs(List<String> items)
    {
        Response<String> res = null;
        try
        {
            this.pFacade.VerifyItems(items);
            res = new Response<>(null,null);
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public Response<String> Load() {
        Response<String> res;
        try {
            pFacade.LoadData();
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }
}
