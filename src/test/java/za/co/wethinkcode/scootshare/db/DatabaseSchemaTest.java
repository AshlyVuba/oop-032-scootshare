package za.co.wethinkcode.scootshare.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseSchemaTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = Database.connect(":memory:");
        DatabaseSchema.createSchema(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    // ---- riders ----

    @Test
    void riders_hasTheExpectedColumnsAndTypes() throws SQLException {
        assertThat(info("riders", "type"))
                .containsEntry("id", "INTEGER")
                .containsEntry("name", "TEXT")
                .containsEntry("email", "TEXT")
                .hasSize(3);
    }

    @Test
    void riders_hasIdAsPrimaryKeyAndRequiredColumns() throws SQLException {
        assertThat(flags("riders", "pk")).containsEntry("id", true);
        assertThat(flags("riders", "notnull")).containsEntry("name", true).containsEntry("email", true);
    }

    @Test
    void riders_emailIsUnique() throws SQLException {
        execute("INSERT INTO riders (name, email) VALUES ('A', 'a@mail.co.za')");

        assertThatThrownBy(() -> execute("INSERT INTO riders (name, email) VALUES ('B', 'a@mail.co.za')"))
                .isInstanceOf(SQLException.class);
    }

    // ---- scooters ----

    @Test
    void scooters_hasTheExpectedColumnsAndTypes() throws SQLException {
        assertThat(info("scooters", "type"))
                .containsEntry("id", "INTEGER")
                .containsEntry("model", "TEXT")
                .containsEntry("battery_percent", "INTEGER")
                .hasSize(3);
    }

    @Test
    void scooters_hasIdAsPrimaryKeyAndRequiredColumns() throws SQLException {
        assertThat(flags("scooters", "pk")).containsEntry("id", true);
        assertThat(flags("scooters", "notnull"))
                .containsEntry("model", true)
                .containsEntry("battery_percent", true);
    }

    @Test
    void scooters_batteryDefaultsToOneHundred() throws SQLException {
        execute("INSERT INTO scooters (model) VALUES ('Standard')");

        assertThat(queryInt("SELECT battery_percent FROM scooters")).isEqualTo(100);
    }

    @Test
    void scooters_batteryMustBeBetweenZeroAndOneHundredInclusive() throws SQLException {
        execute("INSERT INTO scooters (model, battery_percent) VALUES ('Standard', 0)");
        execute("INSERT INTO scooters (model, battery_percent) VALUES ('Standard', 100)");

        assertThatThrownBy(() -> execute("INSERT INTO scooters (model, battery_percent) VALUES ('Standard', 101)"))
                .isInstanceOf(SQLException.class);
        assertThatThrownBy(() -> execute("INSERT INTO scooters (model, battery_percent) VALUES ('Standard', -1)"))
                .isInstanceOf(SQLException.class);
    }

    // ---- trips ----

    @Test
    void trips_hasTheExpectedColumnsAndTypes() throws SQLException {
        assertThat(info("trips", "type"))
                .containsEntry("id", "INTEGER")
                .containsEntry("rider_id", "INTEGER")
                .containsEntry("scooter_id", "INTEGER")
                .containsEntry("minutes", "INTEGER")
                .containsEntry("fare_cents", "INTEGER")
                .hasSize(5);
    }

    @Test
    void trips_hasIdAsPrimaryKey() throws SQLException {
        assertThat(flags("trips", "pk")).containsEntry("id", true);
    }

    @Test
    void trips_minutesIsOptionalBecauseATripMayStillBeInProgress() throws SQLException {
        assertThat(flags("trips", "notnull"))
                .containsEntry("rider_id", true)
                .containsEntry("scooter_id", true)
                .containsEntry("fare_cents", true)
                .containsEntry("minutes", false);
    }

    @Test
    void trips_fareDefaultsToZeroAndMinutesToNull() throws SQLException {
        seedRiderAndScooter();
        execute("INSERT INTO trips (rider_id, scooter_id) VALUES (1, 1)");

        assertThat(queryInt("SELECT fare_cents FROM trips")).isZero();
        assertThat(queryInt("SELECT COUNT(*) FROM trips WHERE minutes IS NULL")).isEqualTo(1);
    }

    @Test
    void trips_minutesCannotBeNegative() throws SQLException {
        seedRiderAndScooter();

        assertThatThrownBy(() -> execute("INSERT INTO trips (rider_id, scooter_id, minutes) VALUES (1, 1, -5)"))
                .isInstanceOf(SQLException.class);
        execute("INSERT INTO trips (rider_id, scooter_id, minutes) VALUES (1, 1, 0)");
    }

    @Test
    void trips_declaresBothForeignKeys() throws SQLException {
        Map<String, String> foreignKeys = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA foreign_key_list(trips)")) {
            while (rs.next()) {
                foreignKeys.put(rs.getString("from"), rs.getString("table") + "." + rs.getString("to"));
            }
        }
        assertThat(foreignKeys)
                .containsEntry("rider_id", "riders.id")
                .containsEntry("scooter_id", "scooters.id")
                .hasSize(2);
    }

    @Test
    void trips_cannotReferenceARiderOrScooterThatDoesNotExist() throws SQLException {
        seedRiderAndScooter();

        assertThatThrownBy(() -> execute("INSERT INTO trips (rider_id, scooter_id) VALUES (99, 1)"))
                .isInstanceOf(SQLException.class);
        assertThatThrownBy(() -> execute("INSERT INTO trips (rider_id, scooter_id) VALUES (1, 99)"))
                .isInstanceOf(SQLException.class);
    }

    // ---- helpers ----

    private void seedRiderAndScooter() throws SQLException {
        execute("INSERT INTO riders (id, name, email) VALUES (1, 'A', 'a@mail.co.za')");
        execute("INSERT INTO scooters (id, model) VALUES (1, 'Standard')");
    }

    private void execute(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private int queryInt(String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            assertThat(rs.next()).isTrue();
            return rs.getInt(1);
        }
    }

    private Map<String, Boolean> flags(String table, String flagColumn) throws SQLException {
        Map<String, Boolean> result = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                result.put(rs.getString("name"), rs.getInt(flagColumn) > 0);
            }
        }
        return result;
    }

    private Map<String, String> info(String table, String column) throws SQLException {
        Map<String, String> result = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                result.put(rs.getString("name"), rs.getString(column).toUpperCase());
            }
        }
        return result;
    }
}
