package pl.netroute.hussar.spring.boot.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.netroute.hussar.core.Endpoint;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class SimpleApplicationClient {
    private static final String PING_PATH = "/api/v1/ping";

    private final String endpoint;
    private final OkHttpClient httpClient;

    public SimpleApplicationClient(Endpoint endpoint) {
        Objects.requireNonNull(endpoint, "endpoint is required");

        this.endpoint = endpoint.getAddress();
        this.httpClient = new OkHttpClient();
    }

    public String ping() {
        var request = new Request
                .Builder()
                .url(endpoint + PING_PATH)
                .build();

        try(var response = httpClient.newCall(request).execute()) {
            return Optional
                    .of(response)
                    .filter(Response::isSuccessful)
                    .map(Response::body)
                    .map(this::extractBodyContent)
                    .orElseThrow(() -> new IllegalStateException("Expected successful ping response"));
        } catch(IOException ex) {
            throw new IllegalStateException("Could not finish ping request", ex);
        }
    }

    private String extractBodyContent(ResponseBody body) {
        try {
            return body.string();
        } catch (IOException ex) {
            throw new IllegalStateException("Could not extract ping response body content", ex);
        }
    }

}
