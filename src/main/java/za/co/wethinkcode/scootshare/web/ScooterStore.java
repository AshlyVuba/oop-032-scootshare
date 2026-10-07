package za.co.wethinkcode.scootshare.web;

import za.co.wethinkcode.scootshare.model.ElectricScooter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** In-memory fleet used by the web API. Provided; do not change. */
public class ScooterStore {

    private final Map<String, ElectricScooter> scooters = new LinkedHashMap<>();
    private final Set<String> inUse = new HashSet<>();

    public void add(ElectricScooter scooter) {
        scooters.put(scooter.getId(), scooter);
    }

    public Optional<ElectricScooter> find(String id) {
        return Optional.ofNullable(scooters.get(id));
    }

    /** Every scooter, in the order they were added. */
    public List<ElectricScooter> all() {
        return new ArrayList<>(scooters.values());
    }

    public boolean isInUse(String id) {
        return inUse.contains(id);
    }

    public void setInUse(String id, boolean value) {
        if (value) {
            inUse.add(id);
        } else {
            inUse.remove(id);
        }
    }
}
