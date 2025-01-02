package pl.netroute.hussar.service.nosql.mongodb.assertion;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ServerConnectionState;
import com.mongodb.connection.ServerDescription;
import com.mongodb.connection.SocketSettings;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.nosql.mongodb.MongoDBDockerService;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class MongoDBAssertionHelper {
    private static final int SINGLE = 1;

    private static final int TIMEOUT_MILLIS = 5000;

    private static final String DEFAULT_AUTH_DB = "admin";

    @NonNull
    private final MongoDBDockerService database;

    public void assertSingleEndpoint() {
        assertThat(database.getEndpoints()).hasSize(SINGLE);
    }

    public void asserDatabaseAccessible() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(database);

        try(var client = createClient(endpoint)) {
            assertThat(client.listDatabaseNames()).contains(DEFAULT_AUTH_DB);
        }
    }

    public void assertDatabaseNotAccessible(@NonNull Endpoint endpoint) {
        try(var client = createClient(endpoint)) {
            var states = client
                    .getClusterDescription()
                    .getServerDescriptions()
                    .stream()
                    .map(ServerDescription::getState)
                    .toList();

            assertThat(states).containsOnly(ServerConnectionState.CONNECTING);
        }
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

    private MongoClient createClient(Endpoint endpoint) {
        var mongoURL = new ConnectionString(endpoint.address());
        var mongoCredentials = createCredentials();

        var mongoSettings = MongoClientSettings
                .builder()
                .credential(mongoCredentials)
                .applyToSocketSettings(this::configureSocketSettings)
                .applyToClusterSettings(this::configureClusterSettings)
                .applyConnectionString(mongoURL)
                .build();

        return MongoClients.create(mongoSettings);
    }

    private MongoCredential createCredentials() {
        var credentials = database.getCredentials();
        var username = credentials.username();
        var password = credentials.password();

        return MongoCredential.createCredential(username, DEFAULT_AUTH_DB, password.toCharArray());
    }

    private void configureSocketSettings(SocketSettings.Builder builder) {
        builder.connectTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
               .readTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    private void configureClusterSettings(ClusterSettings.Builder builder) {
        builder.serverSelectionTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }
}
