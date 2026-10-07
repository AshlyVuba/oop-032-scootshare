package za.co.wethinkcode.scootshare.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchema {

    private DatabaseSchema() {
    }

    public static void createSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // TODO (Q6.2): create the `riders`, `scooters` and `trips` tables
            // described in resources/erd.png, including every key and
            // constraint listed there. Create them in an order that lets the
            // foreign keys resolve.
        }
    }
}
