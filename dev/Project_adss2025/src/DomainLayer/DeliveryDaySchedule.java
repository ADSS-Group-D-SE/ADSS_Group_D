package DomainLayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the fixed delivery days for suppliers that deliver on a weekly schedule.
 * Days are represented as 1=Sunday through 7=Saturday.
 */
public class DeliveryDaySchedule {

    private List<Integer> deliveryDays;

    public DeliveryDaySchedule() {
        this.deliveryDays = new ArrayList<>();
    }

    public DeliveryDaySchedule(List<Integer> deliveryDays) {
        this();
        if (deliveryDays != null) {
            for (int day : deliveryDays) {
                addDay(day);
            }
        }
    }

    public void addDay(int day) {
        validateDay(day);
        if (!deliveryDays.contains(day)) {
            deliveryDays.add(day);
        }
    }

    public void removeDay(int day) {
        deliveryDays.remove(Integer.valueOf(day));
    }

    public List<Integer> getDays() {
        return new ArrayList<>(deliveryDays);
    }

    public boolean isEmpty() {
        return deliveryDays.isEmpty();
    }

    public boolean containsDay(int day) {
        return deliveryDays.contains(day);
    }

    private void validateDay(int day) {
        if (day < 1 || day > 7) {
            throw new IllegalArgumentException("Day must be between 1 (Sunday) and 7 (Saturday).");
        }
    }

    @Override
    public String toString() {
        return deliveryDays.toString();
    }
}
