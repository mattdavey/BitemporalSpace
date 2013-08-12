package BitemporalSpace.Booking;

import BitemporalSpace.Pricing.Instrument;
import BitemporalSpace.Market.MarketEnvironment;

public class Deal {
    final private Instrument instrument;
    final private int notional;

    public Deal(final Instrument instrument, final int notional) {
        this.instrument = instrument;
        this.notional = notional;

        // Creation
        instrument.addEconomicEvent(MarketEnvironment.getInstance().getTradeDate(), "Create", new Object[]{this});
    }

    public double value() {
        return instrument.value();
    }

    public void addLifecycleEvent(final String functor) {
        instrument.addEconomicEvent(MarketEnvironment.getInstance().getTradeDate(), functor, null);
    }

    public LifecycleEvent[] eventLifeCycles() {
        return instrument.eventLifeCycles();
    }
}
