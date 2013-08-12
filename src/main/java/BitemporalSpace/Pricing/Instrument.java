package BitemporalSpace.Pricing;

import BitemporalSpace.Booking.LifecycleEvent;
import BitemporalSpace.Market.MarketEnvironment;
import org.joda.time.LocalDate;

import java.util.*;

public abstract class Instrument {
    final private List<LifecycleEvent> economicEvents = new ArrayList<LifecycleEvent>();
    private int notional;

    public Object[] positions() {
        final LifecycleEvent event = currentLifecycleEvent();
        if (event != null)
            return currentLifecycleEvent().positions();

        // Default is that our position is the instrument itself (until its booked possibly)
        return new Object[]{this};
    }

    public LifecycleEvent[] eventLifeCycles() {
        Collections.sort(economicEvents, new Comparator<LifecycleEvent>() {
            @Override
            public int compare(LifecycleEvent lifecycleEvent, LifecycleEvent lifecycleEvent1) {
                return lifecycleEvent.getDate().compareTo(lifecycleEvent1.getDate());
            }
        });

        return economicEvents.toArray(new LifecycleEvent[economicEvents.size()]);
    }

    private LifecycleEvent createEconomicEvent(final LocalDate eventDate, final String name, final Object[] positions) {
        return new LifecycleEvent(eventDate, name, positions);
    }

    public LifecycleEvent addEconomicEvent(final LocalDate eventDate, final String name, final Object[] positions) {
        final LifecycleEvent event = createEconomicEvent(eventDate, name, positions);
        economicEvents.add(event);
        return event;
    }

    abstract public double value();

    public LifecycleEvent currentLifecycleEvent() {
        final LocalDate now = MarketEnvironment.getInstance().getTradeDate();

        LifecycleEvent ret = null;
        for (LifecycleEvent i : economicEvents) {
            if (i.getDate().isAfter(now)) {
                break;
            }
            ret = i;
        }

        return ret;
    }

    public LifecycleEvent nextLifecycleEvent() {
        final LocalDate now = MarketEnvironment.getInstance().getTradeDate();

        LifecycleEvent ret = null;
        for (LifecycleEvent i : economicEvents) {
            if (i.getDate().isAfter(now)) {
                ret = i;
                break;
            }
        }

        return ret;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + this.hashCode();
    }
}
