package BitemporalSpace.Pricing;

import BitemporalSpace.Market.MarketEnvironment;
import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

public class InterestRateSwapTest {
    @Before
    public void setup() {
        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2012,12,1));
        MarketEnvironment.getInstance().setLocation("London");
    }

    @Test
    public void testPrice() {
        final InterestRateSwap swap = (InterestRateSwap) InstrumentFactory.create(InterestRateSwap.class);
        Assert.assertTrue("Incorrect deal", swap.positions().length == 1);
        Assert.assertEquals("Incorrect deal type", swap.positions()[0].getClass(), InterestRateSwap.class);
        Assert.assertTrue("Incorrect first payment cashflow's", swap.nextLifecycleEvent().positions().length == 2);
    }
}
