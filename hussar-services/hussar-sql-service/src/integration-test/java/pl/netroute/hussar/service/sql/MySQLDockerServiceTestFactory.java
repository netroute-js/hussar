package pl.netroute.hussar.service.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.sql.api.MySQLDockerService;
import pl.netroute.hussar.service.sql.api.MySQLDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MySQLDockerServiceTestFactory {

    public static MySQLDockerService createMinimallyConfigured(@NonNull SQLDatabaseSchema databaseSchema,
                                                               @NonNull ServiceConfigureContext context) {
        return MySQLDockerServiceConfigurer
                .newInstance()
                .databaseSchema(databaseSchema)
                .done()
                .configure(context);
    }

}
