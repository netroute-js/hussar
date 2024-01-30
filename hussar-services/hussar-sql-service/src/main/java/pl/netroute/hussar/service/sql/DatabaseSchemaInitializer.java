package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.service.sql.api.DatabaseSchema;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DatabaseSchemaInitializer {
    private static final String CREATE_DATABASE_SQL_TEMPLATE = "CREATE DATABASE %s";

    @NonNull
    private final DataSource dataSource;

    void initialize(@NonNull DatabaseSchema database) {
        var schema = database.schema();
        var scriptsLocation = database.scriptsLocation();

        createSchema(schema);

        if(!StringUtils.isBlank(scriptsLocation)) {
            migrate(schema, scriptsLocation);
        }
    }

    private void createSchema(String schema) {
        log.info("Creating DB[{}] schema", schema);

        try(var connection = dataSource.getConnection()) {
            var command = CREATE_DATABASE_SQL_TEMPLATE.formatted(schema);

            connection
                    .prepareStatement(command)
                    .execute();
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not create DB schema", ex);
        }
    }

    private void migrate(String schema, String scriptsLocation) {
        log.info("Initializing DB[{}] schema by applying scripts in {}", schema, scriptsLocation);

        Flyway
                .configure()
                .schemas(schema)
                .locations(scriptsLocation)
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}
