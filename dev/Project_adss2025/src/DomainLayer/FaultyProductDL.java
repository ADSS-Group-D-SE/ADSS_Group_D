package DomainLayer;

import CrossCuttingPackage.ShelfLocation;

import java.time.LocalDateTime;

public class FaultyProductDL {

    private int reportID;

    private final String name;
    private final String catalog_number;
    private ShelfLocation location;
    private String description;
    private LocalDateTime dateOnReport;

    /**
    Constructor of a faulty product object, constructs from a product instance, and saves description and date on report.
     **/
    public FaultyProductDL(ProductDL p,int reportID,String location, String description, LocalDateTime reportTime)
    {
        this.reportID = reportID;
        this.name = p.getName();
        this.catalog_number = p.getCatalog_number();

        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }
        String normalizedLocation = location.trim().toUpperCase();
        String locationPattern = "^[A-Z]-[0-9]+$";
        if (!normalizedLocation.matches(locationPattern)) {
            throw new IllegalArgumentException("Invalid location format. Expected format: Letter-Number (e.g., A-12).");
        }
        this.location = new ShelfLocation(location);

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

    public ShelfLocation getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public int getReportID() {
        return reportID;
    }
}
