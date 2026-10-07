package za.co.wethinkcode.scootshare.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class PricingStrategiesTest {

    // ---- PayPerMinute: R5.00 to unlock + R1.50 per minute ----

    @Test
    void payPerMinuteIsAPricingStrategyCalledPAY_PER_MINUTE() {
        PricingStrategy strategy = new PayPerMinute();
        assertThat(strategy.name()).isEqualTo("PAY_PER_MINUTE");
    }

    @Test
    void payPerMinuteChargesTheUnlockFeeEvenForAZeroMinuteTrip() {
        assertThat(new PayPerMinute().priceFor(0)).isCloseTo(5.00, within(0.001));
    }

    @Test
    void payPerMinuteAddsOneFiftyPerMinute() {
        assertThat(new PayPerMinute().priceFor(10)).isCloseTo(20.00, within(0.001));
        assertThat(new PayPerMinute().priceFor(1)).isCloseTo(6.50, within(0.001));
    }

    // ---- DayPass: R40.00 flat for up to 240 minutes, R1.00 per extra minute ----

    @Test
    void dayPassIsAPricingStrategyCalledDAY_PASS() {
        assertThat(new DayPass().name()).isEqualTo("DAY_PASS");
    }

    @Test
    void dayPassIsAFlatFeeUpToTheLimit() {
        assertThat(new DayPass().priceFor(0)).isCloseTo(40.00, within(0.001));
        assertThat(new DayPass().priceFor(120)).isCloseTo(40.00, within(0.001));
        assertThat(new DayPass().priceFor(240)).isCloseTo(40.00, within(0.001));
    }

    @Test
    void dayPassChargesOneRandPerMinuteBeyondTheLimit() {
        assertThat(new DayPass().priceFor(241)).isCloseTo(41.00, within(0.001));
        assertThat(new DayPass().priceFor(300)).isCloseTo(100.00, within(0.001));
    }

    // ---- factory ----

    @Test
    void forPlanFindsEachStrategy() {
        assertThat(PricingStrategies.forPlan("PAY_PER_MINUTE")).isInstanceOf(PayPerMinute.class);
        assertThat(PricingStrategies.forPlan("DAY_PASS")).isInstanceOf(DayPass.class);
    }

    @Test
    void forPlanIgnoresCaseAndSurroundingSpaces() {
        assertThat(PricingStrategies.forPlan("  day_pass ")).isInstanceOf(DayPass.class);
    }

    @Test
    void forPlanRejectsUnknownOrMissingPlans() {
        assertThatThrownBy(() -> PricingStrategies.forPlan("FREE_FOREVER"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown plan");
        assertThatThrownBy(() -> PricingStrategies.forPlan(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ---- TripPricer works with any strategy (provided code) ----

    @Test
    void tripPricerDelegatesToTheStrategyItWasGiven() {
        assertThat(new TripPricer(new PayPerMinute()).quote(10)).isCloseTo(20.0, within(0.001));

        PricingStrategy oneRandPerMinute = new PricingStrategy() {
            @Override
            public double priceFor(int minutes) {
                return minutes;
            }

            @Override
            public String name() {
                return "FAKE";
            }
        };
        assertThat(new TripPricer(oneRandPerMinute).quote(7)).isCloseTo(7.0, within(0.001));
    }

    @Test
    void tripPricerRejectsNegativeMinutes() {
        assertThatThrownBy(() -> new TripPricer(new PayPerMinute()).quote(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
