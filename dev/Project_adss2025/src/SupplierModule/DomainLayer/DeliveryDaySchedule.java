package SupplierModule.DomainLayer;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeliveryDaySchedule {


    private List<DayOfWeek> deliveryDays;

    /*
    Invariant: deliveryDays != null && deliveryDays.size() <=7 && deliveryDays.size() >=0
    For 0<= i <=6 deliverDays.get(i) != deliveryDays.get(i+1).
     */
    public DeliveryDaySchedule(List<DayOfWeek> dd)
    {
        if(!isLegalList(dd))
            throw new IllegalArgumentException("Cannot create DeliveryDateSchedule instance with a null list, or a day occurs twice.");

        this.deliveryDays = dd;
    }

    /*
    Helper method that helps us ensure that the DeliveryDateSchedule is constructed according to invariant.
     */
    private Boolean isLegalList(List<DayOfWeek> l)
    {
        HashMap<DayOfWeek,Integer> temp = new HashMap<>();
        if(l == null || l.size() > 7)
           return false;
        for(DayOfWeek d:l) // init temp map
            temp.put(d,0);

        for(DayOfWeek d:l) // checks that a day does not occur twice.
        {
            if(temp.get(d) == 1)
                return false;
            temp.put(d,1);
        }
        return true;
    }

    public void addDay(DayOfWeek d)
    {
        if(this.deliveryDays.contains(d))
            throw new RuntimeException("Day " + d.toString() +" is already included in the schedule instance.");
    }
    public void removeDay(DayOfWeek d)
    {
        if(!this.deliveryDays.contains(d))
            throw new RuntimeException("Day " + d.toString() +" is not included in the schedule instance.");

        this.deliveryDays.remove(d);
    }
    //getter
    public List<DayOfWeek> getDays() {return new ArrayList<>(deliveryDays);}
}
