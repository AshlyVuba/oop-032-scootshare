package za.co.wethinkcode.scootshare.model;

public class StandardScooter extends ElectricScooter {

    public StandardScooter(String id, int batteryPercent) {
        super(id, batteryPercent);
    }

    @Override
    public double maxSpeedKmh() {
        return 25.0;
    }

    @Override
    public double batteryDrainPerKm() {
        return 2.5;
    }
}
