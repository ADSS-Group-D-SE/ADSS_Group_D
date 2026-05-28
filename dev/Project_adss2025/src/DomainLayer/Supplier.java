package DomainLayer;

import java.util.ArrayList;
import java.util.List;

public class Supplier {

    private int supplierId; // Unique identifier for the supplier
    private String companyId; // Company Registration Number (ח"פ)
    private String name; // Supplier name
    private String bankAccount; // Bank account details
    private PaymentTerms paymentTerms; // Payment terms (e.g. "Net 30", "Net 60+")
    private List<ContactPerson> contactPersons; // Contact persons for this supplier
    private SupplierAgreement agreement; // The agreement with this supplier

    public Supplier(int supplierId, String companyId, String name,
            String bankAccount, PaymentTerms paymentTerms) {
        if (companyId == null || companyId.isEmpty()) {
            throw new IllegalArgumentException("Company ID cannot be null or empty.");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be null or empty.");
        }
        this.supplierId = supplierId;
        this.companyId = companyId;
        this.name = name;
        this.bankAccount = bankAccount;
        this.paymentTerms = paymentTerms;
        this.contactPersons = new ArrayList<>();
        this.agreement = null;
    }

    public void addContactPerson(ContactPerson contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact person cannot be null.");
        }
        contactPersons.add(contact);
    }

    public void removeContactPerson(ContactPerson contact) {
        contactPersons.remove(contact);
    }

    public ContactPerson findContactByName(String name) {
        for (ContactPerson cp : contactPersons) {
            if (cp.getName().equalsIgnoreCase(name)) {
                return cp;
            }
        }
        return null;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public PaymentTerms getPaymentTerms() {
        return paymentTerms;
    }

    public List<ContactPerson> getContactPersons() {
        return new ArrayList<>(contactPersons);
    }

    public SupplierAgreement getAgreement() {
        return agreement;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setCompanyId(String companyId) {
        if (companyId == null || companyId.isEmpty()) {
            throw new IllegalArgumentException("Company ID cannot be null or empty.");
        }
        this.companyId = companyId;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be null or empty.");
        }
        this.name = name;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setPaymentTerms(PaymentTerms paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public void setAgreement(SupplierAgreement agreement) {
        this.agreement = agreement;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId=" + supplierId +
                ", companyId='" + companyId + '\'' +
                ", name='" + name + '\'' +
                ", paymentTerms=" + (paymentTerms != null ? paymentTerms.toString() : "null") +
                ", contacts=" + contactPersons.size() +
                ", hasAgreement=" + (agreement != null) +
                '}';
    }
}
