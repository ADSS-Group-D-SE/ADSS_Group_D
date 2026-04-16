package DomainLayer;

public class QuantityDiscount {

    private int minQuantity; // Minimum quantity to trigger this discount tier
    private double discountPercent; // Discount percentage for this tier

    public QuantityDiscount(int minQuantity, double discountPercent) {
        if (minQuantity <= 0) {
            throw new IllegalArgumentException("Minimum quantity must be positive.");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
        }
        this.minQuantity = minQuantity;
        this.discountPercent = discountPercent;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setMinQuantity(int minQuantity) {
        if (minQuantity <= 0) {
            throw new IllegalArgumentException("Minimum quantity must be positive.");
        }
        this.minQuantity = minQuantity;
    }

    public void setDiscountPercent(double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
        }
        this.discountPercent = discountPercent;
    }

    @Override
    public String toString() {
        return "QuantityDiscount{" +
                "minQuantity=" + minQuantity +
                ", discountPercent=" + discountPercent + "%" +
                '}';
    }
}
