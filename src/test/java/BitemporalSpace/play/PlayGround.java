package BitemporalSpace.play;

import com.ervacon.bitemporal.Bitemporal;
import com.ervacon.bitemporal.BitemporalWrapper;
import com.ervacon.bitemporal.TimeUtils;
import com.ervacon.bitemporal.WrappedBitemporalProperty;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.out;

abstract class LifeCycleEvent {
    private final DateTime time;

    public LifeCycleEvent(DateTime time2) {
        this.time = time2;
    }

    public abstract void apply(Object object);

    public abstract void undo(Object object);

    public DateTime getTime() {
        return time;
    }

    public abstract String getDescription();

    @Override
    public String toString() {
        return getDescription();
    }
}

abstract class UserEvent extends LifeCycleEvent {
    public UserEvent(DateTime time) {
        super(time);
    }
}

class ChangeNotionalUserEvent extends UserEvent {
    private long newValue;
    private long oldValue;

    public ChangeNotionalUserEvent(DateTime time, long newValue,
                                   long oldValue) {
        super(time);
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    @Override
    public void apply(Object object) {
        PlayDeal deal = (PlayDeal) object;
        deal.quantity = newValue;
    }

    @Override
    public void undo(Object object) {
        PlayDeal deal = (PlayDeal) object;
        deal.quantity = oldValue;
    }

    @Override
    public String getDescription() {
        return "Change Notional from " + oldValue + " to " + newValue;
    }

}

class BitemporalLong extends BitemporalWrapper<Long> {
    private Long value;

    public BitemporalLong(Long value, Interval validityInterval) {
        super(value, validityInterval);
    }

    @Override
    public Long getValue() {
        return value;
    }

    public Bitemporal copyWith(Interval validityInterval) {
        return new BitemporalLong(value, validityInterval);
    }
}

class PlayInstrument
{
    private Collection<BitemporalPlayInstrument> instruments = new LinkedList<BitemporalPlayInstrument>();
}

class PlayDeal
{
    private Collection<BitemporalLong> quantityList = new LinkedList<BitemporalLong>();
    protected Long quantity = 0L;
    
    private Collection<BitemporalPlayInstrument> orders = new LinkedList<BitemporalPlayInstrument>();

    public void setQuantity(Long quantityArg) {
        if (quantityArg != null) {
            getQuantityProperty().set(quantityArg);
        }
    }

    public long getQuantity() {
        return getQuantityProperty().now();
    }

    public WrappedBitemporalProperty<Long> getQuantityProperty() {
        return new WrappedBitemporalProperty<Long>(this.quantityList);
    }

    public void changeQuantity(long newQuantity) {
        // create the event
        ChangeNotionalUserEvent newEvent = new ChangeNotionalUserEvent(TimeUtils.now(),
                newQuantity, quantity);

        // apply
        newEvent.apply(this);

        // store on the timeline
        new WrappedBitemporalProperty<LifeCycleEvent>(this.orders).set(newEvent);

    }

    public List<LifeCycleEvent> getHistoryEvents(DateTime asOf) {
        List<LifeCycleEvent> result = new ArrayList<LifeCycleEvent>();

        for (BitemporalWrapper<LifeCycleEvent> anEvent : new WrappedBitemporalProperty<LifeCycleEvent>(
                this.orders).getHistory(asOf)) {
            result.add(anEvent.getValue());
        }

        return result;
    }
}

public class PlayGround {
    @Test
    public void testDealHistory() {

        PlayDeal deal = new PlayDeal(); 
        TimeUtils.setReference(TimeUtils.day(1, 1, 2011));
        DateTime time = TimeUtils.reference();

        deal.getQuantityProperty().set(100L);

        TimeUtils.setReference(TimeUtils.day(1, 2, 2011));
        time = TimeUtils.reference();

        deal.getQuantityProperty().set(200L);

        TimeUtils.setReference(TimeUtils.day(1, 3, 2011));
        time = TimeUtils.reference();

        deal.getQuantityProperty().set(300L);

        // Now let us try to change the valid value for a particular period
        TimeUtils.setReference(TimeUtils.day(1, 4, 2011));
        time = TimeUtils.reference();

        deal.getQuantityProperty().set(400L, new Interval(TimeUtils.day(1, 1, 2011), TimeUtils.day(1, 2, 2011)));

        // Now let us query the history

        Assert.assertEquals(100L, (long)deal.getQuantityProperty().get(TimeUtils.day(15, 1, 2011), TimeUtils.day(15, 1, 2011)).getValue());

        // in April we thought differently
        Assert.assertEquals(400L, (long)deal.getQuantityProperty().get(TimeUtils.day(15, 1, 2011), TimeUtils.day(15, 4, 2011)).getValue());

        out.println(deal.getQuantityProperty().getHistory());

        out.println("-----------------------");

        // get the history as of a previous moment in time
        // out.println(deal.getQuantityProperty().getHistory(day(15, 2, 2011)));

        out.println("-----------------------");

        out.println(deal.getQuantityProperty().getEvolution());

        assert (true);
    }

    @Test
    public void testChangeDealNotional() {
        final PlayDeal deal = new PlayDeal();

        // set quantity many times with the same validity level
        TimeUtils.setReference(TimeUtils.day(1, 1, 2011));
        DateTime time = TimeUtils.reference();
        System.out.println("Start Time: " + time.toString("dd MM yyyy"));

        deal.changeQuantity(100L);

        TimeUtils.setReference(TimeUtils.day(1, 1, 2011));
        time = TimeUtils.reference();

        deal.changeQuantity(200L);

        TimeUtils.setReference(TimeUtils.day(2, 1, 2011));
        time = TimeUtils.reference();

        deal.changeQuantity(300L);

        TimeUtils.setReference(TimeUtils.day(3, 1, 2011));
        time = TimeUtils.reference();

        // make an amendment back in time as 2nd quantity amended was wrong

        deal.getQuantityProperty().set(500L);

        out.println(deal.getHistoryEvents(TimeUtils.now()));

        out.println("---------Event lifecycle on 15 Feb ------------");

        out.println(deal.getHistoryEvents(TimeUtils.day(15, 2, 2011)));

        //The challenge is now to correct the first changeNotionalEvent()
        assert (true);
    }
}
