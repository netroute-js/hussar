package pl.netroute.hussar.service.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.sql.api.PostgreSQLDockerService;
import pl.netroute.hussar.service.sql.api.PostgreSQLDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PostgreSQLDockerServiceTestFactory {

    public static PostgreSQLDockerService createMinimallyConfigured(@NonNull SQLDatabaseSchema databaseSchema,
                                                                    @NonNull ServiceConfigureContext context) {
        return PostgreSQLDockerServiceConfigurer
                .newInstance()
                .databaseSchema(databaseSchema)
                .done()
                .configure(context);
    }

}
