package SupplierModule.DomainLayer;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

    //Assumes string format is in day1,day2....
    public DeliveryDaySchedule(String ddc)
    {
        List<DayOfWeek> res = new ArrayList<>();
        if(!ddc.isEmpty()) {
            String[] days = ddc.split(",");
            for (int i = 0; i < days.length; i++) {
                res.add(DayOfWeek.valueOf(days[i]));
            }
        }
        this.deliveryDays=res;
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
        this.deliveryDays.add(d);
    }
    public void removeDay(DayOfWeek d)
    {
        if(!this.deliveryDays.contains(d))
            throw new RuntimeException("Day " + d.toString() +" is not included in the schedule instance.");

        this.deliveryDays.remove(d);
    }
    //getter
    public List<DayOfWeek> getDays() {return new ArrayList<>(deliveryDays);}

    public String toString()
    {
        String res= "";
        for (DayOfWeek d:this.deliveryDays)
            res+=d.toString()+',';
        return res.isEmpty() ? res : res.substring(0, res.length() - 1); //removes last ,
    }

    public LocalDate ComputeNextOrderDate()
    {
        if(deliveryDays.isEmpty())
            throw new RuntimeException("Cannot compute next delivery date, as the list of days is empty.");
        LocalDate date = LocalDate.now().plusDays(1);

        while (!this.deliveryDays.contains(date.getDayOfWeek())) {
            date = date.plusDays(1);
        }

        return date;
    }


}
