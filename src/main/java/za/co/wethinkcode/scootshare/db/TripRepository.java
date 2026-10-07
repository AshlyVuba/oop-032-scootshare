package za.co.wethinkcode.scootshare.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TripRepository {

    private final Connection connection;

    public TripRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * The ids of the scooters the rider with this email has ridden, each id
     * once only, smallest first. Empty if the email is unknown.
     */
    public List<Integer> findScooterIdsRiddenBy(String email) throws SQLException {
        // TODO (Q6.3)
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /** The names of riders who have never taken a trip, in alphabetical order. */
    public List<String> findRidersWithNoTrips() throws SQLException {
        // TODO (Q6.3)
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Total riding minutes per scooter model, keeping only the models whose
     * total is at least {@code minimumMinutes}. Trips still in progress (no
     * minutes recorded yet) add nothing to the total.
     */
    public Map<String, Integer> totalMinutesPerModel(int minimumMinutes) throws SQLException {
        // TODO (Q6.3)
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
