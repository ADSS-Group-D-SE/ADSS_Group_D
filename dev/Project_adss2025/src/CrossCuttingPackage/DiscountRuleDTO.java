package CrossCuttingPackage;

public class DiscountRuleDTO {
    public String ruleName;
    public int minQuantity;
    public double discountPercent;

    /**
     * Class for data transfer for discountRule object between layers.
     * @param name
     * @param min
     * @param pre
     */
    public DiscountRuleDTO(String name,int min,double pre){
        this.ruleName = name;
        this.minQuantity =min;
        this.discountPercent = pre;
    }
}
