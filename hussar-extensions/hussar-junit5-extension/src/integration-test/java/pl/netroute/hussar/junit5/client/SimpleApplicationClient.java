package pl.netroute.hussar.junit5.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public interface SimpleApplicationClient {

    @GetMapping("/api/v1/ping")
    String ping();

    @GetMapping("/api/v1/properties/{property}")
    Optional<String> getProperty(@PathVariable("property") String property);

}
