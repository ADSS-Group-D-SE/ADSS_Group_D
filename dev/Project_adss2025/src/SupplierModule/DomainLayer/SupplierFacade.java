package SupplierModule.DomainLayer;

import CrossCuttingPackage.Notification;
import CrossCuttingPackage.Report;
import CrossCuttingPackage.SupplierAgreementDTO;
import CrossCuttingPackage.SupplierDTO;
import SupplierModule.DataAccessLayer.SupplierAgreementDAO;
import SupplierModule.DataAccessLayer.SupplierDAO;

import java.time.DayOfWeek;
import java.util.*;

public class SupplierFacade {

    private static final SupplierDAO supDao = new SupplierDAO();
    private static final HashMap<String,Supplier> suppliers = new HashMap<>();

    public SupplierFacade()
    {}



    public void AddItemToAgreement(String supId, String itemCatalog, Double price) {
        Supplier supplier = FindSupplier(supId);
        supplier.getAgreement().AddItem(itemCatalog, price);
    }

    public void RemoveItemFromAgreement(String supId, String itemCatalog) {
        Supplier supplier = FindSupplier(supId);
        supplier.getAgreement().RemoveItem(itemCatalog);
    }

    public void UpdateItemPriceInAgreement(String supId, String itemCatalog, Double newPrice) {
        Supplier supplier = FindSupplier(supId);
        SupplierAgreement agreement = supplier.getAgreement();
        if (agreement == null) {
            throw new RuntimeException("No agreement found for supplier " + supId);
        }
        if (!agreement.getItemsInAgreement().containsKey(itemCatalog)) {
            throw new RuntimeException("Cannot update price. Item " + itemCatalog + " is not included in the agreement.");
        }

        agreement.UpdateItemPrice(itemCatalog, newPrice);
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
        if(suppliers.containsKey(supplierId))
            throw new RuntimeException("SupplierFacade-AddSupplier:Supplier already exist in system.");
        Supplier toAdd = new Supplier(supplierId,name,bankAccount,new PaymentTerms(PayingTerms),regNum,itemsToPrices);

        supDao.Insert(toAdd.toDTO()); // saves in db.

        suppliers.put(supplierId,toAdd);
        return toAdd.getSupplierId();
    }

    /**
     * Method that removes a supplier from facade if it exists in it,
     * else, throws.
     * @param supplierId
     */
    public void RemoveSupplier(String supplierId)
    {
        Supplier toRemove = FindSupplier(supplierId);
        supDao.Delete(supplierId);
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

    public SupplierAgreement GetAgreement(String supId)
    {
        Supplier s = FindSupplier(supId);
        return s.getAgreement();
    }
    /*
    Note - must be used before creating an order
     */
    public static HashMap<String,Double> GetPricesFromAgreement(String supId,HashMap<String,Integer> itemsToQuan)
    {
        if(!suppliers.containsKey(supId))
            throw new NoSuchElementException("SupplierFacade-FindSupplier:Supplier was not found in facade.");

        SupplierAgreement agreement= suppliers.get(supId).getAgreement();
        if(!agreement.getItemsCatalogs().containsAll(itemsToQuan.keySet()))
            throw new RuntimeException("OrderFacade:GetPricesFromAgreement Items sent to order are not in the agreement.");
        HashMap<String,Double> res = new HashMap<>();

        for(String item:itemsToQuan.keySet())
            res.put(item,agreement.GetEffectivePrice(item,itemsToQuan.get(item)));

        return res;
    }

    public Report ViewAllSuppliers()
    {
        Report res = new Report("Suppliers report\n");
        res.AddLine("===========================");
        for (Map.Entry<String,Supplier> en : suppliers.entrySet())
        {
            Supplier s = en.getValue();
            res.AddLine(s.Summary());
            res.AddLine("");
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

        supDao.UpdateSupplier(s.getSupplierId(),name,s.getRegNumber(),s.getBankAccount(),s.getPaymentTerms().toString(),s.getDDC().toString());
        s.setName(name);
    }
    public void UpdateSupplierBank(String supId,String bankAccount)
    {
        Supplier s = FindSupplier(supId);

        supDao.UpdateSupplier(s.getSupplierId(),s.getName(),s.getRegNumber(),bankAccount,s.getPaymentTerms().toString(),s.getDDC().toString());
        s.setBankAccount(bankAccount);
    }
    public void UpdateSupplierPaymentTerms(String supId,String pt)
    {
        Supplier s = FindSupplier(supId);

        supDao.UpdateSupplier(s.getSupplierId(),s.getName(),s.getRegNumber(),s.getBankAccount(),pt,s.getDDC().toString());
        s.setPaymentTerms(pt);
    }
    public void UpdateSupplierRegNumber(String supId,String reg)
    {
        Supplier s = FindSupplier(supId);

        supDao.UpdateSupplier(s.getSupplierId(),s.getName(),reg,s.getBankAccount(),s.getPaymentTerms().toString(),s.getDDC().toString());
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
        DeliveryDaySchedule temp =new DeliveryDaySchedule(s.getDDC().getDays());
        temp.addDay(d);

        supDao.UpdateSupplier(s.getSupplierId(),s.getName(),s.getRegNumber(),s.getBankAccount(),s.getPaymentTerms().toString(),temp.toString());
        s.AddFixedDay(d);
    }

    public void RemoveFixedDelDay(String supId,DayOfWeek d)
    {
        Supplier s = FindSupplier(supId);
        DeliveryDaySchedule temp =new DeliveryDaySchedule(s.getDDC().getDays());
        temp.removeDay(d);

        supDao.UpdateSupplier(s.getSupplierId(),s.getName(),s.getRegNumber(),s.getBankAccount(),s.getPaymentTerms().toString(),temp.toString());
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

    public static boolean IsSupplierOnFixedDays(String supId)
    {
        if(IsSupplierExist(supId))
            return suppliers.get(supId).HasFixedDeliveryDays();
        return false;
    }

    public static boolean IsDayInSchedule(String supId,DayOfWeek d)
    {
        if(IsSupplierExist(supId))
        {
            return suppliers.get(supId).GetFixedDays().contains(d);
        }
        return false;
    }

    public HashMap<String,Double> getProductfromSupplier(String supId) {
        if (supId == null || supId.trim().isEmpty()) {
            throw new RuntimeException("Supplier ID cannot be empty.");
        }

        Supplier supplier = suppliers.get(supId);

        if (supplier == null) {
            throw new RuntimeException("Supplier with ID " + supId + " does not exist.");
        }

        HashMap<String,Double> items = supplier.getAgreement().getItemsInAgreement();

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Supplier " + supId + " has no items in their catalog.");
        }

        return items;
    }

    public void AddDiscountRule(String supId,String cat,String name,double disc,int min){

        Supplier s = FindSupplier(supId);
        SupplierAgreement agreement = s.getAgreement();

        if (agreement == null) {
            throw new RuntimeException("No agreement found for supplier " + supId);
        }

        agreement.AddDiscountRule(cat,name,min,disc);
    }

    public void RemoveDiscountRule(String supId,String cat,String name){

        Supplier s = FindSupplier(supId);
        SupplierAgreement agreement = s.getAgreement();

        if (agreement == null) {
            throw new RuntimeException("No agreement found for supplier " + supId);
        }

        agreement.RemoveDiscountRule(cat,name);
    }

    public void UpdateDiscountRuleMin(String supId,String cat,String name,int min){

        Supplier s = FindSupplier(supId);
        SupplierAgreement agreement = s.getAgreement();

        if (agreement == null) {
            throw new RuntimeException("No agreement found for supplier " + supId);
        }

        DiscountRule rule = agreement.FindDRule(cat,name);

        rule.setMinQuantity(supId,cat,min);
    }

    public void UpdateDiscountRuleDiscount(String supId,String cat,String name,double d){

        Supplier s = FindSupplier(supId);
        SupplierAgreement agreement = s.getAgreement();

        if (agreement == null) {
            throw new RuntimeException("No agreement found for supplier " + supId);
        }

        DiscountRule rule = agreement.FindDRule(cat,name);

        rule.setDiscountPercent(supId,cat,d);
    }

    public void CleanData()
    {
        supDao.Clean(); //clears data in db in suppliers.
    }

    /*
    Builds facade from data from DB in a list of DTOs.
     */
    public void LoadData()
    {
        List<SupplierDTO> sups = supDao.SelectAll();
        for(SupplierDTO s:sups)
        {
            suppliers.put(s.supplierId,new Supplier(s));
        }
    }

    /*
    ===================================
    Automatic orders functionality
    ===================================
    */

    public String FindBestSupplier(Notification n)
    {
        String res = null;
        Double min = null;
        for(Map.Entry<String, Supplier> en :suppliers.entrySet())
        {
            String supId = en.getKey();
            SupplierAgreement agreement = GetAgreement(supId);
            if(agreement.getItemsCatalogs().contains(n.getCatalog_number()))
            {
                Double temp = agreement.GetEffectivePrice(n.getCatalog_number(), n.HowManyToRestock());
                if (min == null || min > temp) {
                    min = temp;
                    res = supId;
                }
            }
        }

        if(res == null)
            throw new RuntimeException("Find best supplier for notification for ite,:" + n.getCatalog_number() +" no suppliers sells such product.");
        return res;
    }
}
