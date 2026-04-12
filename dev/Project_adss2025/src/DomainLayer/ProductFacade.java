package DomainLayer;

import ServiceLayer.ProductServices;

import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class ProductFacade {

    private HashMap<String, ProductDL> products;
    private HashMap<String,ProductDL> faultyProducts;

    private int faultyProductsIdCounter=0;

    public ProductFacade(){
        products=new HashMap<String, ProductDL>();
        faultyProducts=new HashMap<String, ProductDL>();
    }

    public int generateNextId() {
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



}
