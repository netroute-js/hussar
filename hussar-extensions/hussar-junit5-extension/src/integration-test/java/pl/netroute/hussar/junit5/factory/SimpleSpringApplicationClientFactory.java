package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.junit5.client.ClientFactory;
import pl.netroute.hussar.junit5.client.SimpleApplicationClient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleSpringApplicationClientFactory {

    public static SimpleApplicationClient create(@NonNull Application application) {
        var endpoint = application
                .getEndpoints()
                .getFirst();

        return ClientFactory.create(endpoint, SimpleApplicationClient.class);
    }

}
