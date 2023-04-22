package pl.netroute.hussar.spring.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@SpringBootApplication(exclude = { FeignAutoConfiguration.class })
public class SimpleSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSpringApplication.class, args);
    }

    @RestController
    @RequestMapping("/api/v1")
    public static class PingController {

        @Autowired
        Environment environment;

        @GetMapping("/ping")
        public ResponseEntity<String> ping() {
            return ResponseEntity.ok("pong");
        }

        @GetMapping("/properties/{property}")
        public ResponseEntity<String> getProperty(@PathVariable("property") String property) {
            var maybeProperty = Optional.ofNullable(environment.getProperty(property));

            return ResponseEntity.of(maybeProperty);
        }

    }
}
