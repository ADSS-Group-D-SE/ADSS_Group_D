package CrossCuttingPackage;

import java.util.HashMap;
import java.util.List;

public class SupplierAgreementDTO {

    public HashMap<String,Double> agreement;
    public List<DiscountRuleDTO> rules;

    public SupplierAgreementDTO(HashMap<String,Double> agreement,List<DiscountRuleDTO> rules)
    {
        this.agreement = agreement;
        this.rules = rules;
    }
}
