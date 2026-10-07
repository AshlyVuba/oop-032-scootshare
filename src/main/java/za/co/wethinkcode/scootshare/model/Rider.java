package za.co.wethinkcode.scootshare.model;

import java.util.ArrayList;
import java.util.List;

/** A registered ScootShare rider. */
public class Rider {

    public String name;
    public String email;
    public List<String> tripIds = new ArrayList<>();

    public Rider(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getTripIds() {
        return tripIds;
    }

    public void addTrip(String tripId) {
        tripIds.add(tripId);
    }
}
