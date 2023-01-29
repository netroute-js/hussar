package pl.netroute.hussar.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SimpleSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSpringApplication.class, args);
    }

    @RestController
    @RequestMapping("/api/v1")
    public static class PingController {

        @GetMapping("/ping")
        public ResponseEntity<String> ping() {
            return ResponseEntity.ok("pong");
        }

    }
}
