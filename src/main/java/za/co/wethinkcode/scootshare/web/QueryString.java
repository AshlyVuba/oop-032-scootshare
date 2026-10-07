package za.co.wethinkcode.scootshare.web;

import java.util.Optional;

/** Reads values from a raw query string such as "minBattery=50&x=1". Provided; do not change. */
public final class QueryString {

    private QueryString() {
    }

    public static Optional<String> get(String query, String name) {
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            String key = eq < 0 ? pair : pair.substring(0, eq);
            if (key.equals(name)) {
                return Optional.of(eq < 0 ? "" : pair.substring(eq + 1));
            }
        }
        return Optional.empty();
    }
}
