package SupplierModule.DomainLayer;

import CrossCuttingPackage.Report;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class SupplierFacade {

    private static final HashMap<String,Supplier> suppliers = new HashMap<>();

    public SupplierFacade()
    {}



    public void AddItemToAgreement(String supId, String itemCatalog, Double price) {
        Supplier supplier = this.suppliers.get(supId);
        if (supplier == null) throw new RuntimeException("Supplier not found");
        supplier.getAgreement().AddItem(itemCatalog, price);
    }

    public void RemoveItemFromAgreement(String supId, String itemCatalog) {
        Supplier supplier = this.suppliers.get(supId);
        if (supplier == null) throw new RuntimeException("Supplier not found");
        supplier.getAgreement().RemoveItem(itemCatalog);
    }

    public void UpdateItemPriceInAgreement(String supId, String itemCatalog, Double newPrice) {
        Supplier supplier = this.suppliers.get(supId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supId + " does not exist.");
        }
        SupplierAgreement agreement = supplier.getAgreement();
        if (agreement == null) {
            throw new RuntimeException("No agreement found for supplier " + supId);
        }
        if (!agreement.getItemsInAgreement().containsKey(itemCatalog)) {
            throw new RuntimeException("Cannot update price. Item " + itemCatalog + " is not included in the agreement.");
        }
        agreement.AddItem(itemCatalog, newPrice);
    }

    /**
    Method that searches for supplier id in facade and returns its supplier object,
    Throws if id was not found.
     **/
    public Supplier FindSupplier(String id)
    {
        if(!suppliers.containsKey(id))
            throw new NoSuchElementException("SupplierFacade-FindSupplier:Supplier was not found in facade.");

        return suppliers.get(id);
    }

    /**
     * Method that adds a supplier to the facade. - creates a new supplier object.
     * @param supplierId
     * @param name
     * @param bankAccount
     * @param PayingTerms
     * @return
     */
    public String AddSupplier(String supplierId,String regNum, String name, String bankAccount, String PayingTerms,HashMap<String,Double> itemsToPrices)
    {
        try {
            if(suppliers.containsKey(supplierId))
                throw new RuntimeException("SupplierFacade-AddSupplier:Supplier already exist in system.");

            Supplier toAdd = new Supplier(supplierId,name,bankAccount,new PaymentTerms(PayingTerms),regNum,itemsToPrices);

            suppliers.put(supplierId,toAdd);
            return toAdd.getSupplierId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Method that removes a supplier from facade if it exists in it,
     * else, throws.
     * @param supplierId
     */
    public void RemoveSupplier(String supplierId)
    {
        Supplier toRemove = FindSupplier(supplierId);
        suppliers.remove(toRemove.getSupplierId());
    }

    /**
     * Method that allows adding contact to supplier.
     * @param supplierId
     * @param name
     * @param email
     * @param phoneNumber
     */
    public void AddContactToSupplier(String supplierId,String name,String email,String phoneNumber)
    {
        Supplier s = FindSupplier(supplierId);
        s.AddContact(name,email,phoneNumber);
    }

    /**
     * Method that allows removing a contact from a supplier's contact map.
     * @param supplierId
     * @param name
     */
    public void RemoveContactFromSupplier(String supplierId,String name)
    {
        Supplier s = FindSupplier(supplierId);
        s.RemoveContact(name);
    }

    public Report ViewContacts(String supId)
    {
        Report res = new Report("Contacts for supplier: " + supId+"\n");
        res.AddLine("===================================");
        for (Map.Entry<String,ContactInfo> en :FindSupplier(supId).getContactPersons().entrySet())
        {
            ContactInfo c = en.getValue();
            res.AddLine(c.toString());
            res.AddLine("--------------------------------");
        }
        return res;
    }

    /*
    ====================
    Method that operates Update contact and supplier fields.
    ====================
     */
    public void UpdateContactName(String sId,String oldName,String newName)
    {
        Supplier s = FindSupplier(sId);
        s.UpdateContactName(oldName,newName);
    }
    public void UpdateContactEmail(String sId,String name,String newEmail)
    {
        Supplier s = FindSupplier(sId);
        s.UpdateEmail(name,newEmail);
    }
    public void UpdateContactPhone(String sId,String name,String newPhone)
    {
        Supplier s = FindSupplier(sId);
        s.UpdatePhone(name,newPhone);
    }

    public void UpdateSupplierName(String supId,String name)
    {
        Supplier s = FindSupplier(supId);
        s.setName(name);
    }
    public void UpdateSupplierBank(String supId,String bankAccount)
    {
        Supplier s = FindSupplier(supId);
        s.setBankAccount(bankAccount);
    }
    public void UpdateSupplierPaymentTerms(String supId,String pt)
    {
        Supplier s = FindSupplier(supId);
        s.setPaymentTerms(pt);
    }
    public void UpdateSupplierRegNumber(String supId,String reg)
    {
        Supplier s = FindSupplier(supId);
        s.setRegNumber(reg);
    }

    /*
    =========================================
    Methods for DeliveryDateSchedule management
    =========================================
     */
    public void AddFixedDelDay(String supId,DayOfWeek d)
    {
        Supplier s = FindSupplier(supId);
        s.AddFixedDay(d);
    }

    public void RemoveFixedDelDay(String supId,DayOfWeek d)
    {
        Supplier s = FindSupplier(supId);
        s.RemoveFixedDay(d);
    }
    /*
    ======================================
    Static method for external usage
    =======================================
     */

    public static boolean IsSupplierExist(String supId)
    {
        return suppliers.containsKey(supId);
    }
}
