package za.co.wethinkcode.scootshare.web;

/**
 * The routing logic of the ScootShare web API, free of any networking so it
 * can be tested directly. ScooterServer connects it to real HTTP.
 *
 * A scooter is rendered as JSON like this:
 *     {"id":"S1","model":"StandardScooter","battery":80,"inUse":false}
 * where "model" is the scooter's simple class name.
 *
 * Routes:
 *
 *   GET  /scooters               200 a JSON array of every scooter, in store
 *                                    order ("[]" when there are none).
 *                                Optional query ?minBattery=N keeps only
 *                                scooters with battery >= N.
 *                                400 {"error":"minBattery must be a whole number"}
 *                                    when minBattery is present but not an integer.
 *   GET  /scooters/{id}          200 the scooter
 *                                404 {"error":"Scooter not found"}
 *   POST /scooters/{id}/unlock   Checked in this order:
 *                                404 {"error":"Scooter not found"}
 *                                409 {"error":"Scooter already in use"}
 *                                422 {"error":"Battery too low"}  (battery under 20)
 *                                otherwise mark it in use and return 200 with the scooter.
 *   POST /scooters/{id}/lock     404 {"error":"Scooter not found"}
 *                                409 {"error":"Scooter is not in use"}
 *                                otherwise mark it free and return 200 with the scooter.
 *
 *   A known path used with the wrong method     405 {"error":"Method not allowed"}
 *   Any other path                              404 {"error":"Not found"}
 */
public class ScooterApi {

    private final ScooterStore store;

    public ScooterApi(ScooterStore store) {
        this.store = store;
    }

    /** @param query the raw query string without the '?', or null if there is none */
    public ApiResponse route(String method, String path, String query) {
        // TODO (Q7.2): implement the routes described above.
        return new ApiResponse(501, "{\"error\":\"Not implemented\"}");
    }
}
