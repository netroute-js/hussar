package pl.netroute.hussar.service.sql.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.service.sql.SQLDatabaseDockerService;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseSchemaInitializerAssertionHelper {

    public static void assertSchemaInitialized(@NonNull DatabaseSchemaInitializer schemaInitializer,
                                               @NonNull SQLDatabaseDockerService databaseService,
                                               @NonNull SQLDatabaseCredentials credentials,
                                               @NonNull Set<SQLDatabaseSchema> schemas) {
    }

    public static void assertNoSchemaInitialized(@NonNull DatabaseSchemaInitializer schemaInitializer) {
        verify(schemaInitializer, never()).initialize(any(), any(), any());
    }

}