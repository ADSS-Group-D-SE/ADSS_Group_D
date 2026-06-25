package SupplierModule.ServiceLayer;

import CrossCuttingPackage.Notification;
import CrossCuttingPackage.Report;
import CrossCuttingPackage.Response;
import SupplierModule.DomainLayer.SupplierFacade;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;

public class SupplierServices {

    private static final SupplierServices INSTANCE = new SupplierServices();
    private final SupplierFacade sf;

    private SupplierServices(){this.sf = new SupplierFacade();}

    public static SupplierServices getInstance()
    {
        return INSTANCE;
    }


    public synchronized Response<String> AddItemToAgreement(String supId, String itemCatalog, Double price)
    {
        Response<String> res;
        try {
            sf.AddItemToAgreement(supId, itemCatalog, price);
            res = new Response<>(null, "Item added successfully");
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> RemoveItemFromAgreement(String supId, String itemCatalog)
    {
        Response<String> res;
        try {
            sf.RemoveItemFromAgreement(supId, itemCatalog);
            res = new Response<>(null, null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> UpdateItemPriceInAgreement(String supId, String itemCatalog, Double newPrice)
    {
        Response<String> res;
        try {
            sf.UpdateItemPriceInAgreement(supId, itemCatalog, newPrice);
            res = new Response<>(null, "Item price updated successfully");
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }


    public synchronized Response<String> AddSupplier(String supId, String regNumber, String name, String bank, String pt, HashMap<String,Double> itemsToPrices)
    {
        Response<String> res;
        try {
            res = new Response<>(null,sf.AddSupplier(supId,regNumber,name,bank,pt,itemsToPrices));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> RemoveSupplier(String supId)
    {
        Response<String> res;
        try {
            sf.RemoveSupplier(supId);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> AddContact(String supId,String name,String email, String phone)
    {
        Response<String> res;
        try {
            sf.AddContactToSupplier(supId,name,email,phone);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> RemoveContact(String supId,String name)
    {
        Response<String> res;
        try {
            sf.RemoveContactFromSupplier(supId,name);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> EditContactName(String supId,String oldName,String newName)
    {
        Response<String> res;
        try {
            sf.UpdateContactName(supId,oldName,newName);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> EditContactEmail(String supId,String name,String email)
    {
        Response<String> res;
        try {
            sf.UpdateContactEmail(supId,name,email);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> EditSupplierName(String supId,String name)
    {
        Response<String> res;
        try {
            sf.UpdateSupplierName(supId,name);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> EditSupplierBank(String supId,String b)
    {
        Response<String> res;
        try {
            sf.UpdateSupplierBank(supId,b);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> EditSupplierPaymentTerms(String supId,String pt)
    {
        Response<String> res;
        try {
            sf.UpdateSupplierPaymentTerms(supId,pt);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> EditSupplierRegNumber(String supId,String reg)
    {
        Response<String> res;
        try {
            sf.UpdateSupplierRegNumber(supId,reg);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> EditContactPhone(String supId,String name,String phone)
    {
        Response<String> res;
        try {
            sf.UpdateContactPhone(supId,name,phone);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<Report> ViewContacts(String supId)
    {
        Response<Report> res;
        try {
            res = new Response<>(null,sf.ViewContacts(supId));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> AddDelvDay(String supId,DayOfWeek d)
    {
        Response<String> res;
        try {
            sf.AddFixedDelDay(supId,d);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }
    public synchronized Response<String> RemoveDelvDay(String supId, DayOfWeek d)
    {
        Response<String> res;
        try {
            sf.RemoveFixedDelDay(supId,d);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> AddDiscountRule(String supId,String cat,String name,double d,int min)
    {
        Response<String> res;
        try {
            sf.AddDiscountRule(supId,cat,name,d,min);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> RemoveDiscountRule(String supId,String cat,String name)
    {
        Response<String> res;
        try {
            sf.RemoveDiscountRule(supId,cat,name);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> UpdateDiscountRuleMin(String supId,String cat,String name,int min)
    {
        Response<String> res;
        try {
            sf.UpdateDiscountRuleMin(supId,cat,name,min);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> UpdateDiscountRuleDisc(String supId,String cat,String name,double d)
    {
        Response<String> res;
        try {
            sf.UpdateDiscountRuleDiscount(supId,cat,name,d);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /*
    Service that must be used before creating an order!
     */
    public synchronized Response<HashMap<String,Double>> GetAgreementPrices(String supId, HashMap<String,Integer> itemsToAmounts)
    {
        Response<HashMap<String,Double>> res;
        try {
            res = new Response<>(null,sf.GetPricesFromAgreement(supId,itemsToAmounts));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<Report> ViewAllSuppliers()
    {
        Response<Report> res;
        try {
            res = new Response<>(null,sf.ViewAllSuppliers());
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<HashMap<String,Double>> getSupplierItems(String supId) {
        Response<HashMap<String,Double>> res;
        try {
            res = new Response<>(null,this.sf.getProductfromSupplier(supId));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> Clean()
    {
        Response<String> res;
        try {
            sf.CleanData();
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public Response<String> Load()
    {
        Response<String> res;
        try {
            sf.LoadData();
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /*
    ===================================
    Automatic orders functionality
    ===================================
    */

    public synchronized Response<String> FindBestSupplier(Notification n)
    {
        Response<String> res;
        try {
            res = new Response<>(null,sf.FindBestSupplier(n));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }


}



