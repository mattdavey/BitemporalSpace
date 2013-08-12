package BitemporalSpace.Booking;


import BitemporalSpace.Market.Curve;
import BitemporalSpace.Market.MarketEnvironment;
import BitemporalSpace.Pricing.FXForward;
import BitemporalSpace.Pricing.InstrumentFactory;
import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class LifecycleEventTest {
    private final LocalDate date = new LocalDate(2012, 12, 1);

    @Before
    public void setup() {
        MarketEnvironment.getInstance().setTradeDate(date);
        MarketEnvironment.getInstance().setLocation("London");

        final String[] cashTerms = new String[]{ "1W", "2W", "1M", "2M" };
        final double[] cashGBPRates = new double[]{ 5.11, 5.25, 5.13, 5.32 };
        final double[] cashEURRates = new double[]{ 4.11, 4.25, 4.13, 4.32 };

        MarketEnvironment.getInstance().store("curve:://EUR", new Curve("EUR", cashTerms, cashEURRates));
        MarketEnvironment.getInstance().store("curve:://GBP", new Curve("GBP", cashTerms, cashGBPRates));
    }

    @Test
    public void testCreate() {
        // Settlement date for FXForward
        final LocalDate newDate = date.plusDays(30);

        // Sales person quotes to client on telephone and then enters order
        // The FXForward should reference the market data appropriately for the calendar date as per above
        final FXForward fwd = (FXForward) InstrumentFactory.create(FXForward.class, "GBPEUR", newDate);
        final LifecycleEvent event = fwd.currentLifecycleEvent();
        Assert.assertTrue("lifecycle event shouldn't exist", event == null);
        Assert.assertTrue("No settlement date", fwd.nextLifecycleEvent().getDate() != null);
        Assert.assertTrue("Incorrect number of Lifecycle events", fwd.eventLifeCycles().length == 1);
    }
}
