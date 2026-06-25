package CrossCuttingPackage;

public class SupplierItemDTO {

    public String catalogNumber;
    public double price;
    public int amount;

    public SupplierItemDTO(String catalogNumber, double price,int amount)
    {
        this.catalogNumber = catalogNumber;
        this.price = price;
        this.amount = amount;
    }
}
