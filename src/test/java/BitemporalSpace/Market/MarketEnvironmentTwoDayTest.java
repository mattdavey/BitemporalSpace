package BitemporalSpace.Market;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MarketEnvironmentTwoDayTest {
    @Before
    public void setup() {
        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,1));
        MarketEnvironment.getInstance().setLocation("London");

        final String[] cashTerms = new String[]{ "1W", "2W", "1M", "2M" };
        final double[] cashGBPRates = new double[]{ 5.11, 5.25, 5.13, 5.32 };
        final double[] cashEURRates = new double[]{ 4.11, 4.25, 4.13, 4.32 };

        MarketEnvironment.getInstance().store("curve:://USDGBP", new Curve("USDGBP", cashTerms, cashGBPRates));

        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,2));
        MarketEnvironment.getInstance().store("curve:://EURGBP", new Curve("EURGBP", cashTerms, cashEURRates));
    }

    @Test
    public void testFindCurvesOnWrongDay() {
        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2012,12,3));

        final Curve eurgbp = (Curve)MarketEnvironment.getInstance().get("curve:://EURGBP");
        Assert.assertNull("EURGBP curve found", eurgbp);
        final Curve usdgbp = (Curve)MarketEnvironment.getInstance().get("curve:://USDGBP");
        Assert.assertNull("USDGBP curve found", usdgbp);
    }

    @Test
    public void testFindCurvesOnCorrectDay() {
        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,1));

        final Curve eurgbp = (Curve)MarketEnvironment.getInstance().get("curve:://USDGBP");
        Assert.assertNotNull("No USDGBP curve found", eurgbp);

        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,2));
        final Curve usdgbp = (Curve)MarketEnvironment.getInstance().get("curve:://EURGBP");
        Assert.assertNotNull("No EURGBP curve found", usdgbp);
    }

    @After
    public void tearDown() {
        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2012,12,1));
        MarketEnvironment.getInstance().delete("curve:://USDGBP");
        Object obj = MarketEnvironment.getInstance().get("curve:://USDGBP");
        Assert.assertTrue("Curve not delete correctly", obj == null);

        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2012,12,2));
        MarketEnvironment.getInstance().delete("curve:://EURGBP");
        Object obj2 = MarketEnvironment.getInstance().get("curve:://EURGBP");
        Assert.assertTrue("Curve not delete correctly", obj2 == null);
    }
}
