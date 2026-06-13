package SupplierModule.DomainLayer;

public class SupplierItem {

    private final String catalogNumber; // Supplier's catalog number for this item
    private double price; // Base price per unit
    private int amount;

    public SupplierItem(String catalogNumber,double price,int amount) {

        if(catalogNumber == null || catalogNumber.isEmpty())
            throw new IllegalArgumentException("Catalog number cant be null or empty on supplier item creation.");

        this.catalogNumber = catalogNumber;
        setPrice(price);
        setAmount(amount);
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public void setPrice(double price) {
        if(price < 0)
            throw new IllegalArgumentException("Supplier price for the item cannot be negative.");
        this.price = price;
    }

    public void setAmount(int amount) {
        if(amount <=0)
            throw new IllegalArgumentException("Cannot set non positive amount.");
        this.amount = amount;
    }


}
