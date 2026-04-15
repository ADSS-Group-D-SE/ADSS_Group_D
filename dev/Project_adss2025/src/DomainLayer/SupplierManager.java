package DomainLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierManager {

    private Map<Integer, Supplier> suppliers; // supplierId -> Supplier
    private int nextSupplierId;

    public SupplierManager() {
        this.suppliers = new HashMap<>();
        this.nextSupplierId = 1;
    }

    /**
     * Creates and registers a new supplier. Returns the created Supplier.
     */
    public Supplier addSupplier(String companyId, String name,
            String bankAccount, String paymentTerms) {
        Supplier supplier = new Supplier(nextSupplierId, companyId, name, bankAccount, paymentTerms);
        suppliers.put(nextSupplierId, supplier);
        nextSupplierId++;
        return supplier;
    }

    /**
     * Removes a supplier by its ID.
     * 
     * @return true if the supplier existed and was removed; false otherwise.
     */
    public boolean removeSupplier(int supplierId) {
        return suppliers.remove(supplierId) != null;
    }

    /**
     * Retrieves a supplier by its ID.
     * 
     * @return the Supplier, or null if not found.
     */
    public Supplier getSupplier(int supplierId) {
        return suppliers.get(supplierId);
    }

    /**
     * Returns a list of all registered suppliers.
     */
    public List<Supplier> getAllSuppliers() {
        return new ArrayList<>(suppliers.values());
    }

    /**
     * Finds a supplier by its company registration number (ח"פ).
     */
    public Supplier findByCompanyId(String companyId) {
        for (Supplier s : suppliers.values()) {
            if (s.getCompanyId().equals(companyId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Finds all suppliers that supply a given internal item ID.
     */
    public List<Supplier> findSuppliersByItem(int internalItemId) {
        List<Supplier> result = new ArrayList<>();
        for (Supplier s : suppliers.values()) {
            if (s.getAgreement() != null &&
                    s.getAgreement().findItemByInternalId(internalItemId) != null) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Returns the total number of registered suppliers.
     */
    public int getSupplierCount() {
        return suppliers.size();
    }

    @Override
    public String toString() {
        return "SupplierManager{supplierCount=" + suppliers.size() + '}';
    }
}
