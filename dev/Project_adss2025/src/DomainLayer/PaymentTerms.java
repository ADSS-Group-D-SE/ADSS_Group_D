package DomainLayer;

/**
 * Represents payment terms for a supplier.
 */
public class PaymentTerms {

    private String paymentMethod;   // e.g. "Net", "Cash", "Credit", "EOM"
    private int netDays;            // e.g. 30 for "Net 30", 60 for "Net 60"

    public PaymentTerms(String paymentMethod, int netDays) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty.");
        }
        if (netDays < 0) {
            throw new IllegalArgumentException("Net days cannot be negative.");
        }
        this.paymentMethod = paymentMethod;
        this.netDays = netDays;
    }

    /**
     * Convenience constructor that parses a string like "Net 30" into method="Net", days=30.
     */
    public PaymentTerms(String termsString) {
        if (termsString == null || termsString.isEmpty()) {
            throw new IllegalArgumentException("Payment terms string cannot be null or empty.");
        }
        String[] parts = termsString.trim().split("\\s+", 2);
        this.paymentMethod = parts[0];
        if (parts.length > 1) {
            try {
                this.netDays = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                this.netDays = 0;
            }
        } else {
            this.netDays = 0;
        }
    }

    public String getPaymentMethod() { return paymentMethod; }
    public int getNetDays() { return netDays; }

    public void setPaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty.");
        }
        this.paymentMethod = paymentMethod;
    }

    public void setNetDays(int netDays) {
        if (netDays < 0) {
            throw new IllegalArgumentException("Net days cannot be negative.");
        }
        this.netDays = netDays;
    }

    @Override
    public String toString() {
        return paymentMethod + " " + netDays;
    }
}
