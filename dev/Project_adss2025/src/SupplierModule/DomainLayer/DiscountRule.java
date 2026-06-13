package SupplierModule.DomainLayer;

public class DiscountRule {

    private final String ruleName;
    private int minQuantity; // Minimum quantity to trigger this discount tier
    private double discountPercent; // Discount percentage for this tier

    public DiscountRule(int minQuantity, double discountPercent,String name) {
        if (minQuantity <= 0) {
            throw new IllegalArgumentException("Minimum quantity must be positive.");
        }
        if (discountPercent < 0 || discountPercent > 1) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 1. 0.5 = 50% for example.");
        }
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Rule name cannot be null or empty.");
        this.ruleName = name;
        this.minQuantity = minQuantity;
        this.discountPercent = discountPercent;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public String getRuleName() {return ruleName;}

    public void setMinQuantity(int minQuantity) {
        if (minQuantity <= 0) {
            throw new IllegalArgumentException("Minimum quantity must be positive.");
        }
        this.minQuantity = minQuantity;
    }

    public void setDiscountPercent(double discountPercent) {
        if (discountPercent < 0 || discountPercent > 1) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 1.");
        }
        this.discountPercent = discountPercent;
    }

    public boolean IsApplicable(int q) { return q>= this.minQuantity;}

    @Override
    public String toString() {
        return "Discount Rule " + this.getRuleName() +" : {" +
                "minQuantity=" + minQuantity +
                ", discountPercent=" + discountPercent*100 + "%" +
                '}';
    }


}
