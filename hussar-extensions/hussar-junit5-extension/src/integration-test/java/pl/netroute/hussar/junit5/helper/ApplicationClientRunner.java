package pl.netroute.hussar.junit5.helper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.junit5.client.SimpleApplicationClient;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class ApplicationClientRunner {

    @NonNull
    private final Application application;

    public void run(@NonNull Consumer<SimpleApplicationClient> runner) {
        application
                .getEndpoints()
                .stream()
                .map(SimpleApplicationClient::newClient)
                .forEach(runner);
    }

}
