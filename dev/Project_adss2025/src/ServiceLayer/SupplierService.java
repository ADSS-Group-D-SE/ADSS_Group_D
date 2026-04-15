package ServiceLayer;

import DomainLayer.*;
import DomainLayer.SupplierAgreement.SupplyMethod;

import java.util.List;

public class SupplierService {

    private SupplierManager supplierManager;

    public SupplierService(SupplierManager supplierManager) {
        this.supplierManager = supplierManager;
    }

    public Supplier addSupplier(String companyId, String name, String bankAccount, String paymentTerms) {
        return supplierManager.addSupplier(companyId, name, bankAccount, paymentTerms);
    }

    public boolean removeSupplier(int supplierId) {
        return supplierManager.removeSupplier(supplierId);
    }

    public Supplier getSupplier(int supplierId) {
        return supplierManager.getSupplier(supplierId);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierManager.getAllSuppliers();
    }

    public Supplier findByCompanyId(String companyId) {
        return supplierManager.findByCompanyId(companyId);
    }

    public int getSupplierCount() {
        return supplierManager.getSupplierCount();
    }

    public void addContactPerson(int supplierId, String name, String phone, String email) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        supplier.addContactPerson(new ContactPerson(name, phone, email));
    }

    public boolean removeContactPerson(int supplierId, String contactName) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        ContactPerson cp = supplier.findContactByName(contactName);
        if (cp != null) {
            supplier.removeContactPerson(cp);
            return true;
        }
        return false;
    }

    public List<ContactPerson> getContactPersons(int supplierId) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        return supplier.getContactPersons();
    }

    public void createAgreement(int supplierId, SupplyMethod method, List<Integer> fixedDays, int deliveryDays) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        SupplierAgreement agreement = new SupplierAgreement(method);
        if (method == SupplyMethod.FIXED_DAYS && fixedDays != null) {
            for (int day : fixedDays) {
                agreement.addFixedSupplyDay(day);
            }
        }
        if (method == SupplyMethod.ON_ORDER) {
            agreement.setDeliveryDays(deliveryDays);
        }
        supplier.setAgreement(agreement);
    }

    public SupplierAgreement getAgreement(int supplierId) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        return supplier.getAgreement();
    }

    public void updateSupplyMethod(int supplierId, SupplyMethod method) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.setSupplyMethod(method);
    }

    public void setDeliveryDays(int supplierId, int days) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.setDeliveryDays(days);
    }

    public void addFixedSupplyDay(int supplierId, int day) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.addFixedSupplyDay(day);
    }

    public void removeFixedSupplyDay(int supplierId, int day) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        agreement.removeFixedSupplyDay(day);
    }

    public void addItemToAgreement(int supplierId, int catalogNumber, int internalId,
            String description, double price, String manufacturer) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement. Create one first.");
        }
        SupplierItem item = new SupplierItem(catalogNumber, internalId, description, price, manufacturer);
        agreement.addItem(item);
    }

    public boolean removeItemFromAgreement(int supplierId, int catalogNumber) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        SupplierItem item = agreement.findItemByCatalogNumber(catalogNumber);
        if (item != null) {
            agreement.removeItem(item);
            return true;
        }
        return false;
    }

    public List<SupplierItem> getAgreementItems(int supplierId) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        return agreement.getItems();
    }

    public void addQuantityDiscount(int supplierId, int catalogNumber, int minQuantity, double discountPercent) {
        SupplierAgreement agreement = getAgreement(supplierId);
        if (agreement == null) {
            throw new IllegalArgumentException("Supplier has no agreement.");
        }
        SupplierItem item = agreement.findItemByCatalogNumber(catalogNumber);
        if (item == null) {
            throw new IllegalArgumentException("Item with catalog number " + catalogNumber + " not found.");
        }
        item.addQuantityDiscount(new QuantityDiscount(minQuantity, discountPercent));
    }

    public List<Supplier> findSuppliersByItem(int internalItemId) {
        return supplierManager.findSuppliersByItem(internalItemId);
    }
}
