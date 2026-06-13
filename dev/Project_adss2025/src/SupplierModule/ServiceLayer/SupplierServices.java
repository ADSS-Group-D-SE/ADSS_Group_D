package SupplierModule.ServiceLayer;

import CrossCuttingPackage.Report;
import CrossCuttingPackage.Response;
import SupplierModule.DomainLayer.SupplierFacade;

import java.time.DayOfWeek;
import java.util.HashMap;

public class SupplierServices {

    private static SupplierServices INSTANCE;
    private final SupplierFacade sf;

    private SupplierServices(){this.sf = new SupplierFacade();}

    public static SupplierServices getInstance()
    {
        if(INSTANCE == null)
            INSTANCE= new SupplierServices();

        return INSTANCE;
    }


    public Response<String> AddItemToAgreement(String supId, String itemCatalog, Double price)
    {
        Response<String> res;
        try {
            sf.AddItemToAgreement(supId, itemCatalog, price);
            res = new Response<>(null, "Item added/updated successfully");
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public Response<String> RemoveItemFromAgreement(String supId, String itemCatalog)
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

    public Response<String> UpdateItemPriceInAgreement(String supId, String itemCatalog, Double newPrice)
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


    public Response<String> AddSupplier(String supId, String regNumber, String name, String bank, String pt, HashMap<String,Double> itemsToPrices)
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

    public Response<String> RemoveSupplier(String supId)
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

    public Response<String> AddContact(String supId,String name,String email, String phone)
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

    public Response<String> RemoveContact(String supId,String name)
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

    public Response<String> EditContactName(String supId,String oldName,String newName)
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

    public Response<String> EditContactEmail(String supId,String name,String email)
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

    public Response<String> EditSupplierName(String supId,String name)
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

    public Response<String> EditSupplierBank(String supId,String b)
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

    public Response<String> EditSupplierPaymentTerms(String supId,String pt)
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

    public Response<String> EditSupplierRegNumber(String supId,String reg)
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

    public Response<String> EditContactPhone(String supId,String name,String phone)
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

    public Response<Report> ViewContacts(String supId)
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

    public Response<String> AddDelvDay(String supId,DayOfWeek d)
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
    public Response<String> RemoveDelvDay(String supId, DayOfWeek d)
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

    /*
    Service that must be used before creating an order!
     */
    public Response<HashMap<String,Double>> GetAgreementPrices(String supId, HashMap<String,Integer> itemsToAmounts)
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

}
