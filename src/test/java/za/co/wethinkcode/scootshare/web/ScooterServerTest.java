package za.co.wethinkcode.scootshare.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.scootshare.model.StandardScooter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/** Exercises the API over a real HTTP connection on a free port. */
class ScooterServerTest {

    private ScooterServer server;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        ScooterStore store = new ScooterStore();
        store.add(new StandardScooter("S1", 80));
        store.add(new StandardScooter("S2", 15));
        server = new ScooterServer(0, new ScooterApi(store));
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    private HttpResponse<String> send(String method, String pathAndQuery) throws Exception {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("http://localhost:" + server.getPort() + pathAndQuery))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void queryStringsReachTheApi() throws Exception {
        HttpResponse<String> response = send("GET", "/scooters?minBattery=50");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("S1").doesNotContain("S2");
        assertThat(response.headers().firstValue("Content-Type")).contains("application/json");
    }

    @Test
    void unlockThenLockOverHttp() throws Exception {
        assertThat(send("POST", "/scooters/S1/unlock").statusCode()).isEqualTo(200);
        assertThat(send("POST", "/scooters/S1/unlock").statusCode()).isEqualTo(409);
        assertThat(send("POST", "/scooters/S1/lock").statusCode()).isEqualTo(200);
    }

    @Test
    void lowBatteryAnd404OverHttp() throws Exception {
        assertThat(send("POST", "/scooters/S2/unlock").statusCode()).isEqualTo(422);
        assertThat(send("GET", "/scooters/NOPE").statusCode()).isEqualTo(404);
    }
}
