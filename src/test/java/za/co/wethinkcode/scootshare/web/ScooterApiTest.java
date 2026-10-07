package za.co.wethinkcode.scootshare.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.scootshare.model.CargoScooter;
import za.co.wethinkcode.scootshare.model.StandardScooter;

import static org.assertj.core.api.Assertions.assertThat;

class ScooterApiTest {

    private ScooterStore store;
    private ScooterApi api;

    @BeforeEach
    void setUp() {
        store = new ScooterStore();
        store.add(new StandardScooter("S1", 80));
        store.add(new StandardScooter("S2", 15));
        store.add(new CargoScooter("C1", 50));
        api = new ScooterApi(store);
    }

    // ---- GET /scooters ----

    @Test
    void listReturnsEveryScooterAsAJsonArray() {
        ApiResponse response = api.route("GET", "/scooters", null);

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.body()).startsWith("[").endsWith("]")
                .contains("\"id\":\"S1\"").contains("\"id\":\"S2\"").contains("\"id\":\"C1\"");
    }

    @Test
    void listRendersEachScooterWithModelBatteryAndInUse() {
        ApiResponse response = api.route("GET", "/scooters", null);

        assertThat(response.body())
                .contains("{\"id\":\"S1\",\"model\":\"StandardScooter\",\"battery\":80,\"inUse\":false}")
                .contains("{\"id\":\"C1\",\"model\":\"CargoScooter\",\"battery\":50,\"inUse\":false}");
    }

    @Test
    void listIsInStoreOrder() {
        String body = api.route("GET", "/scooters", null).body();

        assertThat(body.indexOf("S1")).isLessThan(body.indexOf("S2"));
        assertThat(body.indexOf("S2")).isLessThan(body.indexOf("C1"));
    }

    @Test
    void minBatteryFiltersTheList() {
        ApiResponse response = api.route("GET", "/scooters", "minBattery=50");

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.body()).contains("S1").contains("C1").doesNotContain("S2");
    }

    @Test
    void minBatteryWithNoMatchesGivesAnEmptyArray() {
        assertThat(api.route("GET", "/scooters", "minBattery=99").body()).isEqualTo("[]");
    }

    @Test
    void minBatteryMustBeAWholeNumber() {
        ApiResponse response = api.route("GET", "/scooters", "minBattery=lots");

        assertThat(response.status()).isEqualTo(400);
        assertThat(response.body()).contains("\"error\":\"minBattery must be a whole number\"");
    }

    @Test
    void otherQueryParametersAreIgnored() {
        assertThat(api.route("GET", "/scooters", "colour=red").body()).contains("S1").contains("S2");
    }

    // ---- GET /scooters/{id} ----

    @Test
    void getReturnsOneScooter() {
        ApiResponse response = api.route("GET", "/scooters/S1", null);

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.body())
                .isEqualTo("{\"id\":\"S1\",\"model\":\"StandardScooter\",\"battery\":80,\"inUse\":false}");
    }

    @Test
    void getReturns404ForAnUnknownScooter() {
        ApiResponse response = api.route("GET", "/scooters/NOPE", null);

        assertThat(response.status()).isEqualTo(404);
        assertThat(response.body()).contains("\"error\":\"Scooter not found\"");
    }

    // ---- unlock ----

    @Test
    void unlockMarksTheScooterInUse() {
        ApiResponse response = api.route("POST", "/scooters/S1/unlock", null);

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.body()).contains("\"id\":\"S1\"").contains("\"inUse\":true");
        assertThat(store.isInUse("S1")).isTrue();
    }

    @Test
    void unlockingTwiceGives409() {
        api.route("POST", "/scooters/S1/unlock", null);

        ApiResponse response = api.route("POST", "/scooters/S1/unlock", null);

        assertThat(response.status()).isEqualTo(409);
        assertThat(response.body()).contains("\"error\":\"Scooter already in use\"");
    }

    @Test
    void unlockWithALowBatteryGives422AndDoesNotUnlock() {
        ApiResponse response = api.route("POST", "/scooters/S2/unlock", null);

        assertThat(response.status()).isEqualTo(422);
        assertThat(response.body()).contains("\"error\":\"Battery too low\"");
        assertThat(store.isInUse("S2")).isFalse();
    }

    @Test
    void anExactlyTwentyPercentBatteryCanBeUnlocked() {
        store.add(new StandardScooter("S20", 20));

        assertThat(api.route("POST", "/scooters/S20/unlock", null).status()).isEqualTo(200);
    }

    @Test
    void unlockChecksInUseBeforeBattery() {
        store.setInUse("S2", true);

        assertThat(api.route("POST", "/scooters/S2/unlock", null).status()).isEqualTo(409);
    }

    @Test
    void unlockReturns404ForAnUnknownScooter() {
        ApiResponse response = api.route("POST", "/scooters/NOPE/unlock", null);

        assertThat(response.status()).isEqualTo(404);
        assertThat(response.body()).contains("\"error\":\"Scooter not found\"");
    }

    // ---- lock ----

    @Test
    void lockFreesAScooterThatWasInUse() {
        api.route("POST", "/scooters/S1/unlock", null);

        ApiResponse response = api.route("POST", "/scooters/S1/lock", null);

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.body()).contains("\"inUse\":false");
        assertThat(store.isInUse("S1")).isFalse();
    }

    @Test
    void lockingAScooterThatIsNotInUseGives409() {
        ApiResponse response = api.route("POST", "/scooters/S1/lock", null);

        assertThat(response.status()).isEqualTo(409);
        assertThat(response.body()).contains("\"error\":\"Scooter is not in use\"");
    }

    @Test
    void lockReturns404ForAnUnknownScooter() {
        assertThat(api.route("POST", "/scooters/NOPE/lock", null).status()).isEqualTo(404);
    }

    @Test
    void theListShowsInUseAfterUnlock() {
        api.route("POST", "/scooters/S1/unlock", null);

        assertThat(api.route("GET", "/scooters/S1", null).body()).contains("\"inUse\":true");
    }

    // ---- fallbacks ----

    @Test
    void knownPathsWithTheWrongMethodGive405() {
        assertThat(api.route("POST", "/scooters", null).status()).isEqualTo(405);
        assertThat(api.route("DELETE", "/scooters/S1", null).status()).isEqualTo(405);
        assertThat(api.route("GET", "/scooters/S1/unlock", null).status()).isEqualTo(405);
        assertThat(api.route("GET", "/scooters/S1/lock", null).status()).isEqualTo(405);
    }

    @Test
    void theWrongMethodBodyNamesTheProblem() {
        assertThat(api.route("DELETE", "/scooters/S1", null).body())
                .contains("\"error\":\"Method not allowed\"");
    }

    @Test
    void unknownPathsGive404() {
        assertThat(api.route("GET", "/riders", null).status()).isEqualTo(404);
        assertThat(api.route("GET", "/", null).status()).isEqualTo(404);
        ApiResponse response = api.route("POST", "/scooters/S1/explode", null);
        assertThat(response.status()).isEqualTo(404);
        assertThat(response.body()).contains("\"error\":\"Not found\"");
    }
}
