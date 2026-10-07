package za.co.wethinkcode.scootshare.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class ElectricScooterTest {

    @Test
    void standardScooterRangeIsBatteryDividedByDrain() {
        ElectricScooter scooter = new StandardScooter("S1", 80);
        assertThat(scooter.rangeKm()).isCloseTo(32.0, within(0.001));
        assertThat(scooter.maxSpeedKmh()).isCloseTo(25.0, within(0.001));
    }

    @Test
    void batteryMustBeBetweenZeroAndOneHundred() {
        assertThatThrownBy(() -> new StandardScooter("S1", 101)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StandardScooter("S1", -1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(new StandardScooter("S1", 0).getBatteryPercent()).isZero();
        assertThat(new StandardScooter("S1", 100).getBatteryPercent()).isEqualTo(100);
    }

    @Test
    void describeListsTypeIdAndBattery() {
        assertThat(new StandardScooter("S1", 80).describe()).isEqualTo("StandardScooter S1 battery=80%");
    }
}
