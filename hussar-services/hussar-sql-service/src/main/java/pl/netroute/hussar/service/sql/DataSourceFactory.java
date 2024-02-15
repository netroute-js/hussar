package pl.netroute.hussar.service.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.flywaydb.core.internal.jdbc.DriverDataSource;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;

import javax.sql.DataSource;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DataSourceFactory {
    private static final String UNKNOWN_DRIVER = null;
    private static final String SLASH = "/";

    static DataSource create(@NonNull Endpoint endpoint, @NonNull SQLDatabaseCredentials credentials) {
        return create(endpoint, credentials, null);
    }

    static DataSource create(@NonNull Endpoint endpoint, @NonNull SQLDatabaseCredentials credentials, String schema) {
        var classLoader = Thread.currentThread().getContextClassLoader();

        var jdbcURL = Optional
                .ofNullable(schema)
                .map(actualSchema -> prepareJdbcSchemaURL(endpoint, actualSchema))
                .orElseGet(() -> prepareJdbcURL(endpoint.getAddress()));

        var username = credentials.username();
        var password = credentials.password();

        return new DriverDataSource(classLoader, UNKNOWN_DRIVER, jdbcURL, username, password);
    }

    private static String prepareJdbcSchemaURL(Endpoint endpoint, String schema) {
        return endpoint.getAddress() + SLASH + schema;
    }

    private static String prepareJdbcURL(String jdbcURL) {
        return jdbcURL.endsWith(SLASH) ? jdbcURL : jdbcURL + SLASH;
    }
}
