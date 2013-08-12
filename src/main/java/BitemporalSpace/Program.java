package BitemporalSpace;

import BitemporalSpace.Booking.Book;
import BitemporalSpace.Booking.BookingRepository;
import BitemporalSpace.Booking.Deal;
import BitemporalSpace.Booking.LifecycleEvent;
import BitemporalSpace.Market.Curve;
import BitemporalSpace.Market.MarketEnvironment;
import BitemporalSpace.Pricing.FXForward;
import BitemporalSpace.Pricing.InstrumentFactory;
import BitemporalSpace.Pricing.InterestRateSwap;
import org.joda.time.LocalDate;

import java.util.Arrays;

public class Program {

    public static final String TRADE_BOOK_LONDON_STERLING = "TradeBook-London-Sterling";

    public static void main(String[] args) {
        setupMarketEnvironment();

        testFXForward();
        testIterestRateSwap();
        testMarkToMarket();
    }

    private static void testMarkToMarket() {
        final Book book1 = BookingRepository.getBook(TRADE_BOOK_LONDON_STERLING);
        System.out.println("No of deals in book: " + book1.size());

        book1.MarkToMarket();
        System.out.println("Book value: " + book1.value());
    }

    private static void testIterestRateSwap() {
        final InterestRateSwap swap = (InterestRateSwap) InstrumentFactory.create(InterestRateSwap.class);
        System.out.println("Value: " + swap.value());
        System.out.println("Position: " + Arrays.toString(swap.positions()));
        System.out.println("1st payment Date: " + swap.nextLifecycleEvent().getDate());

        final LifecycleEvent[] events = swap.eventLifeCycles();
        for (LifecycleEvent event : events) {
            System.out.println("Date: " + Arrays.toString(event.positions()));
        }

        System.out.println("Date of 2nd payment:" + events[1].getDate());

        // As per wording of deal (http://www.lmaxtrader.co.uk/trading-platforms/web-platform/deal-tickets)
        final Deal deal = BookingRepository.create(swap, 100);

        final Book book1 = BookingRepository.getBook(TRADE_BOOK_LONDON_STERLING);
        book1.addDeal(deal);
        
        System.out.println("No of deals in book: " + book1.size());
    }

    private static void setupMarketEnvironment() {
        // Set the date on our Market environment
        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,1));
        MarketEnvironment.getInstance().setLocation("London");

        final String[] cashTerms = new String[]{ "1W", "2W", "1M", "2M" };
        final double[] cashGBPRates = new double[]{ 5.11, 5.25, 5.13, 5.32 };
        final double[] cashEURRates = new double[]{ 4.11, 4.25, 4.13, 4.32 };

        // Setup curves and market data for date
        MarketEnvironment.getInstance().store("curve:://EUR", new Curve("EUR", cashTerms, cashEURRates));
        MarketEnvironment.getInstance().store("curve:://GBP", new Curve("GBP", cashTerms, cashGBPRates));

        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,3));

        final double[] cashGBPRates2 = new double[]{ 5.10, 5.23, 5.15, 5.33 };
        final double[] cashEURRates2 = new double[]{ 4.12, 4.23, 4.15, 4.35 };

        // Setup curves and market data for date
        MarketEnvironment.getInstance().store("curve:://EUR", new Curve("EUR", cashTerms, cashEURRates2));
        MarketEnvironment.getInstance().store("curve:://GBP", new Curve("GBP", cashTerms, cashGBPRates2));

        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,1));
    }

    private static void testFXForward() {
        // Sales person quotes to client on telephone and then enters order
        // The FXForward should reference the market data appropriately for the calendar date as per above
        final FXForward fwd = (FXForward) InstrumentFactory.create(FXForward.class, "GBPEUR", new LocalDate(2011,12,30));
        System.out.println("Market Dependencies: " + Arrays.toString(fwd.MarketDependencies()));  // Curves to be displayed ideally
        System.out.println("Value: " + fwd.value());
        System.out.println("Position: " + Arrays.toString(fwd.positions()));
        System.out.println("Settlement Date: " + fwd.nextLifecycleEvent().getDate());
        System.out.println("Settlement Position: " + Arrays.toString(fwd.nextLifecycleEvent().positions()));  // Display an instantiation of the instrument (positions)

        // As per wording of deal (http://www.lmaxtrader.co.uk/trading-platforms/web-platform/deal-tickets)
        final Deal deal = BookingRepository.create(fwd, 2000);

        // Add to book for accounting - double booking????
        final Book book1 = BookingRepository.getBook(TRADE_BOOK_LONDON_STERLING);
        book1.addDeal(deal);

        // Same day, TA does amend
        deal.addLifecycleEvent("updateQuantity=3000");  // this is why we need a dynamic language

        // Has lifecycle been captured
        System.out.println();
        System.out.println("Lifecycle events for deal:");
        final LifecycleEvent[] events = deal.eventLifeCycles();
        for (LifecycleEvent event : events) {
            System.out.print("\t");
            System.out.println(event);
        }

        // Jump to delivery event
        System.out.println();
        final LifecycleEvent deliveryEvent = events[events.length-1];
        System.out.println("Settlement Position: " + Arrays.toString(deliveryEvent.positions()));  // Display an instantiation of the instrument (positions)

        // Move forwards a day or so
        System.out.println();
        MarketEnvironment.getInstance().setTradeDate(new LocalDate(2011,12,3));
        System.out.println("Value: " + deal.value());   //
    }
}
