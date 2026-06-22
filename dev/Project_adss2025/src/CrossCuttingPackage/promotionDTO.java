package CrossCuttingPackage;

public class promotionDTO {
    private final String id;
    private final double discountPercentage;
    private final String endDate;
    private final String scope;

    public promotionDTO(String id, double discountPercentage, String endDate, String scope) {
        this.id = id;
        this.discountPercentage = discountPercentage;
        this.endDate = endDate;
        this.scope = scope;
    }

    public String getId() { return id; }
    public double getDiscountPercentage() { return discountPercentage; }
    public String getEndDate() { return endDate; }
    public String getScope() { return scope; }
}
