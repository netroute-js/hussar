package pl.netroute.hussar.service.nosql.mongodb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBDockerService;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBDockerServiceConfigurer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MongoDBDockerServiceTestFactory {

    public static MongoDBDockerService createMinimallyConfigured(@NonNull ServiceConfigureContext context) {
        return MongoDBDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(context);
    }

}
