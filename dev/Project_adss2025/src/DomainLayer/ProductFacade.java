package DomainLayer;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class ProductFacade {
    private HashMap<String, ProductDL> products;
    private HashMap<Integer,FaultyProductDL> faultyProducts;

    private int faultyProductsIdCounter=0;

    public ProductFacade(){
        products=new HashMap<String, ProductDL>();
        faultyProducts=new HashMap<Integer, FaultyProductDL>();
    }

    private int generateNextId() {
        return faultyProductsIdCounter++;
    }


    /**
     *
     * @param name name of the product
     * @param catalogNumber unique identifier of the product
     * @param categoriesNames a list of category names the product belongs to
     * @param location location of the product
     * @param amountOnShelves the quantity of the product available on shelves
     * @param amountOnStock the quantity of the product available in stock
     * @param supplyPrice the supplier price of the product
     * @param consumerPrice the consumer price of the product
     * @param minAmount the minimum required quantity of the product
     * @return the newly created object
     * @throws Exception Exception if the product does  exist in the system or if an error occurs while creating the product
     */
    public ProductDL addProduct(String name, String catalogNumber, List<String> categoriesNames,
                                String location, int amountOnShelves, int amountOnStock,
                                double supplyPrice, double consumerPrice, int minAmount) throws Exception {


        if(products.get(catalogNumber)!=null){
            throw new Exception("Product already exists in the system with catalog number: " + catalogNumber);
        }
        ProductDL product=null;
        try {

//            product = new ProductDL(name, catalogNumber, categoriesNames, location,
//                    amountOnShelves, amountOnStock, supplyPrice, consumerPrice, minAmount);

        } catch (Exception e) {
            throw e;
        }

        products.put(catalogNumber,product);
        return product;
    }

    /**
     * Sets a Min Amount for a product identified by its catalog number.
     * @param catalogNumber unique identifier of the product
     * @param amount the minimum required quantity of the product
     * @throws Exception Exception if the product does not exist in the system or if an error occurs while setting the min amount
     */
    public void setMinAmount(String catalogNumber, int amount) throws Exception{
        ProductDL product = products.get(catalogNumber);

        if(product==null){
            throw new Exception("Product already exists in the system with catalog number: " + catalogNumber);
        }
        try{
            //לבדוק שנבדק בתוך הPRODUCTDL שהכמות חיובית
//            product.setMinAmount(amount);
        }
        catch (Exception e) {
            throw e;
        }

    }

    /**
     * Sets a Price for a product identified by its catalog number.
     * @param catalogNumber unique identifier of the product
     * @param price the consumer price of the product
     * @throws Exception Exception if the product does not exist in the system or if an error occurs while setting the price
     */
    public void setPrice(String catalogNumber, double price) throws Exception{
        ProductDL product = products.get(catalogNumber);

        if(product==null){
            throw new Exception("Product already exists in the system with catalog number: " + catalogNumber);
        }
        try{
            //לבדוק שנבדק בתוך הPRODUCTDL שהמחיר הגיוני
//            product.setPrice(price);
        }
        catch (Exception e) {
            throw e;
        }

    }

    /**
     * Sets a supplier discount for a product identified by its catalog number.
     * @param catalogNumber unique identifier of the product
     * @param discount discount the discount value to be applied to the product
     * @throws Exception Exception if the product does not exist in the system or if an error occurs while setting the discount
     */
    public void setSupplierDiscount(String catalogNumber, int discount) throws Exception{
        ProductDL product = products.get(catalogNumber);

        if(product==null){
            throw new Exception("Product already exists in the system with catalog number: " + catalogNumber);
        }
        try{
            //לבדוק שנבדק בתוך הPRODUCTDL שהכמות חיובית
//            product.setSupplierDiscount(discount);
        }
        catch (Exception e) {
            throw e;
        }

    }



    

    /**
    A method that locates a product by its catalog number and returns it.
    throws exception if product was not located.
     **/
    public ProductDL FindProductByID(String catalog_number)
    {
        ProductDL p = this.products.get(catalog_number);
        if(p == null)
            throw new NoSuchElementException("Product:" +catalog_number +" ,No such product was found in facade");
        return p;
    }

    /**
    A method that locates a faulty report entry in map, by its report id.
    if wasnt found, throws an exception.
     **/
    public FaultyProductDL FindProductByReportId(int report_id)
    {
        FaultyProductDL p = this.faultyProducts.get(report_id);
        if(p == null)
            throw new NoSuchElementException("Report:"+ report_id +" ,No such report was found in facade");
        return p;
    }

    /**
    Method that creates a faulty product record on the faulty product map.
    Looks for the product by catalog, throws exception if wasnt found.
    returns report id to the client
     **/
    public Integer ReportFaultyProduct(String catalog_number, String description, LocalDateTime dateOnReport)
    {
        ProductDL toFaulty = FindProductByID(catalog_number);
        FaultyProductDL toAdd = new FaultyProductDL(toFaulty,generateNextId(),description,dateOnReport);

        this.faultyProducts.put(toAdd.getReportID(), toAdd);
        return toAdd.getReportID();
    }

    /**
    Method that removes a report from the map, locates it by its ID.
    If the report wasnt found, throws.
     **/
    public void RemoveFaultyReport(int report_id)
    {
        FaultyProductDL p = FindProductByReportId(report_id);
        this.faultyProducts.remove(p.getReportID());
    }

    /**
    Method that iterates on the faulty product map, and adds each entry that fit the entered date range.
    construct a string reports and returns it.
     **/
    public String CreateFaultyReport(LocalDateTime start,LocalDateTime end)
    {
        String report = "Fault product reports from " + start.toString() +" to " + end.toString() +"\n===========================================\n";
        for(Map.Entry<Integer,FaultyProductDL> en: this.faultyProducts.entrySet())
        {
            FaultyProductDL p = en.getValue();
            if(!p.getDateOnReport().isAfter(end) && !p.getDateOnReport().isBefore(start)) // if the report date fits: start <+ report date <= end
            {
                report += "Report id:" + p.getReportID() +"\nOn product:" +p.getName() + ", Catalog number:" + p.getCatalog_number() + " ,Location:" +p.getLocation() +"\n" +
                        "Reported on:" + p.getDateOnReport().toString() +"\nDescription:\n" + p.getDescription() +"\n\n";
            }
        }
        return report;
    }

    /**
     Method that finds a product by its catalog number, and conducts a purchase call to it.
     ALLOWS negative number, for amount increase.
     **/
    public void PurchaseProduct(String catalog_number,int shelves,int stock)
    {
        ProductDL p = FindProductByID(catalog_number);
        p.Purchase(shelves,stock);
    }

}
