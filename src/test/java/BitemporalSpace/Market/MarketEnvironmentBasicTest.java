package BitemporalSpace.Market;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MarketEnvironmentBasicTest {
    @Before
    public void setup() {
        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,1));
        MarketEnvironment.getInstance().setLocation("London");

        final String[] cashTerms = new String[]{ "1W", "2W", "1M", "2M" };
        final double[] cashGBPRates = new double[]{ 5.11, 5.25, 5.13, 5.32 };
        MarketEnvironment.getInstance().store("curve:://EURGBP", new Curve("EURGBP", cashTerms, cashGBPRates));
    }

    @Test
    public void testFindCurve() {
        final Curve eurgbp = (Curve)MarketEnvironment.getInstance().get("curve:://EURGBP");
        Assert.assertNotNull("No EURGBP curve found", eurgbp);
        Assert.assertTrue("Wrong curve name", eurgbp.getName() == "EURGBP");
    }

    @After
    public void tearDown() {
        MarketEnvironment.getInstance().delete("curve:://EURGBP");
        Object obj = MarketEnvironment.getInstance().get("curve:://EURGBP");
        Assert.assertTrue("Curve not delete correctly", obj == null);
    }
}
