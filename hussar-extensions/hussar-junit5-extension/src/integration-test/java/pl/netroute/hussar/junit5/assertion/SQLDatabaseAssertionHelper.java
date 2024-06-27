package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.flywaydb.core.internal.jdbc.DriverDataSource;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class SQLDatabaseAssertionHelper {
    private static final int CONNECTION_TIMEOUT_MS = 5000;

    private static final String PATH_SEPARATOR = "/";
    private static final String UNKNOWN_DRIVER = null;

    static void assertDatabaseReachable(Endpoint endpoint, SQLDatabaseSchema schema, SQLDatabaseCredentials credentials) {
        var jdbcURL = resolveJdbcURL(endpoint, schema);
        var datasource = createDatasource(jdbcURL, credentials);

        try {
            var validConnection = datasource.getConnection().isValid(CONNECTION_TIMEOUT_MS);

            assertThat(validConnection).isTrue();
        } catch (SQLException ex) {
            throw new AssertionError("Expected database to be accessible", ex);
        }
    }

    private static String resolveJdbcURL(Endpoint endpoint, SQLDatabaseSchema schema) {
        return endpoint.address() + PATH_SEPARATOR + schema.name();
    }

    private static DataSource createDatasource(@NonNull String url,
                                               @NonNull SQLDatabaseCredentials credentials) {
        var classLoader = Thread.currentThread().getContextClassLoader();

        var username = credentials.username();
        var password = credentials.password();

        return new DriverDataSource(classLoader, UNKNOWN_DRIVER, url, username, password);
    }

}
