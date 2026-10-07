package za.co.wethinkcode.scootshare.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class TripRepositoryTest {

    private Connection connection;
    private TripRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        connection = Database.connect(":memory:");
        DatabaseSchema.createSchema(connection);
        try (Statement s = connection.createStatement()) {
            s.execute("INSERT INTO riders (id, name, email) VALUES (1, 'Zanele Khumalo', 'zanele@mail.co.za')");
            s.execute("INSERT INTO riders (id, name, email) VALUES (2, 'Pieter van Wyk', 'pieter@mail.co.za')");
            s.execute("INSERT INTO riders (id, name, email) VALUES (3, 'Bongani Sithole', 'bongani@mail.co.za')");
            s.execute("INSERT INTO riders (id, name, email) VALUES (4, 'Amahle Zulu', 'amahle@mail.co.za')");

            s.execute("INSERT INTO scooters (id, model, battery_percent) VALUES (1, 'Standard', 80)");
            s.execute("INSERT INTO scooters (id, model, battery_percent) VALUES (2, 'Standard', 60)");
            s.execute("INSERT INTO scooters (id, model, battery_percent) VALUES (3, 'Cargo', 40)");

            s.execute("INSERT INTO trips (rider_id, scooter_id, minutes) VALUES (1, 3, 30)");
            s.execute("INSERT INTO trips (rider_id, scooter_id, minutes) VALUES (1, 1, 10)");
            s.execute("INSERT INTO trips (rider_id, scooter_id, minutes) VALUES (1, 1, 20)");
            s.execute("INSERT INTO trips (rider_id, scooter_id, minutes) VALUES (2, 2, 15)");
            s.execute("INSERT INTO trips (rider_id, scooter_id) VALUES (2, 1)");
        }
        repository = new TripRepository(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void findsEachScooterARiderHasRiddenOnceSmallestFirst() throws SQLException {
        assertThat(repository.findScooterIdsRiddenBy("zanele@mail.co.za")).containsExactly(1, 3);
    }

    @Test
    void doesNotIncludeScootersRiddenByOthers() throws SQLException {
        assertThat(repository.findScooterIdsRiddenBy("pieter@mail.co.za")).containsExactly(1, 2);
    }

    @Test
    void returnsNothingForAnUnknownEmailOrARiderWithNoTrips() throws SQLException {
        assertThat(repository.findScooterIdsRiddenBy("nobody@mail.co.za")).isEmpty();
        assertThat(repository.findScooterIdsRiddenBy("amahle@mail.co.za")).isEmpty();
    }

    @Test
    void treatsTheEmailAsDataNotAsSql() throws SQLException {
        assertThat(repository.findScooterIdsRiddenBy("' OR '1'='1")).isEmpty();
    }

    @Test
    void findsRidersWhoHaveNeverRidden() throws SQLException {
        assertThat(repository.findRidersWithNoTrips()).containsExactly("Amahle Zulu", "Bongani Sithole");
    }

    @Test
    void totalsMinutesPerModelIgnoringTripsInProgress() throws SQLException {
        assertThat(repository.totalMinutesPerModel(0))
                .containsEntry("Standard", 45)
                .containsEntry("Cargo", 30)
                .hasSize(2);
    }

    @Test
    void onlyKeepsModelsThatReachTheMinimum() throws SQLException {
        assertThat(repository.totalMinutesPerModel(40))
                .containsEntry("Standard", 45)
                .hasSize(1);
        assertThat(repository.totalMinutesPerModel(30)).hasSize(2);
        assertThat(repository.totalMinutesPerModel(100)).isEmpty();
    }
}
