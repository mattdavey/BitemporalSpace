package BitemporalSpace.Booking;

import java.util.ArrayList;
import java.util.List;

public class Book {
    final private List<Deal> deals = new ArrayList<Deal>();
    final private String name;

    public Book(String name) {

        this.name = name;
    }

    public void addDeal(Deal deal) {
        deals.add(deal);
    }

    public void MarkToMarket() {
        for (final Deal deal : deals) {
            deal.value();
        }
    }

    public int size() {
        return deals.size();
    }

    public double value() {
        double total=0.0;
        for (final Deal deal : deals) {
            total += deal.value();
        }

        return total;
    }

    @Override
    public String toString() {
        return name + hashCode();
    }
}
