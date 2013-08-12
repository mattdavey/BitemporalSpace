package BitemporalSpace.Pricing;

import BitemporalSpace.Booking.Cashflow;
import BitemporalSpace.Market.MarketEnvironment;
import org.joda.time.LocalDate;

import java.util.Calendar;

public class InterestRateSwap extends Instrument {

    public InterestRateSwap(final Object[] args) {
        // Assume fixed and floating legs, 6mth payments

        //Pay	    Fixed	   -100	    5.00%   6m	6m	01/01/2010	01/01/2015	30/360
        //Receive	Floating	100	USD Libor	6m	6m	01/01/2010	01/01/2015	A/360

        // Calculate cashflow dates
        LocalDate date = MarketEnvironment.getInstance().getTradeDate();
        
        for (int i=2010; i <= 2015; i++) {
            date = date.plusMonths(6);
            super.addEconomicEvent(date, "Cashflow " + i, new Object[]{new Cashflow("Fixed " + i), new Cashflow("Floating " + i)});
        }

    }

    @Override
    public double value() {
        // Has any dependency changed?  If not, then return cached value, else re-calculate

        // Calculation

        // Get curves - yield and forecast

        // Calculate cashflows

        return 0;
    }
}
