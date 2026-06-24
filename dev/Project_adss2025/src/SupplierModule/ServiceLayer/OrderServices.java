package SupplierModule.ServiceLayer;
import CrossCuttingPackage.Report;
import CrossCuttingPackage.Response;
import SupplierModule.DomainLayer.OrderFacade;
import SupplierModule.DomainLayer.SupplierFacade;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderServices {
    private static final OrderServices INSTANCE = new OrderServices();
    private final OrderFacade of;

    private OrderServices(){this.of = new OrderFacade();}

    public static OrderServices getInstance()
    {
        return INSTANCE;
    }

    public synchronized Response<String> CreateOrder(String supId,boolean isUrgent,HashMap<String,Integer> amounts)
    {
        Response<String> res;
        try {
            res = new Response<>(null,of.CreateOrder(supId,isUrgent,amounts));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> RemoveOrder(String orderId)
    {
        Response<String> res;
        try {
            this.of.RemoveOrder(orderId);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<Report> ViewAllOrders()
    {
        Response<Report> res;
        try {
            res = new Response<>(null,of.ViewAllOrders());
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<Report> ViewOrdersBySupplier(String supId)
    {
        Response<Report> res;
        try {
            res = new Response<>(null,of.ViewAllOrdersBySupplier(supId));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<Report> ViewOrdersByDateRange(LocalDate start,LocalDate end)
    {
        Response<Report> res;
        try {
            res = new Response<>(null,of.ViewAllOrdersByDateRange(start,end));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> PrepareOrder(String orderId)
    {
        Response<String> res;
        try {
            this.of.PrepareOrder(orderId);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> CancelOrder(String orderId)
    {
        Response<String> res;
        try {
            this.of.CancelOrder(orderId);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> SendOrder(String orderId)
    {
        Response<String> res;
        try {
            this.of.SendOrder(orderId);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<HashMap<String,Integer>> DeliverOrder(String orderId)
    {
        Response<HashMap<String,Integer>> res;
        try {

            res = new Response<>(null,this.of.DeliverOrder(orderId));
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
            this.of.CleanData();
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /*
    =====================
    BuyOrderServices
    =====================
     */

    public synchronized Response<String> CreateBuyOrder(String supId, HashMap<String,Integer> amounts, List<DayOfWeek> days)
    {
        Response<String> res;
        try {
            res = new Response<>(null,this.of.CreateBuyOrder(supId,amounts,days));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> RemoveBuyOrder(String boId)
    {
        Response<String> res;
        try {
            of.DeleteBuyOrder(boId);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> AddItemToBuyOrder(String boId,String item,Integer amount)
    {
        Response<String> res;
        try {
            of.AddItemToBuyOrder(boId,item,amount);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> RemoveItemFromBuyOrder(String boId,String item)
    {
        Response<String> res;
        try {
            of.RemoveItemFromBuyOrder(boId,item);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> UpdateItemInBuyOrder(String boId,String item,Integer amount)
    {
        Response<String> res;
        try {
            of.UpdateItemInBO(boId,item,amount);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<String> AddDayToBO(String boId,DayOfWeek d)
    {
        Response<String> res;
        try {
            of.AddDayToBuyOrder(boId,d);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }
    public synchronized Response<String> RemoveDayFromBO(String boId,DayOfWeek d)
    {
        Response<String> res;
        try {
            of.RemoveDayFromBuyOrder(boId,d);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<Report> ViewAllBuyOrders()
    {
        Response<Report> res;
        try {
            res = new Response<>(null,of.ViewAllBuyOrders());
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<Report> ViewAllBuyOrdersBySupplier(String supId)
    {
        Response<Report> res;
        try {
            res = new Response<>(null,of.ViewAllBuyOrders(supId));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public synchronized Response<List<String>> CreateOrdersFromBuyOrders()
    {
        Response<List<String>> res;
        try {
            res = new Response<>(null,of.CreateOrdersFromBuyOrders());
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }


}
