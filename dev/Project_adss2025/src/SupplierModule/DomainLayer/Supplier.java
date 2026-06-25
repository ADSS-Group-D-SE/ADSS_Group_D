package SupplierModule.DomainLayer;

import CrossCuttingPackage.SupplierDTO;
import SupplierModule.DataAccessLayer.ContactDAO;
import SupplierModule.DataAccessLayer.SupplierDAO;

import java.time.DayOfWeek;
import java.util.*;

public class Supplier {
    private static final ContactDAO conDao = new ContactDAO();

    private final String supplierId;
    private String regNumber;// Company Registration Number (ח"פ)
    private String name; // Supplier name
    private String bankAccount; // Bank account details
    private PaymentTerms paymentTerms;
    private final HashMap<String,ContactInfo> contactPersons; // Contact persons for this supplier
    private DeliveryDaySchedule ddc;
    private SupplierAgreement agreement;

    public Supplier(String supplierId, String name, String bankAccount, PaymentTerms paymentTerms,String reg,HashMap<String,Double> itemsToPrice) {

        if (supplierId == null || supplierId.isEmpty()) {
            throw new IllegalArgumentException("Company ID cannot be null or empty.");
        }
        if(paymentTerms == null)
            throw new IllegalArgumentException("PaymentTerms cannot be null");

        this.VerifyQ(itemsToPrice.values());

        this.paymentTerms = paymentTerms;
        this.supplierId = supplierId;
        this.ddc = new DeliveryDaySchedule(new ArrayList<>());

        this.setRegNumber(reg);
        this.setName(name);
        this.setBankAccount(bankAccount);


        this.contactPersons = new HashMap<>();
        this.agreement = new SupplierAgreement(this.supplierId,itemsToPrice);
    }

    public Supplier(SupplierDTO dto)
    {
        this.supplierId = dto.supplierId;

        this.setRegNumber(dto.regNumber);
        this.setName(dto.name);
        this.setBankAccount(dto.bankAccount);
        this.setPaymentTerms(dto.paymentTerms);
        this.contactPersons = ContactInfo.convert(dto.contactPersons);
        this.ddc = new DeliveryDaySchedule(dto.ddc);
        this.agreement = new SupplierAgreement(dto.agreement);
    }


    public SupplierDTO toDTO()
    {
        return new SupplierDTO(this.supplierId,this.regNumber,this.name,this.bankAccount,this.paymentTerms.getPaymentMethod()
                ,ContactInfo.convert(this.contactPersons),ddc.toString(),agreement.toDTO());
    }

    public void AddContact(String name,String email, String phoneNumber) {

        ContactInfo toAdd = new ContactInfo(name,email,phoneNumber);

        conDao.Insert(supplierId,name,email,phoneNumber);
        contactPersons.put(name,toAdd);
    }

    public ContactInfo FindContact(String name)
    {
        if(!this.contactPersons.containsKey(name))
            throw new RuntimeException("Supplier " + this.getSupplierId() + "-RemoveContact:Name " + name + " does not exist in contact map.");
        return this.contactPersons.get(name);
    }
    public void RemoveContact(String name) {

        ContactInfo toRemove = FindContact(name);
        conDao.RemoveContact(this.supplierId,toRemove.getName());
        contactPersons.remove(toRemove.getName());
    }

    /**
    Update method for contact details : name, email , phone
     **/
    public void UpdateContactName(String oldName,String newName)
    {
        if(oldName == null || oldName.isEmpty())
            throw new IllegalArgumentException("Contact person name cannot be null or empty.");

        ContactInfo c = FindContact(oldName);
        String phone = c.getPhoneNumber();
        String email = c.getEmail();

        RemoveContact(oldName);
        AddContact(newName,email,phone);
    }

    public void UpdateEmail(String name,String email)
    {
        ContactInfo c = FindContact(name);
        conDao.Update(this.supplierId,c.getName(),email,c.getPhoneNumber());
        c.setEmail(email);
    }

    public void UpdatePhone(String name,String p)
    {
        ContactInfo c = FindContact(name);
        conDao.Update(this.supplierId,c.getName(),c.getEmail(),p);
        c.setPhoneNumber(p);
    }

    public void AddFixedDay(DayOfWeek d){this.ddc.addDay(d);}

    public void RemoveFixedDay(DayOfWeek d){this.ddc.removeDay(d);}

    public boolean HasFixedDeliveryDays() { return !this.ddc.getDays().isEmpty();}

    public List<DayOfWeek> GetFixedDays(){return  this.ddc.getDays();}

    /*
    Returns null if wasnt found
     */

    public HashMap<String,ContactInfo> getContactPersons (){return this.contactPersons;}

    public String  getSupplierId() {
        return supplierId;
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

    public DeliveryDaySchedule getDDC() { return this.ddc;}

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be null or empty.");
        }

        this.name = name;
    }

    public void setBankAccount(String bankAccount) {
        if (bankAccount == null || bankAccount.isEmpty()) {
            throw new IllegalArgumentException("Supplier bank account cannot be null or empty.");
        }

        this.bankAccount = bankAccount;
    }

    public void setPaymentTerms(String paymentTerms) {
        if (paymentTerms == null || paymentTerms.isEmpty()) {
            throw new IllegalArgumentException("Payment terms cannot be null or empty");
        }

        this.paymentTerms = new PaymentTerms(paymentTerms);
    }


    public void setRegNumber(String regNumber) {
        if(regNumber == null || regNumber.isEmpty())
            throw new IllegalArgumentException("Cannot set registration number - bad argument");

        this.regNumber = regNumber;
    }

    private void VerifyQ (Collection<Double> prices)
    {
        for(Double p:prices)
        {
            if(p<=0)
                throw new IllegalArgumentException("Price for an item in agreement must be positive.");
        }
    }

    public String getRegNumber() {
        return regNumber;
    }

    public SupplierAgreement getAgreement() {
        return agreement;
    }

    public String Summary()
    {
        return "Supplier:"+this.supplierId+"\n-----------------\nRegistration number:" + this.regNumber +"\nName:" + this.name
                + "\n" +"Bank Account" + this.bankAccount +"\nPaymentTerms:" + this.paymentTerms.toString() +"\nContacts:" +contactPersons.toString() +
                "\nAgreement:" + this.agreement.getItemsInAgreement().toString()+"\nDiscount Rules:" + this.agreement.getDiscounts().toString();
    }


}
