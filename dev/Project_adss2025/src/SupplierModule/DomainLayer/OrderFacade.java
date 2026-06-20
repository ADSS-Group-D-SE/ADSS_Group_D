package SupplierModule.DomainLayer;
import CrossCuttingPackage.Report;
import CrossCuttingPackage.SupplierOrderDTO;
import SupplierModule.DataAccessLayer.SupplierOrderDAO;

import java.time.LocalDate;
import java.util.*;

public class OrderFacade {

    //private HashMap<String,SupplierOrder> orders;
    private static final SupplierOrderDAO orderDao = new SupplierOrderDAO();
    public OrderFacade()
    {}

    public SupplierOrder FindOrderById(String oId)
    {
        SupplierOrderDTO order = orderDao.SelectByOrderId(oId);

        return new SupplierOrder(order);
    }


    /*
    Before creating an order UI must:
    Call for the service - GetAgreementPrices with the quantities.
     */
    public String CreateOrder(String supId,boolean isUrgent,HashMap<String,Integer> itemsToQuan,HashMap<String,Double> prices)
    {
        if(!prices.keySet().equals(itemsToQuan.keySet()))
            throw new RuntimeException("OrderFacade:CreateOrder - Items sets is different between prices and amounts.");
        if(!isUrgent && SupplierFacade.IsSupplierOnFixedDays(supId) && !SupplierFacade.IsDayInSchedule(supId,LocalDate.now().getDayOfWeek()))
            throw new RuntimeException("OrderFacade:CreateOrder - Supplier " + supId +" accepts does not accept non urgent orders today.");

        SupplierOrder toAdd = new SupplierOrder(supId,itemsToQuan,prices);
        orderDao.Insert(toAdd.toDTO());
        return toAdd.getOrderId();
    }

    public void RemoveOrder(String orderId)
    {
        SupplierOrder toRemove = FindOrderById(orderId);
        orderDao.Delete(toRemove.getOrderId());
    }

    public Report ViewAllOrders()
    {
        Report res = new Report("All orders report\n");
        res.AddLine("===========================");
        List<SupplierOrder> orders = SupplierOrder.convertToOrders(orderDao.SelectAll());
        for(SupplierOrder order:orders)
        {
            String id = order.getOrderId();
            res.AddLine("Order:" +id+":");
            res.AddLine(order.Summary());
            res.AddLine("Total Price:" + order.GetTotalPrice());
            res.AddLine("Status:" + order.getStatus().toString());
            res.AddLine("-----------------------------");
        }
        return res;
    }

    public Report ViewAllOrdersBySupplier(String supId)
    {
        Report res = new Report("All orders report by supplier:" +supId +"\n" );
        res.AddLine("===========================");
        List<SupplierOrder> orders = SupplierOrder.convertToOrders(orderDao.SelectAllBySupplierId(supId));
        for(SupplierOrder order:orders)
        {
            String id = order.getOrderId();
            res.AddLine("Order:" +id+":");
            res.AddLine(order.Summary());
            res.AddLine("Total Price:" + order.GetTotalPrice());
            res.AddLine("Status:" + order.getStatus().toString());
            res.AddLine("-----------------------------");
        }
        return res;
    }
    public Report ViewAllOrdersByDateRange(LocalDate start, LocalDate end)
    {
        Report res = new Report("All orders report\n");
        res.AddLine("===========================");
        List<SupplierOrder> orders = SupplierOrder.convertToOrders(orderDao.SelectAllByDateRange(start.toString(),end.toString()));
        for(SupplierOrder order:orders)
        {
            String id = order.getOrderId();
            res.AddLine("Order:" +id+":");
            res.AddLine(order.Summary());
            res.AddLine("Total Price:" + order.GetTotalPrice());
            res.AddLine("Status:" + order.getStatus().toString());
            res.AddLine("-----------------------------");
        }
        return res;
    }

    /*
    ================================
    Order status change methods
    ================================
     */
    public void PrepareOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        orderDao.UpdateStatus(order.getOrderId(), SupplierOrder.OrderStatus.PREP.toString());
    }

    public void CancelOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        orderDao.UpdateStatus(order.getOrderId(), SupplierOrder.OrderStatus.CANCELLED.toString());
    }

    public void SendOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        orderDao.UpdateStatus(order.getOrderId(), SupplierOrder.OrderStatus.SENT.toString());
    }
    public void DeliverOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        orderDao.UpdateStatus(order.getOrderId(), SupplierOrder.OrderStatus.DELIVERED.toString());
    }

    public void CleanData()
    {
        orderDao.Clean();
    }
}
