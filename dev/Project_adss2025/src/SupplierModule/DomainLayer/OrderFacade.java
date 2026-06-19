package SupplierModule.DomainLayer;
import CrossCuttingPackage.Report;
import java.time.LocalDate;
import java.util.*;

public class OrderFacade {

    private HashMap<String,SupplierOrder> orders;

    public OrderFacade()
    {
        this.orders = new HashMap<>();
    }

    public SupplierOrder FindOrderById(String oId)
    {
        if(!this.orders.containsKey(oId))
            throw new NoSuchElementException("OrderFacade:FindOrderById - Order " + oId + " was not located in facade.");
        return this.orders.get(oId);
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
        this.orders.put(toAdd.getOrderId(),toAdd);
        return toAdd.getOrderId();
    }

    public void RemoveOrder(String orderId)
    {
        SupplierOrder toRemove = FindOrderById(orderId);
        this.orders.remove(orderId);
    }

    public Report ViewAllOrders()
    {
        Report res = new Report("All orders report\n");
        res.AddLine("===========================");
        for(Map.Entry<String,SupplierOrder> en:this.orders.entrySet())
        {
            String id = en.getKey();
            SupplierOrder order = en.getValue();
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
        Report res = new Report("All orders report\n");
        res.AddLine("===========================");
        for(Map.Entry<String,SupplierOrder> en:this.orders.entrySet())
        {

            String id = en.getKey();
            SupplierOrder order = en.getValue();
            if(supId.equals(order.getSupplierId())) {
                res.AddLine("Order:" + id + ":");
                res.AddLine(order.Summary());
                res.AddLine("Total Price:" + order.GetTotalPrice());
                res.AddLine("Status:" + order.getStatus().toString());
                res.AddLine("-----------------------------");
            }
        }
        return res;
    }
    public Report ViewAllOrdersByDateRange(LocalDate start, LocalDate end)
    {
        Report res = new Report("All orders report\n");
        res.AddLine("===========================");
        for(Map.Entry<String,SupplierOrder> en:this.orders.entrySet())
        {
            String id = en.getKey();
            SupplierOrder order = en.getValue();
            if(order.getOrderDate().isBefore(end.plusDays(1)) && order.getOrderDate().isAfter(start.minusDays(1))) {
                res.AddLine("Order:" + id + ":");
                res.AddLine(order.Summary());
                res.AddLine("Total Price:" + order.GetTotalPrice());
                res.AddLine("Status:" + order.getStatus().toString());
                res.AddLine("-----------------------------");
            }
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
        order.Prepare();
    }

    public void CancelOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        order.Cancel();
    }

    public void SendOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        order.SendOrder();
    }
    public void DeliverOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        order.MarkDelivered();
    }

}
