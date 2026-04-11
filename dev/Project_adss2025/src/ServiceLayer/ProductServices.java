package ServiceLayer;

import DomainLayer.ProductFacade;

import java.time.LocalDateTime;


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
