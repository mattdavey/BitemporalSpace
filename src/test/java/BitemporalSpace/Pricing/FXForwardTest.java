package BitemporalSpace.Pricing;

import BitemporalSpace.Market.Curve;
import BitemporalSpace.Market.MarketEnvironment;
import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class FXForwardTest {
    @Before
    public void setup() {
        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,1));
        MarketEnvironment.getInstance().setLocation("London");

        final String[] cashTerms = new String[]{ "1W", "2W", "1M", "2M" };
        final double[] cashGBPRates = new double[]{ 5.11, 5.25, 5.13, 5.32 };
        final double[] cashEURRates = new double[]{ 4.11, 4.25, 4.13, 4.32 };

        // Setup curves and market data for date
        MarketEnvironment.getInstance().store("curve:://EUR", new Curve("EUR", cashTerms, cashEURRates));
        MarketEnvironment.getInstance().store("curve:://GBP", new Curve("GBP", cashTerms, cashGBPRates));
    }

    @Test
    public void testCreate() {
        // Sales person quotes to client on telephone and then enters order
        // The FXForward should reference the market data appropriately for the calendar date as per above
        final FXForward fwd = (FXForward) InstrumentFactory.create(FXForward.class, "GBPEUR", new LocalDate(2011,12,30));
        Assert.assertEquals("Incorrect number of Market Dependencies", fwd.MarketDependencies().length, 2);

        Assert.assertTrue("Should be a curve ", fwd.MarketDependencies()[0].getClass().equals(Curve.class));
        Assert.assertTrue("Should be a curve", fwd.MarketDependencies()[1].getClass().equals(Curve.class));
        Assert.assertTrue("Should be a EUR curve", fwd.MarketDependencies()[1].getName().equals("GBP") || fwd.MarketDependencies()[1].getName().equals("EUR"));
        Assert.assertTrue("Should be a EUR curve", fwd.MarketDependencies()[1].getName().equals("EUR") || fwd.MarketDependencies()[1].getName().equals("GBP"));

        Assert.assertEquals("Incorrect Market Dependencies", fwd.MarketDependencies()[0].getClass(), Curve.class);
        Assert.assertEquals("Incorrect Market Dependencies", fwd.MarketDependencies()[1].getClass(), Curve.class);
    }
}
