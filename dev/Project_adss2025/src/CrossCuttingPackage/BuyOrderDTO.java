package CrossCuttingPackage;
import java.util.HashMap;

public class BuyOrderDTO {

    public String buyOrderID;
    public String supId;
    public HashMap<String,Integer> items;
    public String regularDays;
    public String nextDeliveryDate;

    public BuyOrderDTO(String boId,String supId,HashMap<String,Integer> items,String regularDays, String ndd)
    {
        this.buyOrderID = boId;
        this.supId = supId;
        this.items = items;
        this.regularDays = regularDays;
        this.nextDeliveryDate = ndd;
    }
}
