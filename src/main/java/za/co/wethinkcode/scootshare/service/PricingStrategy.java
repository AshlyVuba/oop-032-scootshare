package za.co.wethinkcode.scootshare.service;

/** One way of charging for a trip. Provided; do not change. */
public interface PricingStrategy {

    /** The fare in rand for a trip of the given length. */
    double priceFor(int minutes);

    /** The plan name, e.g. "PAY_PER_MINUTE". */
    String name();
}
