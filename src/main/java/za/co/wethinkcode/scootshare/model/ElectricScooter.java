package za.co.wethinkcode.scootshare.model;

/** A scooter in the ScootShare fleet. Provided; do not change. */
public abstract class ElectricScooter {

    private final String id;
    private final int batteryPercent;

    protected ElectricScooter(String id, int batteryPercent) {
        if (batteryPercent < 0 || batteryPercent > 100) {
            throw new IllegalArgumentException("Battery must be between 0 and 100");
        }
        this.id = id;
        this.batteryPercent = batteryPercent;
    }

    public String getId() {
        return id;
    }

    public int getBatteryPercent() {
        return batteryPercent;
    }

    /** The fastest this kind of scooter will go. */
    public abstract double maxSpeedKmh();

    /** Percent of battery used per kilometre travelled. */
    public abstract double batteryDrainPerKm();

    /** How far the scooter can still travel on its current charge. */
    public double rangeKm() {
        return batteryPercent / batteryDrainPerKm();
    }

    public String describe() {
        return getClass().getSimpleName() + " " + id + " battery=" + batteryPercent + "%";
    }
}
