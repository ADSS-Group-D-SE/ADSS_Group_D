package CrossCuttingPackage;

import java.util.HashMap;
import java.util.List;

public class SupplierAgreementDTO {

    public String supId;
    public HashMap<String,Double> agreement;
    public HashMap<String,List<DiscountRuleDTO>> rules;

    public SupplierAgreementDTO(String supId,HashMap<String,Double> agreement,HashMap<String,List<DiscountRuleDTO>> rules)
    {
        this.supId = supId;
        this.agreement = agreement;
        this.rules = rules;
    }
}
