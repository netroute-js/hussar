package pl.netroute.hussar.service.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.sql.api.MariaDBDockerService;
import pl.netroute.hussar.service.sql.api.MariaDBDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MariaDBDockerServiceTestFactory {

    public static MariaDBDockerService createMinimallyConfigured(@NonNull SQLDatabaseSchema databaseSchema,
                                                                 @NonNull ServiceConfigureContext context) {
        return MariaDBDockerServiceConfigurer
                .newInstance()
                .databaseSchema(databaseSchema)
                .done()
                .configure(context);
    }

}
