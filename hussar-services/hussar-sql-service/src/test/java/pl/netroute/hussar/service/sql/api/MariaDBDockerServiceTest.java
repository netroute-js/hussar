package pl.netroute.hussar.service.sql.api;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertSingleEndpoint;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.service.sql.assertion.DatabaseSchemaInitializerAssertionHelper.assertNoSchemaInitialized;
import static pl.netroute.hussar.service.sql.assertion.DatabaseSchemaInitializerAssertionHelper.assertSchemasInitialized;

public class MariaDBDockerServiceTest  {
    private static final String MARIA_DB_HOST = "localhost";
    private static final int MARIA_DB_LISTENING_PORT = 3306;
    private static final int MARIA_DB_MAPPED_PORT = 30120;

    private static final String MARIA_DB_SERVICE_NAME = "mariadb-service";
    private static final String MARIA_DB_SERVICE_IMAGE = "mariadb";

    private static final String MARIA_DB_SCHEME = "jdbc:mariadb://";

    private static final String MARIA_DB_PASSWORD_ENV = "MARIADB_ROOT_PASSWORD";

    private static final String MARIA_DB_USERNAME = "root";
    private static final String MARIA_DB_PASSWORD = "test";
    private static final SQLDatabaseCredentials MARIA_DB_CREDENTIALS = new SQLDatabaseCredentials(MARIA_DB_USERNAME, MARIA_DB_PASSWORD);

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = SQLDatabaseDockerServiceConfig
                .builder()
                .name(MARIA_DB_SERVICE_NAME)
                .dockerImage(MARIA_DB_SERVICE_IMAGE)
                .scheme(MARIA_DB_SCHEME)
                .databaseSchemas(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var schemaInitializer = mock(DatabaseSchemaInitializer.class);
        var service = createDatabaseService(config, container, schemaInitializer);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = Endpoint.of(MARIA_DB_SCHEME, MARIA_DB_HOST, MARIA_DB_MAPPED_PORT);
        var envVariables = Map.of(MARIA_DB_PASSWORD_ENV, MARIA_DB_PASSWORD);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, MARIA_DB_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, MARIA_DB_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNoSchemaInitialized(schemaInitializer);
        assertNoEntriesRegistered(service);
    }

    @Test
    public void shouldStartExtendedService() {
        // given
        var schemaA = SQLDatabaseSchema.scriptLess("schemaA");
        var schemaB = new SQLDatabaseSchema("schemaB", "/some/location");
        var schemas = Set.of(schemaA, schemaB);

        var endpointProperty = "endpoint.url";
        var endpointEnvVariable = "ENDPOINT_URL";

        var usernameProperty = "mariadb.username";
        var usernameEnvVariable = "MARIA_DB_USERNAME";

        var passwordProperty = "mariadb.password";
        var passwordEnvVariable = "MARIA_DB_PASSWORD";

        var config = SQLDatabaseDockerServiceConfig
                .builder()
                .name(MARIA_DB_SERVICE_NAME)
                .dockerImage(MARIA_DB_SERVICE_IMAGE)
                .scheme(MARIA_DB_SCHEME)
                .databaseSchemas(schemas)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of(usernameEnvVariable))
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of(passwordEnvVariable))
                .build();

        var container = createStubContainer();
        var schemaInitializer = mock(DatabaseSchemaInitializer.class);
        var service = createDatabaseService(config, container, schemaInitializer);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = Endpoint.of(MARIA_DB_SCHEME, MARIA_DB_HOST, MARIA_DB_MAPPED_PORT);
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());

        var usernamePropertyEntry = ConfigurationEntry.property(usernameProperty, MARIA_DB_USERNAME);
        var usernameEnvVariableEntry = ConfigurationEntry.envVariable(usernameEnvVariable, MARIA_DB_USERNAME);

        var passwordPropertyEntry = ConfigurationEntry.property(passwordProperty, MARIA_DB_PASSWORD);
        var passwordEnvVariableEntry = ConfigurationEntry.envVariable(passwordEnvVariable, MARIA_DB_PASSWORD);

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointPropertyEntry,
                endpointEnvVariableEntry,
                usernamePropertyEntry,
                usernameEnvVariableEntry,
                passwordPropertyEntry,
                passwordEnvVariableEntry
        );

        var envVariables = Map.of(
                MARIA_DB_PASSWORD_ENV, MARIA_DB_PASSWORD
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, MARIA_DB_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, MARIA_DB_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertSchemasInitialized(schemaInitializer, service, MARIA_DB_CREDENTIALS, schemas);
        assertEntriesRegistered(service, registeredEntries);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = SQLDatabaseDockerServiceConfig
                .builder()
                .name(MARIA_DB_SERVICE_NAME)
                .dockerImage(MARIA_DB_SERVICE_IMAGE)
                .scheme(MARIA_DB_SCHEME)
                .databaseSchemas(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var schemaInitializer = mock(DatabaseSchemaInitializer.class);
        var service = createDatabaseService(config, container, schemaInitializer);

        givenContainerAccessible(container);

        // when
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    private GenericContainer<?> createStubContainer() {
        return mock(GenericContainer.class, RETURNS_DEEP_STUBS);
    }

    private void givenContainerAccessible(GenericContainer<?> container) {
        when(container.getHost()).thenReturn(MARIA_DB_HOST);
        when(container.getExposedPorts()).thenReturn(List.of(MARIA_DB_LISTENING_PORT));
        when(container.getMappedPort(MARIA_DB_LISTENING_PORT)).thenReturn(MARIA_DB_MAPPED_PORT);
    }

    private MariaDBDockerService createDatabaseService(SQLDatabaseDockerServiceConfig config,
                                                       GenericContainer<?> container,
                                                       DatabaseSchemaInitializer schemaInitializer) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);

        return new MariaDBDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                credentialsRegisterer,
                schemaInitializer
        );
    }

}
