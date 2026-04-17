package DomainLayer;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductFacade {
    private HashMap<String, ProductDL> products;
    private HashMap<Integer,FaultyProductDL> faultyProducts;
    private HashMap<String,Double> categoryDiscounts;
    private HashMap<String,List<ProductDL>> mapByCategory;

    private int faultyProductsIdCounter=0;

    public ProductFacade(){
        products=new HashMap<String, ProductDL>();
        faultyProducts=new HashMap<Integer, FaultyProductDL>();
        categoryDiscounts = new HashMap<>();
        mapByCategory = new HashMap<>();
    }

    private int generateNextId() {
        return faultyProductsIdCounter++;
    }




    public List<ProductDL> getAllProducts() {
        List<ProductDL> results = new ArrayList<>();
        for (ProductDL p : products.values()) {
            results.add(new ProductDL(p));
        }
        return results;
    }







    /**
     *
     * @param name name of the product
     * @param catalogNumber unique identifier of the product
     * @param main_id id of the main category
     * @param location location of the product
     * @param amountOnShelves the quantity of the product available on shelves
     * @param amountOnStock the quantity of the product available in stock
     * @param supplyPrice the supplier price of the product
     * @param consumerPrice the consumer price of the product
     * @param minAmount the minimum required quantity of the product
     * @return the newly created object
     * @throws Exception Exception if the product does  exist in the system or if an error occurs while creating the product
     */
    public ProductDL addProduct(String name, String catalogNumber, String main_id,String sub_id,String subsub_id,
                                String location,String manu, int amountOnShelves, int amountOnStock,
                                double supplyPrice, double consumerPrice, int minAmount) throws Exception {


        if(this.products.get(catalogNumber)!=null){
            throw new Exception("Product already exists in the system with catalog number: " + catalogNumber);
        }
        ProductDL product=new ProductDL(name, catalogNumber, main_id,sub_id,subsub_id, location,manu,amountOnShelves, amountOnStock, supplyPrice, consumerPrice, minAmount);


        products.put(catalogNumber,product);
        mapByCategory.putIfAbsent(main_id,new ArrayList<>()); // create a new list for the category if its new to the data.
        mapByCategory.get(main_id).add(product); // saves in the category map as well.
        return new ProductDL(product);
    }

//    /**
//     * Sets a Min Amount for a product identified by its catalog number.
//     * @param catalogNumber unique identifier of the product
//     * @param amount the minimum required quantity of the product
//     * @throws Exception Exception if the product does not exist in the system or if an error occurs while setting the min amount
//     */
//    public void setMinAmount(String catalogNumber, int amount) throws Exception{
//        ProductDL product = FindProductByID(catalogNumber);
//        try{
//            //לבדוק שנבדק בתוך הPRODUCTDL שהכמות חיובית
////            product.setMinAmount(amount);
//        }
//        catch (Exception e) {
//            throw e;
//        }
//
//    }

//    /**
//     * Sets a Price for a product identified by its catalog number.
//     * @param catalogNumber unique identifier of the product
//     * @param price the consumer price of the product
//     * @throws Exception Exception if the product does not exist in the system or if an error occurs while setting the price
//     */
//    public void setPrice(String catalogNumber, double price) throws Exception{
//        ProductDL product = FindProductByID(catalogNumber);
//        try{
//            //לבדוק שנבדק בתוך הPRODUCTDL שהמחיר הגיוני
////            product.setPrice(price);
//        }
//        catch (Exception e) {
//            throw e;
//        }
//
//    }

    /**
     * Sets a supplier discount for a product identified by its catalog number.
     * @param catalogNumber unique identifier of the product
     * @param discount discount the discount value to be applied to the product
     * @throws Exception Exception if the product does not exist in the system or if an error occurs while setting the discount
     */
    public void setSupplierDiscount(String catalogNumber, int discount) throws Exception{
        ProductDL product = FindProductByID(catalogNumber);
        try{
           product.setSupplier_discount(discount);
        }
        catch (Exception e) {
            throw e;
        }

    }

    /**
    Method that calculates and returns a products final price based on its discounts.
     **/
    public double GetProductPrice(String catalog_number)
    {
        ProductDL p = FindProductByID(catalog_number);
        double res = p.getPrice_to_consumer()*(1-p.getProduct_discount()); //initial discount

        Double cat_discount = this.categoryDiscounts.get(p.getMain_category_id());
        if(cat_discount !=null)
            res = res *(1-cat_discount);

        return res;
    }

    /**
    Method that allows setting category discount, saves data on the category discount map.
     **/
    public void SetCatDiscount(String cat_id,double discount)
    {
        if(discount < 0 || discount > 1)
            throw new RuntimeException("ProductFacade - SetCatDiscounts: Invalid discount was sent:"+discount);

        this.categoryDiscounts.put(cat_id,discount); // saves discounts in map.
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
    public Integer ReportFaultyProduct(String catalog_number,String locationProduct, String description)
    {
        ProductDL toFaulty = FindProductByID(catalog_number);
        LocalDateTime dateOnReport = LocalDateTime.now();
        FaultyProductDL toAdd = new FaultyProductDL(toFaulty,generateNextId(),locationProduct,description,dateOnReport);

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
    public String CreateFaultyReport(String startdate,String enddate)
    {
        LocalDate datestart = LocalDate.parse(startdate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDateTime start = datestart.atStartOfDay();

        LocalDate dateend = LocalDate.parse(enddate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDateTime end = dateend.atTime(23, 59, 59);

        String report = "Fault product reports from " + start +" to " + end +"\n===========================================\n";
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


    public String update(String catalogNumber, String name, String storageLocation,
                       Double consumerPrice, Double supplyPrice,
                       Integer shelvesAmount, Integer stockAmount, Integer minAmountAlert) {

        ProductDL product = FindProductByID(catalogNumber);

        if (name != null) product.setName(name);
        if (storageLocation != null) product.setLocation(storageLocation);
        if (consumerPrice != null) product.setPrice_to_consumer(consumerPrice);
        if (supplyPrice != null) product.setPrice_to_supply(supplyPrice);
        if (shelvesAmount != null) product.setAmount_on_shelves(shelvesAmount);
        if (stockAmount != null) product.setAmount_on_stock(stockAmount);
        if (minAmountAlert != null) product.setMinAmountAlert(minAmountAlert);

        return "Product updated successfully!";
    }

    /**
     * A method that iterates over all products to check for products that are in warning range.
     * Collects each one and creates a notification report.
     */
//    public String GetProductsWarnings()
//    {
//        String report = "Products in warning range:\n";
//        for(Map.Entry<String,ProductDL> en:this.products.entrySet())
//        {
//            ProductDL p = en.getValue();
//            if(p.isInWarningRange())
//                report+="================\n" + p.getName() +" Catalog number:" + p.getCatalog_number() +" Total of:" +(p.getAmount_on_shelves()+p.getAmount_on_stock())+"\n";
//        }
//        return report;
//    }

    public List<ProductDL> GetProductsWarnings() {
        List<ProductDL> warningProducts = new ArrayList<>();

        for(Map.Entry<String,ProductDL> en:this.products.entrySet())
        {
            ProductDL p = en.getValue();
            if(p.isInWarningRange())
                warningProducts.add(new ProductDL(p));

        }
        return warningProducts;
    }

    /**
     * A method that creates an inventory report on the sent categories IDs.
     * ATTENTION:if mapByCategory returns a null list, id does not mean the category not exist, it may imply that it does not have products yet.
     */
    public String GetInventoryReportByCategory(List<String> cats)
    {
        String report="Inventory report on categories:"+cats.toString();
        for(String category:cats){
            report+="=================\n\n";
            List<ProductDL> list = this.mapByCategory.get(category);
            report+="Category " + category+":\n-----------------\n";
            if(list == null || list.isEmpty())
                report+="No Products\n\n";
            else {
                for(ProductDL p:list)
                {
                    report+="Product:" + p.getName() +" Catalog number:"+p.getCatalog_number() +" Location:"+p.getLocation()+" Amount on shelves:"+p.getAmount_on_shelves()+" Amount on stock:"+p.getAmount_on_stock()+"\n";
                }
                report+="\n\n";
            }

        }
        return report;
    }
}
