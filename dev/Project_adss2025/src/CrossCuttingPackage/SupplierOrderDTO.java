package CrossCuttingPackage;

import SupplierModule.DomainLayer.SupplierOrder;

import java.time.LocalDate;
import java.util.List;

public class SupplierOrderDTO {

    public String orderId;
    public String supplierId;
    public LocalDate orderDate;
    public List<SupplierItemDTO> items;
    public SupplierOrder.OrderStatus status;

    public SupplierOrderDTO(String o,String s,LocalDate l,List<SupplierItemDTO> items,SupplierOrder.OrderStatus status)
    {
        this.orderId =o;
        this.supplierId = s;
        this.orderDate =l;
        this.items = items;
        this.status =status;
    }
}
