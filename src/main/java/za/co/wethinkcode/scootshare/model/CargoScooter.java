package za.co.wethinkcode.scootshare.model;

public class CargoScooter extends ElectricScooter {

    public CargoScooter(String id, int batteryPercent) {
        super(id, batteryPercent);
    }

    // TODO (Q3.3): complete this class as described in the README.

    @Override
    public double maxSpeedKmh() {
        return 0;
    }

    @Override
    public double batteryDrainPerKm() {
        return 1.0;
    }

    public int maxLoadKg() {
        return 0;
    }
}
