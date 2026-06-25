package SupplierModule.DomainLayer;

import CrossCuttingPackage.DiscountRuleDTO;
import SupplierModule.DataAccessLayer.DiscountRuleDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscountRule {

    private static final DiscountRuleDAO dao = new DiscountRuleDAO();
    private final String ruleName;
    private int minQuantity; // Minimum quantity to trigger this discount tier
    private double discountPercent; // Discount percentage for this tier


    public DiscountRule(int minQuantity, double discountPercent,String name) {
        if (minQuantity <= 0) {
            throw new IllegalArgumentException("Minimum quantity must be positive.");
        }
        if (discountPercent < 0 || discountPercent > 1) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 1. 0.5 = 50% for example.");
        }
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Rule name cannot be null or empty.");
        this.ruleName = name;
        this.minQuantity = minQuantity;
        this.discountPercent = discountPercent;
    }

    public DiscountRule(DiscountRuleDTO dto)
    {
        this.ruleName = dto.ruleName;
        this.discountPercent = dto.discountPercent;
        this.minQuantity = dto.minQuantity;
    }



    public DiscountRuleDTO toDTO(){return new DiscountRuleDTO(this.ruleName,this.minQuantity,this.discountPercent);}
    public int getMinQuantity() {
        return minQuantity;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public String getRuleName() {return ruleName;}

    public void setMinQuantity(String supId,String cat,int minQuantity) {
        if (minQuantity <= 0) {
            throw new IllegalArgumentException("Minimum quantity must be positive.");
        }
        dao.Update(supId,this.ruleName,cat,this.discountPercent,minQuantity);
        this.minQuantity = minQuantity;
    }

    public void setDiscountPercent(String supId,String cat,double discountPercent) {
        if (discountPercent < 0 || discountPercent > 1) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 1.");
        }
        dao.Update(supId,this.ruleName,cat,discountPercent,this.minQuantity);
        this.discountPercent = discountPercent;
    }

    public boolean IsApplicable(int q) { return q>= this.minQuantity;}

    @Override
    public String toString() {
        return "Discount Rule " + this.getRuleName() +" : {" +
                "minQuantity=" + minQuantity +
                ", discountPercent=" + discountPercent*100 + "%" +
                '}';
    }

    public static HashMap<String,List<DiscountRuleDTO>> convert(HashMap<String,List<DiscountRule>> map)
    {
        HashMap<String,List<DiscountRuleDTO>> res = new HashMap<>();
        for(Map.Entry<String,List<DiscountRule>> en: map.entrySet())
        {
            res.put(en.getKey(), convertList(en.getValue()));
        }
        return res;
    }

    public static HashMap<String,List<DiscountRule>> convertToRules(HashMap<String,List<DiscountRuleDTO>> map)
    {
        HashMap<String,List<DiscountRule>> res = new HashMap<>();
        for(Map.Entry<String,List<DiscountRuleDTO>> en: map.entrySet())
        {
            res.put(en.getKey(), convertListToRules(en.getValue()));
        }
        return res;
    }

    private static List<DiscountRuleDTO> convertList(List<DiscountRule> l)
    {
        List<DiscountRuleDTO> res = new ArrayList<>();
        for(DiscountRule d:l)
            res.add(d.toDTO());
        return res;
    }

    private static List<DiscountRule> convertListToRules(List<DiscountRuleDTO> l)
    {
        List<DiscountRule> res = new ArrayList<>();
        for(DiscountRuleDTO d:l)
            res.add(new DiscountRule(d));
        return res;
    }


}
