package pl.netroute.hussar.junit5.client;

import lombok.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.application.Application;

import java.util.Optional;

public interface SimpleApplicationClient {

    @GetMapping("/api/v1/ping")
    String ping();

    @GetMapping("/api/v1/properties/{property}")
    Optional<String> getProperty(@PathVariable("property") String property);

    @GetMapping("/api/v1/version")
    Integer getVersion();

    @PostMapping("/api/v1/version")
    Integer incrementVersion();

    static SimpleApplicationClient newClient(@NonNull Endpoint endpoint) {
        return ClientFactory.create(endpoint, SimpleApplicationClient.class);
    }

    static SimpleApplicationClient newClient(@NonNull Application application) {
        var endpoint = application
                .getEndpoints()
                .getFirst();

        return newClient(endpoint);
    }

}
