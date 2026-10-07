package za.co.wethinkcode.scootshare.service;

public class BatteryService {

    /**
     * Status of a scooter for the operations dashboard.
     *
     * CHARGING    - on the charger, whatever the level
     * IN_USE_LOW  - being ridden with under 10%
     * IN_USE      - being ridden
     * DEAD        - empty
     * LOW         - under 20%
     * OK          - under 100%
     * FULL        - 100%
     */
    public String statusOf(int battery, boolean charging, boolean inUse) {
        String result;
        if (charging) {
            result = "CHARGING";
        } else {
            if (inUse) {
                if (battery < 10) {
                    result = "IN_USE_LOW";
                } else {
                    result = "IN_USE";
                }
            } else {
                if (battery <= 0) {
                    result = "DEAD";
                } else {
                    if (battery < 20) {
                        result = "LOW";
                    } else {
                        if (battery < 100) {
                            result = "OK";
                        } else {
                            result = "FULL";
                        }
                    }
                }
            }
        }
        return result;
    }
}
