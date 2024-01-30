package pl.netroute.hussar.service.sql.assertion;

import pl.netroute.hussar.service.sql.DatabaseCredentials;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.flywaydb.core.internal.jdbc.DriverDataSource;

import javax.sql.DataSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DataSourceFactory {
    private static final String UNKNOWN_DRIVER = null;

    static DataSource create(@NonNull String url,
                             @NonNull DatabaseCredentials credentials) {
        var classLoader = Thread.currentThread().getContextClassLoader();

        var username = credentials.username();
        var password = credentials.password();

        return new DriverDataSource(classLoader, UNKNOWN_DRIVER, url, username, password);
    }

}
