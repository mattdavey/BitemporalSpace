package BitemporalSpace.Booking;

import BitemporalSpace.Pricing.Instrument;

import java.util.HashMap;

public class BookingRepository {
    static private HashMap<String, Book> books = new HashMap<String, Book>();

    public static Book getBook(final String bookName) {
        if (books.containsKey(bookName)) {
            return books.get(bookName);
        }
        
        final Book newBook = new Book(bookName);
        books.put(bookName, newBook);
        return newBook;
    }

    public static Deal create(final Instrument instrument, final int notional) {
        return new Deal(instrument, notional);
    }
}
