package za.co.wethinkcode.scootshare.service;

import za.co.wethinkcode.scootshare.model.ElectricScooter;

import java.util.List;

public class FleetReport {

    /**
     * One-line battery summary of the fleet, for example "low=1, ok=2, full=1".
     * low = under 20%, full = exactly 100%, ok = everything in between.
     */
    public String summary(List<ElectricScooter> fleet) {
        int low = 0;
        for (ElectricScooter scooter : fleet) {
            if (scooter.getBatteryPercent() < 20) {
                low++;
            }
        }
        int full = 0;
        for (ElectricScooter scooter : fleet) {
            if (scooter.getBatteryPercent() == 100) {
                full++;
            }
        }
        int ok = 0;
        for (ElectricScooter scooter : fleet) {
            if (scooter.getBatteryPercent() >= 20 && scooter.getBatteryPercent() < 100) {
                ok++;
            }
        }
        return "low=" + low + ", ok=" + ok + ", full=" + full;
    }
}
