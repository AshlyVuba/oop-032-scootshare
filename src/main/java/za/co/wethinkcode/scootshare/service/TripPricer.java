package za.co.wethinkcode.scootshare.service;

/** Quotes fares using whichever PricingStrategy it is given. Provided; do not change. */
public class TripPricer {

    private final PricingStrategy strategy;

    public TripPricer(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public double quote(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Trip length cannot be negative");
        }
        return strategy.priceFor(minutes);
    }
}
