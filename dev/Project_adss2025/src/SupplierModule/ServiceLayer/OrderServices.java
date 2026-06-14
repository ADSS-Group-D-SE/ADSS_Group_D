package SupplierModule.ServiceLayer;
import CrossCuttingPackage.Report;
import CrossCuttingPackage.Response;
import SupplierModule.DomainLayer.OrderFacade;
import SupplierModule.DomainLayer.SupplierFacade;

import java.time.LocalDate;
import java.util.HashMap;

public class OrderServices {
    private static OrderServices INSTANCE;
    private final OrderFacade of;

    private OrderServices(){this.of = new OrderFacade();}

    public static OrderServices getInstance()
    {
        if(INSTANCE == null)
            INSTANCE= new OrderServices();

        return INSTANCE;
    }

    public Response<String> CreateOrder(String supId,boolean isUrgent,HashMap<String,Integer> amounts,HashMap<String,Double> prices)
    {
        Response<String> res;
        try {
            res = new Response<>(null,of.CreateOrder(supId,isUrgent,amounts,prices));
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public Response<String> RemoveOrder(String orderId)
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

    public Response<Report> ViewAllOrders()
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

    public Response<Report> ViewOrdersBySupplier(String supId)
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

    public Response<Report> ViewOrdersByDateRange(LocalDate start,LocalDate end)
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

    public Response<String> PrepareOrder(String orderId)
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

    public Response<String> CancelOrder(String orderId)
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

    public Response<String> DeliverOrder(String orderId)
    {
        Response<String> res;
        try {
            this.of.DeliverOrder(orderId);
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }



}
