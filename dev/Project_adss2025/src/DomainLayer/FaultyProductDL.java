package DomainLayer;

import java.time.LocalDateTime;

public class FaultyProductDL {

    private int reportID;

    private final String name;
    private final String catalog_number;
    private String location;
    private String description;
    private LocalDateTime dateOnReport;

    /**
    Constructor of a faulty product object, constructs from a product instance, and saves description and date on report.
     **/
    public FaultyProductDL(ProductDL p,int reportID,String description, LocalDateTime reportTime)
    {
        this.reportID = reportID;
        this.name = p.getName();
        this.catalog_number = p.getCatalog_number();
        this.location = p.getLocation(); // saves data from product.

        this.description = description;
        this.dateOnReport = reportTime;
    }

    /**
    =====================
    Getters and setters
    =====================
     **/
    public LocalDateTime getDateOnReport() {
        return dateOnReport;
    }

    public String getName() {
        return name;
    }

    public String getCatalog_number() {
        return catalog_number;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public int getReportID() {
        return reportID;
    }
}
