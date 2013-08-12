package BitemporalSpace.Booking;

import org.joda.time.LocalDate;

import java.util.Date;

// Named after QuartetFS brochure ware and white paper

public class LifecycleEvent {
    final private Object[] positions;
    final private LocalDate date;
    final private String name;

    public LifecycleEvent(final LocalDate date, final String name, final Object[] positions) {
        this.date = date;
        this.positions = positions;
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public Object[] positions() {
        return positions;
    }

    @Override
    public String toString() {
        return name + this.hashCode();
    }
}
