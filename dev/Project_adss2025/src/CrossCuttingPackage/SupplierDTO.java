package CrossCuttingPackage;


import java.util.HashMap;
import java.util.List;

public class SupplierDTO {
    public String supplierId;
    public String regNumber;
    public String name;
    public String bankAccount;
    public String paymentTerms;
    public List<ContactDTO> contactPersons;
    public String ddc;
    public SupplierAgreementDTO agreement;

    /**
     * Class made for transferring supplier data between layers.
     * @param supId
     * @param regNumber
     * @param name
     * @param bankAccount
     * @param paymentTerms
     * @param contact
     * @param ddc
     * @param agreement
     */
    public SupplierDTO(String supId,String regNumber,String name,String bankAccount,String paymentTerms,List<ContactDTO> contact,String ddc,SupplierAgreementDTO agreement)
    {
        this.supplierId = supId;
        this.regNumber = regNumber;
        this.name = name;
        this.bankAccount = bankAccount;
        this.paymentTerms = paymentTerms;
        this.contactPersons = contact;
        this.ddc = ddc;
        this.agreement = agreement;
    }
}
