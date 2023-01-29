package pl.netroute.hussar.junit5.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.Objects;

public class WiremockClient {
    private static final String ADMIN_PATH = "/__admin";

    private final String endpoint;
    private final OkHttpClient httpClient;

    public WiremockClient(String endpoint) {
        Objects.requireNonNull(endpoint, "endpoint is required");

        this.endpoint = endpoint;
        this.httpClient = new OkHttpClient();
    }

    public boolean isReachable() {
        var request = new Request
                .Builder()
                .url(endpoint + ADMIN_PATH)
                .build();

        try(var response = httpClient.newCall(request).execute()) {
            return response.isSuccessful();
        } catch(IOException ex) {
            throw new IllegalStateException("Could not finish reachable request", ex);
        }
    }

}
