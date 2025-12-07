package pl.netroute.hussar.service.sql.schema;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.helper.StringHelper;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import java.sql.SQLException;

/**
 * An initializer to create configured SQL DB schema.
 */
@Slf4j
@InternalUseOnly
public class DatabaseSchemaInitializer {
    private static final String CREATE_DATABASE_SQL_TEMPLATE = "CREATE DATABASE %s";

    /**
     * Initializes SQL DB schema.
     *
     * @param endpoint - the {@link Endpoint} of the SQL DB.
     * @param credentials - the {@link SQLDatabaseCredentials} of SQL DB.
     * @param database - the {@link SQLDatabaseSchema} to be initialized.
     */
    public void initialize(@NonNull Endpoint endpoint,
                           @NonNull SQLDatabaseCredentials credentials,
                           @NonNull SQLDatabaseSchema database) {
        var schema = database.name();
        var scriptsLocation = database.scriptsLocation();

        createSchema(schema, endpoint, credentials);

        if(!StringHelper.isTextBlank(scriptsLocation)) {
            migrate(schema, scriptsLocation, endpoint, credentials);
        }
    }

    private void createSchema(String schema,
                              Endpoint endpoint,
                              SQLDatabaseCredentials credentials) {
        log.info("Creating DB[{}] schema", schema);

        var dataSource = DataSourceFactory.create(endpoint, credentials);

        try(var connection = dataSource.getConnection()) {
            var command = CREATE_DATABASE_SQL_TEMPLATE.formatted(schema);

            connection
                    .prepareStatement(command)
                    .execute();
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not create DB schema", ex);
        }
    }

    private void migrate(String schema,
                         String scriptsLocation,
                         Endpoint endpoint,
                         SQLDatabaseCredentials credentials) {
        log.info("Initializing DB[{}] schema by applying scripts in {}", schema, scriptsLocation);

        var dataSource = DataSourceFactory.create(endpoint, credentials, schema);

        var result = Flyway
                .configure()
                .schemas(schema)
                .locations(scriptsLocation)
                .dataSource(dataSource)
                .load()
                .migrate();

        if(!result.success) {
            var errorMessage = "DB[%s] schema migration failed".formatted(schema);

            throw new IllegalStateException(errorMessage);
        }
    }
}
