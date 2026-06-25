package CrossCuttingPackage;

public class FaultyProductDTO {
    private final int reportID;
    private final String name;
    private final String catalogNumber;
    private final String location;
    private final String description;
    private final String dateOnReport;

    public FaultyProductDTO(int reportID, String name, String catalogNumber, String location, String description, String dateOnReport) {
        this.reportID = reportID;
        this.name = name;
        this.catalogNumber = catalogNumber;
        this.location = location;
        this.description = description;
        this.dateOnReport = dateOnReport;
    }

    public int getReportID() { return reportID; }
    public String getName() { return name; }
    public String getCatalogNumber() { return catalogNumber; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getDateOnReport() { return dateOnReport; }
}
