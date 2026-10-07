package za.co.wethinkcode.scootshare.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BatteryServiceTest {

    private BatteryService service;

    @BeforeEach
    void setUp() {
        service = new BatteryService();
    }

    @Test
    void chargingWinsOverEverythingElse() {
        assertThat(service.statusOf(0, true, false)).isEqualTo("CHARGING");
        assertThat(service.statusOf(100, true, true)).isEqualTo("CHARGING");
    }

    @Test
    void aScooterBeingRiddenIsInUse() {
        assertThat(service.statusOf(50, false, true)).isEqualTo("IN_USE");
        assertThat(service.statusOf(10, false, true)).isEqualTo("IN_USE");
    }

    @Test
    void aScooterBeingRiddenWithUnderTenPercentIsInUseLow() {
        assertThat(service.statusOf(9, false, true)).isEqualTo("IN_USE_LOW");
        assertThat(service.statusOf(0, false, true)).isEqualTo("IN_USE_LOW");
    }

    @Test
    void anEmptyParkedScooterIsDead() {
        assertThat(service.statusOf(0, false, false)).isEqualTo("DEAD");
    }

    @Test
    void underTwentyPercentParkedIsLow() {
        assertThat(service.statusOf(1, false, false)).isEqualTo("LOW");
        assertThat(service.statusOf(19, false, false)).isEqualTo("LOW");
    }

    @Test
    void twentyToNinetyNineParkedIsOk() {
        assertThat(service.statusOf(20, false, false)).isEqualTo("OK");
        assertThat(service.statusOf(99, false, false)).isEqualTo("OK");
    }

    @Test
    void exactlyOneHundredParkedIsFull() {
        assertThat(service.statusOf(100, false, false)).isEqualTo("FULL");
    }
}
