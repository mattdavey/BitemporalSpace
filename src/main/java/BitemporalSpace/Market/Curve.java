package BitemporalSpace.Market;

import org.joda.time.LocalDate;

import java.util.Random;

public class Curve {
    private final String name;
    private final String[] cashTerms;
    private final double[] cashRates;

    public Curve(final String name, String[] cashTerms, double[] cashRates) {

        this.name = name;
        this.cashTerms = cashTerms;
        this.cashRates = cashRates;
    }

    public String getName() {
        return name;
    }

    public Double getRate(LocalDate date) {
        return cashRates[2];
    }
}
