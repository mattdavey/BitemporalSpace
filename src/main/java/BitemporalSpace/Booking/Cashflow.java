package BitemporalSpace.Booking;

public class Cashflow {
    final private String name;

    public Cashflow(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Cashflow " + name + " " + this.hashCode();
    }
}
