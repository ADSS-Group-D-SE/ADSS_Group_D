package InventoryModule.DomainLayer;


import CrossCuttingPackage.*;
import InventoryModule.DataLayer.FaultyReportDAO;
import InventoryModule.DataLayer.ProductDAO;
import SupplierModule.DataAccessLayer.SupplierDAO;
import SupplierModule.DomainLayer.Supplier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductFacade {
    private HashMap<String, ProductDL> products;
//    private HashMap<Integer,FaultyProductDL> faultyProducts;

    private int faultyProductsIdCounter=0;
    private int promotionIdCounter = 1;


    private static final ProductDAO productDAO = new ProductDAO();
    private static final FaultyReportDAO faultyReportDAO = new FaultyReportDAO();



    public ProductFacade(){
        products=new HashMap<String, ProductDL>();

    }

    private int generateNextId() {
        return faultyProductsIdCounter++;
    }

    private int generatePromotionId() {
        return promotionIdCounter++;
    }

    public List<ProductDL> getAllProducts() {
        List<ProductDL> results = new ArrayList<>();
        for (ProductDL p : products.values()) {
            results.add(p);
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
    public String addProduct(String name, String catalogNumber, String main_id, String sub_id, String subsub_id,
                             String warehouseName, String location, String manu, int amountOnShelves, int amountOnStock,
                             double consumerPrice, double supplyPrice, int minAmount) throws Exception {

        if(this.products.get(catalogNumber) != null){
            throw new Exception("Product already exists in the system with catalog number: " + catalogNumber);
        }

        ProductDL product = new ProductDL(name, catalogNumber, main_id, sub_id, subsub_id, warehouseName, location, manu, amountOnShelves, amountOnStock, consumerPrice, supplyPrice, minAmount);
        productDAO.Insert(product.toDTO());


        products.put(catalogNumber, product);
        return product.getCatalog_number();
    }


    /**
     * Sets a supplier discount for a product identified by its catalog number.
     * @param catalogNumber unique identifier of the product
     * @param discount discount the discount value to be applied to the product
     * @throws Exception Exception if the product does not exist in the system or if an error occurs while setting the discount
     */
    public void setSupplierDiscount(String catalogNumber, double discount) throws Exception{
        ProductDL product = FindProductByID(catalogNumber);

        product.setSupplier_discount(discount);
        try {
            productDAO.UpdateProduct(product.toDTO());
        } catch (Exception e) {
            throw new Exception("Failed to update supplier discount in database: " + e.getMessage());
        }
    }

    /**
    Method that calculates and returns a products final price based on its discounts.
     **/
    public double GetProductPrice(String catalog_number) {
        ProductDL p = FindProductByID(catalog_number);

        p.removeExpiredPromotions();


        double finalPrice = p.getPrice_to_consumer();

        finalPrice = finalPrice * (1 - p.getTotalProductDiscount());

        Double cat_discount = CategoryFacade.GetCategoryDiscount(p.getMain_category_id());
        if (cat_discount != null) {
            finalPrice = finalPrice * (1 - cat_discount);
        }

        return finalPrice;
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
        FaultyProductDL p = faultyReportDAO.findbyid(report_id);
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
        faultyReportDAO.Insert(toAdd.toDTO());
        return toAdd.getReportID();
    }

    /**
    Method that removes a report from the map, locates it by its ID.
    If the report wasnt found, throws.
     **/
    public void RemoveFaultyReport(int report_id)
    {
        FaultyProductDL p = FindProductByReportId(report_id);
        this.faultyReportDAO.deleteby(p.toDTO());
    }

    /**
    Method that iterates on the faulty product map, and adds each entry that fit the entered date range.
    construct a string reports and returns it.
     **/
    public Report CreateFaultyReport(String startdate,String enddate)
    {
        DateTimeFormatter uiFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate datestart = LocalDate.parse(startdate, uiFormatter);
        LocalDate dateend = LocalDate.parse(enddate, uiFormatter);

        Report report = new Report("Fault product reports from " + datestart + " to " + dateend +
                "\n===========================================");

        List<FaultyProductDL> faultyProductsInRange = faultyReportDAO.SelectByDateRange(datestart, dateend);

        for (FaultyProductDL p : faultyProductsInRange)
        {
            String reportDateStr = p.getDateOnReport().toString();

            report.AddLine("Report id: " + p.getReportID() +
                    "\nOn product: " + p.getName() +
                    ", Catalog number: " + p.getCatalog_number() +
                    ", Location: " + p.getLocation() +
                    "\nReported on: " + reportDateStr +
                    "\nDescription:\n" + p.getDescription() +
                    "\n-------------------------------------------");
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
        productDAO.UpdateProduct(p.toDTO());
    }


    public String update(String catalogNumber, String name, String storageWarehouse, String storageLocation,
                         Double consumerPrice, Double supplyPrice,
                         Integer shelvesAmount, Integer stockAmount, Integer minAmountAlert) {

        ProductDL product = FindProductByID(catalogNumber);
        if (name != null) product.setName(name);

        if (storageWarehouse != null) product.setWarehouse(storageWarehouse);

        if (storageLocation != null) product.setLocation(storageLocation);

        if (consumerPrice != null) product.setPrice_to_consumer(consumerPrice);
        if (supplyPrice != null) product.setPrice_to_supply(supplyPrice);
        if (shelvesAmount != null) product.setAmount_on_shelves(shelvesAmount);
        if (stockAmount != null) product.setAmount_on_stock(stockAmount);
        if (minAmountAlert != null) product.setMinAmountAlert(minAmountAlert);
        productDAO.UpdateProduct(product.toDTO());

        return "Product updated successfully!";
    }

    /**
     * A method that iterates over all products to check for products that are in warning range.
     * Collects each one and creates a list of products that in warning range.
     */

    public List<Notification> GetProductsWarnings() {
        List<Notification> warningProducts = new ArrayList<>();

        for(Map.Entry<String, ProductDL> en: this.products.entrySet())
        {
            ProductDL p = en.getValue();
            if(p.isInWarningRange())
                warningProducts.add(new Notification(p.getName(), p.getCatalog_number(), p.getLocation(), p.getMinAmountAlert(), p.getAmount_on_stock(), p.getAmount_on_shelves()));
        }
        return warningProducts;
    }
    /**
     * A method that creates an inventory report on the sent categories IDs.
     */
    public Report GetInventoryReportByCategory(List<String> cats)
    {
        Report report = new Report("Inventory report on categories:"+cats.toString());
        for(String category:cats){
            report.AddLine("\n=================\n");
            List<ProductDL> list = this.GetProductsByCategory(category);
            report.AddLine("Category " + category+":\n-----------------");
            if(list.isEmpty())
                report.AddLine("No Products\n");
            else {
                for(ProductDL p:list)
                {
                    report.AddLine("Product:" + p.getName() + " ,Catalog number:" + p.getCatalog_number() + " ,Warehouse: " + p.getWarehouse() + " ,Location:" + p.getLocation() + " ,Amount on shelves:" + p.getAmount_on_shelves() + " ,Amount on stock:" + p.getAmount_on_stock());                }
                report.AddLine(""); //Adds '\n'
            }

        }
        return report;
    }

    /*
    Helper method that returns every product that is linked to the input category in a list.
     */
    private List<ProductDL> GetProductsByCategory(String category_id)
    {
        List<ProductDL> list = new ArrayList<>();
        for(Map.Entry<String,ProductDL> en:this.products.entrySet())
        {
            ProductDL p = en.getValue();
            if(p.getMain_category_id().equals(category_id) || p.getSub_category_id().equals(category_id) || p.getSubsub_category_id().equals(category_id))
                list.add(p);
        }
        return list;
    }

    /**
     * Method that finds a product in data and sets its discount precentage.
     * @param id
     * @param newDisc
     */
    public void SetProductDiscountMod(String id,double newDisc,String time)
    {
        ProductDL p =FindProductByID(id);
        p.addPromotion(new Promotion(Integer.toString(generatePromotionId()),newDisc,time, PromotionScope.PRODUCT));
        productDAO.UpdateProduct(p.toDTO());
    }

    /**
     * Method that finds a product, and returns its price to supply, with supplier discount.
     * @param id
     * @return
     */
    public double GetFinalPriceToSupply(String id)
    {
        ProductDL p =FindProductByID(id);
        return p.getPrice_to_supplyDiscounted();
    }

    /**
    Throws if one was not find. -Use in UI to verify item list before creating agreement.
     */
    public void VerifyItems(List<String> items)
    {
        for(String cat:items)
            FindProductByID(cat);
    }


    public void CleanData() {
        productDAO.Clean();
        faultyReportDAO.Clean();
    }


    public void LoadData() {
        List<productDTO> productDTOs = productDAO.SelectAll();

        for (productDTO dto : productDTOs) {

            ProductDL productDomain = new ProductDL(dto);
            this.products.put(productDomain.getCatalog_number(), productDomain);
        }

        System.out.println("[V] Database data loaded into Facade memory successfully!");
    }
}
