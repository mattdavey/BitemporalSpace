package BitemporalSpace.Pricing;

import BitemporalSpace.Booking.Cashflow;
import BitemporalSpace.Market.Curve;
import BitemporalSpace.Market.MarketEnvironment;
import BitemporalSpace.model.Currency;
import org.joda.time.LocalDate;

import java.util.Date;

// www.riskworx.com/pdfs/instruments/fxfwds.pdf
public class FXForward extends Instrument  {
    private final Currency domesticCcy;
    private final Currency foreignCcy;
    private final LocalDate settlement;

    public FXForward(final Object[] args) {
        assert args != null : "No arguments passed to FX Forward constructor";
        assert args.length == 2 : "Incorrect number of arguments to FX Forward";
        assert args[0].toString().length() == 6 : "None valid currency pair length";

        final String ccyPair = args[0].toString();
        domesticCcy = new Currency(ccyPair.substring(3));
        foreignCcy = new Currency(ccyPair.substring(3,6));

        settlement = (LocalDate)args[1];

        // Settlement
        super.addEconomicEvent(settlement, "Settlement/Delivery", new Object[]{new Cashflow("Pay"), new Cashflow("Receive")});
    }

    public Curve[] MarketDependencies() {
        final Curve domestic = (Curve) MarketEnvironment.getInstance().get("curve:://" + domesticCcy.getName());
        final Curve foreign = (Curve) MarketEnvironment.getInstance().get("curve:://" + foreignCcy.getName());
        return new Curve[] {domestic, foreign};
    }

    public double value() {
        Double ccy1Time = days(foreignCcy, MarketEnvironment.getInstance().getTradeDate(), settlement) / totalDays(foreignCcy);
        Double ccy2Time = days(domesticCcy, MarketEnvironment.getInstance().getTradeDate(), settlement) / totalDays(domesticCcy);

        final Curve domestic = (Curve) MarketEnvironment.getInstance().get("curve:://" + domesticCcy.getName());
        final Curve foreign = (Curve) MarketEnvironment.getInstance().get("curve:://" + foreignCcy.getName());
        final Double ccy1Rate = domestic.getRate(settlement);
        final Double ccy2Rate = foreign.getRate(settlement);

        double spot=foreign.getRate(MarketEnvironment.getInstance().getTradeDate());
        return spot * (1 + (ccy1Rate * ccy1Time)) / (1 + (ccy2Rate * ccy2Time));
    }

    private double totalDays(Currency ccy) {
        // e.g. 360 or actual?
        return 360;
    }

    private double days(Currency ccy, LocalDate tradeDate, LocalDate forwardDate) {
        // e.g. actual or 30* months ?
        return 30;
    }
}
