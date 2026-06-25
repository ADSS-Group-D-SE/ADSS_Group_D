package SupplierModule.DomainLayer;

/**
 * Represents payment terms for a supplier.
 */
public class PaymentTerms {

    private String paymentMethod;

    public PaymentTerms(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty.");
        }

        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() { return paymentMethod; }

    public void setPaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty.");
        }
        this.paymentMethod = paymentMethod;
    }
    @Override
    public String toString() {return paymentMethod;}
}
