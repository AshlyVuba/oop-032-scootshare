package za.co.wethinkcode.scootshare.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CargoScooterTest {

    @Test
    void cargoScooterIsAnElectricScooter() {
        assertThat(new CargoScooter("C1", 50)).isInstanceOf(ElectricScooter.class);
    }

    @Test
    void cargoScootersAreSlowerThanStandardOnes() {
        assertThat(new CargoScooter("C1", 50).maxSpeedKmh()).isCloseTo(15.0, within(0.001));
    }

    @Test
    void cargoScootersDrainTheBatteryFaster() {
        assertThat(new CargoScooter("C1", 50).batteryDrainPerKm()).isCloseTo(4.0, within(0.001));
    }

    @Test
    void rangeUsesTheCargoDrainRate() {
        ElectricScooter scooter = new CargoScooter("C1", 80);
        assertThat(scooter.rangeKm()).isCloseTo(20.0, within(0.001));
    }

    @Test
    void maxLoadIsOneHundredKilograms() {
        assertThat(new CargoScooter("C1", 50).maxLoadKg()).isEqualTo(100);
    }

    @Test
    void describeExtendsTheInheritedDescription() {
        ElectricScooter scooter = new CargoScooter("C1", 80);
        assertThat(scooter.describe())
                .isEqualTo("CargoScooter C1 battery=80% (cargo, max load 100 kg)");
    }

    @Test
    void theBatteryRuleIsStillEnforcedThroughTheBaseClass() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> new CargoScooter("C1", 150))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
