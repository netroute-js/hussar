package pl.netroute.hussar.service.sql.assertion;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.sql.SQLDatabaseDockerService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RequiredArgsConstructor
public class SQLDBAssertionHelper {
    private static final int SINGLE = 1;
    private static final int DATABASE_CHARS_COUNT = 15;

    private static final String PATH_SEPARATOR = "/";

    private static final String SELECT_ALL_QUERY_TEMPLATE = "SELECT * FROM %s.%s";
    private static final String CREATE_DATABASE_QUERY_TEMPLATE = "CREATE DATABASE %s";

    @NonNull
    private final SQLDatabaseDockerService database;

    public void assertSingleEndpoint() {
        assertThat(database.getEndpoints()).hasSize(SINGLE);
    }

    public void asserDatabaseAccessible(@NonNull String schema) {
        var template = createTemplate(schema);
        var databaseName = RandomStringUtils.randomAlphabetic(DATABASE_CHARS_COUNT);
        var command = CREATE_DATABASE_QUERY_TEMPLATE.formatted(databaseName);

        assertThat(executeCommand(command, template)).isEmpty();
    }

    public void assertDatabaseNotAccessible(@NonNull String schema, @NonNull Endpoint endpoint) {
        var databaseName = RandomStringUtils.randomAlphabetic(DATABASE_CHARS_COUNT);
        var command = CREATE_DATABASE_QUERY_TEMPLATE.formatted(databaseName);

        assertThatThrownBy(() -> createTemplate(schema, endpoint).execute(command))
                .isInstanceOf(CannotGetJdbcConnectionException.class)
                .hasMessage("Failed to obtain JDBC Connection");
    }

    public void assertTablesCreated(@NonNull String schema, @NonNull List<String> tables) {
        var template = createTemplate(schema);

        var failures = tables.stream()
                .map(table -> SELECT_ALL_QUERY_TEMPLATE.formatted(schema, table))
                .map(query -> executeCommand(query, template))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        assertThat(failures).isEmpty();
    }

    public void assertTablesNotCreated(@NonNull String schema, @NonNull List<String> tables) {
        var template = createTemplate(schema);

        var failures = tables.stream()
                .map(table -> SELECT_ALL_QUERY_TEMPLATE.formatted(schema, table))
                .map(query -> executeCommand(query, template))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        assertThat(failures).hasSize(tables.size());

        failures.forEach(failure -> assertThat(failure)
                .isInstanceOf(BadSqlGrammarException.class)
        );
    }

    public void assertRegisteredEndpointUnderProperty(@NonNull String registeredProperty) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(database);

        assertRegisteredEntryInConfigRegistry(registeredProperty, endpoint.address(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredEndpointUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(database);

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, endpoint.address(), EnvVariableConfigurationEntry.class);
    }

    public void assertRegisteredUsernameUnderProperty(@NonNull String registeredProperty) {
        var credentials = database.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredProperty, credentials.username(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredUsernameUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var credentials = database.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, credentials.username(), EnvVariableConfigurationEntry.class);
    }

    public void assertRegisteredPasswordUnderProperty(@NonNull String registeredProperty) {
        var credentials = database.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredProperty, credentials.password(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredPasswordUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var credentials = database.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, credentials.password(), EnvVariableConfigurationEntry.class);
    }

    public void assertNoEntriesRegistered() {
        var entriesRegistered = database
                .getConfigurationRegistry()
                .getEntries();

        assertThat(entriesRegistered).isEmpty();
    }

    private void assertRegisteredEntryInConfigRegistry(String entryName, String entryValue, Class<? extends ConfigurationEntry> configType) {
        var configRegistry = database.getConfigurationRegistry();

        configRegistry
                .getEntries()
                .stream()
                .filter(configEntry -> configEntry.getClass().equals(configType))
                .filter(configEntry -> configEntry.name().equals(entryName))
                .findFirst()
                .ifPresentOrElse(
                        configEntry -> assertThat(configEntry.value()).isEqualTo(entryValue),
                        () -> { throw new AssertionError("Expected registered entry in config registry. Found none"); }
                );
    }

    private Optional<RuntimeException> executeCommand(String command, JdbcTemplate template) {
        try {
            template.execute(command);
        } catch (RuntimeException ex) {
            return Optional.of(ex);
        }

        return Optional.empty();
    }

    private JdbcTemplate createTemplate(String schema) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(database);

        return createTemplate(schema, endpoint);
    }

    private JdbcTemplate createTemplate(String schema, Endpoint endpoint) {
        var credentials = database.getCredentials();
        var url = endpoint.address() + PATH_SEPARATOR + schema;
        var dataSource = DataSourceFactory.create(url, credentials);

        return new JdbcTemplate(dataSource);
    }

}
