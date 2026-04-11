package DomainLayer;

import java.time.LocalDateTime;
import java.util.HashMap;
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
