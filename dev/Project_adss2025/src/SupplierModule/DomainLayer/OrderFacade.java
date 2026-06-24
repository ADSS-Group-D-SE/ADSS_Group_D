package SupplierModule.DomainLayer;
import CrossCuttingPackage.BuyOrderDTO;
import CrossCuttingPackage.Report;
import CrossCuttingPackage.SupplierItemDTO;
import CrossCuttingPackage.SupplierOrderDTO;
import SupplierModule.DataAccessLayer.BuyOrderDAO;
import SupplierModule.DataAccessLayer.SupplierOrderDAO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class OrderFacade {

    //private HashMap<String,SupplierOrder> orders;
    private static final SupplierOrderDAO orderDao = new SupplierOrderDAO();
    private static final BuyOrderDAO boDao = new BuyOrderDAO();
    private final HashMap<String,BuyOrder> buyOrders;
    public OrderFacade()
    {
        buyOrders = new HashMap<>();
    }

    public SupplierOrder FindOrderById(String oId)
    {
        SupplierOrderDTO order = orderDao.SelectByOrderId(oId);

        return new SupplierOrder(order);
    }


    /*
    Before creating an order UI must:
    Call for the service - GetAgreementPrices with the quantities.
     */
    public String CreateOrder(String supId,boolean isUrgent,HashMap<String,Integer> itemsToQuan)
    {
        if(!isUrgent && SupplierFacade.IsSupplierOnFixedDays(supId) && !SupplierFacade.IsDayInSchedule(supId,LocalDate.now().getDayOfWeek()))
            throw new RuntimeException("OrderFacade:CreateOrder - Supplier " + supId +" accepts does not accept non urgent orders today.");

        HashMap<String,Double> prices = SupplierFacade.GetPricesFromAgreement(supId,itemsToQuan);
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
        order.Prepare();
        orderDao.UpdateStatus(order.getOrderId(), SupplierOrder.OrderStatus.PREP.toString());
    }

    public void CancelOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        order.Cancel();
        orderDao.UpdateStatus(order.getOrderId(), SupplierOrder.OrderStatus.CANCELLED.toString());
    }

    public void SendOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        order.SendOrder();
        orderDao.UpdateStatus(order.getOrderId(), SupplierOrder.OrderStatus.SENT.toString());
    }
    public HashMap <String,Integer> DeliverOrder(String orderId)
    {
        SupplierOrder order = FindOrderById(orderId);
        order.MarkDelivered();
        orderDao.UpdateStatus(order.getOrderId(), SupplierOrder.OrderStatus.DELIVERED.toString());

        return this.GetOrderItems(orderId);
    }

    public void CleanData()
    {
        orderDao.Clean();
        boDao.Clean();
    }


    /*
    Methods for automatic orders
     */

    public HashMap<String,Integer> GetOrderItems(String oId)
    {
        HashMap<String,Integer> res = new HashMap<>();
        SupplierOrderDTO o = FindOrderById(oId).toDTO();
        for(SupplierItemDTO i:o.items)
        {
            res.put(i.catalogNumber,i.amount);
        }
       return res;
    }

    public String CreateBuyOrder(String supId, HashMap<String,Integer> amounts, List<DayOfWeek> days)
    {
        if(!SupplierFacade.IsSupplierExist(supId))
            throw new RuntimeException("OrderFacade:CreateBuyOrder - supplier " + supId + " does not exist in system.");

        BuyOrder toAdd = new BuyOrder(supId,amounts,days);
        boDao.Insert(toAdd.toDTO());
        this.buyOrders.put(toAdd.getBuyOrderID(),toAdd);

        return toAdd.getSupId();
    }

    public BuyOrder FindBuyOrderById(String boId)
    {
        if(this.buyOrders.containsKey(boId))
            throw new NoSuchElementException("OrderFacade:FindBuyOrderById - cannot find by order with id: " + boId + " in facade.");
        return this.buyOrders.get(boId);
    }

    public void DeleteBuyOrder(String boId)
    {
        BuyOrder toRemove = FindBuyOrderById(boId);

        boDao.Remove(toRemove.getBuyOrderID());
        this.buyOrders.remove(toRemove.getBuyOrderID());
    }

    public void AddItemToBuyOrder(String boId,String item,Integer amount)
    {
        BuyOrder b = FindBuyOrderById(boId);
        b.AddItemToBO(item,amount);
    }
    public void RemoveItemFromBuyOrder(String boId,String item)
    {
        BuyOrder b = FindBuyOrderById(boId);
        b.RemoveItemFromBO(item);
    }

    public void AddDayToBuyOrder(String boId,DayOfWeek d)
    {
        BuyOrder b = FindBuyOrderById(boId);
        DeliveryDaySchedule temp = b.getRegularDays();
        temp.addDay(d);

        boDao.Update(b.getBuyOrderID(),b.getSupId(),temp.toString(),b.getNextDeliveryDate().toString());
        b.AddRegularDay(d);
    }
    public void RemoveDayFromBuyOrder(String boId,DayOfWeek d)
    {
        BuyOrder b = FindBuyOrderById(boId);
        DeliveryDaySchedule temp = b.getRegularDays();
        temp.removeDay(d);
        if(temp.getDays().isEmpty())
            throw new RuntimeException("Cannot remove day, as the buy order will remain with no regular delivery days.");

        boDao.Update(b.getBuyOrderID(),b.getSupId(),temp.toString(),b.getNextDeliveryDate().toString());
        b.RemoveRegularDay(d);
    }
    public void UpdateItemInBO(String boId,String item,Integer amount)
    {
        BuyOrder b = FindBuyOrderById(boId);
        b.UpdateAmount(item,amount);
    }

    public Report ViewAllBuyOrders()
    {
        Report res = new Report("All Buy Orders report\n");
        res.AddLine("===========================");

        for(Map.Entry<String,BuyOrder> en : this.buyOrders.entrySet())
        {
            res.AddLine(en.getValue().Summary());
            res.AddLine("-----------------------------");
        }
        return res;
    }

    public Report ViewAllBuyOrders(String supId)
    {
        Report res = new Report("All Buy Orders by supplier:" + supId+ " report\n");
        res.AddLine("===========================");

        for(Map.Entry<String,BuyOrder> en : this.buyOrders.entrySet())
        {
            if(en.getValue().getSupId().equals(supId)) {
                res.AddLine(en.getValue().Summary());
                res.AddLine("-----------------------------");
            }
        }
        return res;
    }

    public List<String> CreateOrdersFromBuyOrders()
    {
        List<String> res = new ArrayList<>();
        for(Map.Entry<String,BuyOrder> en : this.buyOrders.entrySet()) {

            BuyOrder b = en.getValue();
            if(LocalDate.now().equals(b.getNextDeliveryDate())) {
                try {
                    res.add(CreateOrder(b.getSupId(), false, b.getItems()));
                    b.ScheduleNextDelivery();
                }catch (Exception e)
                {
                    System.out.println("Cannot create order from BuyOrder:" + b.getBuyOrderID() +" Reason:" + e.getMessage());
                }
            }
        }
        return res;
    }

    public void RemoveAllBOFromSuppliers(String supId)
    {
        for(Map.Entry<String,BuyOrder> en: this.buyOrders.entrySet())
        {
            BuyOrder b = en.getValue();
            if(b.getSupId().equals(supId))
                DeleteBuyOrder(b.getSupId());
        }
    }

    public void Load()
    {
        List<BuyOrderDTO> toLoad = boDao.SelectAll();
        for(BuyOrderDTO b:toLoad)
        {
            this.buyOrders.put(b.buyOrderID,new BuyOrder(b));
        }
    }
}
