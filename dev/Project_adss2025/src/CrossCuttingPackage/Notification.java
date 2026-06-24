package CrossCuttingPackage;

import InventoryModule.DomainLayer.ShelfLocation;

public class Notification {

    private String pName;
    private String catalog_number;
    private ShelfLocation location;
    private int min;
    private int amountInStock;
    private int amountOnShelf;

    public Notification(String name,String cat,ShelfLocation location ,int min,int stock, int shelf)
    {
        this.pName = name;
        this.catalog_number = cat;
        this.location = location;
        this.min = min;
        this.amountInStock = stock;
        this.amountOnShelf = shelf;

    }

    public String getpName() {
        return pName;
    }

    public String getCatalog_number() {
        return catalog_number;
    }

    public int getAmountInStock() {
        return amountInStock;
    }

    public int getAmountOnShelf() {
        return amountOnShelf;
    }

    public int getMin() {
        return min;
    }

    public ShelfLocation getLocation() {
        return location;
    }

    public String toString()
    {
        return "Product name:" + pName +" ,Catalog number:" + catalog_number +" ,Location:"+location+" ,Stock:" + amountInStock +" Shelf:" + amountOnShelf
                +"\nNeed to restock:" + (min - (amountInStock+amountInStock));
    }

    public int HowManyToRestock() { return (min +1 - (amountInStock + amountOnShelf));}


}
