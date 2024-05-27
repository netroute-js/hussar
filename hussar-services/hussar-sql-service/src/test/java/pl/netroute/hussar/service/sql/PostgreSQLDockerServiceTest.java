package pl.netroute.hussar.service.sql;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
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

public class PostgreSQLDockerServiceTest {
    private static final String POSTGRE_SQL_HOST = "localhost";
    private static final int POSTGRE_SQL_LISTENING_PORT = 5432;
    private static final int POSTGRE_SQL_MAPPED_PORT = 30120;

    private static final String POSTGRE_SQL_SERVICE_NAME = "postgres-service";
    private static final String POSTGRE_SQL_SERVICE_IMAGE = "postgres";

    private static final String POSTGRE_SQL_SCHEME = "jdbc:postgresql://";

    private static final String POSTGRE_SQL_PASSWORD_ENV = "POSTGRES_PASSWORD";

    private static final String POSTGRE_SQL_USERNAME = "postgres";
    private static final String POSTGRE_SQL_PASSWORD = "test";
    private static final SQLDatabaseCredentials MYSQL_CREDENTIALS = new SQLDatabaseCredentials(POSTGRE_SQL_USERNAME, POSTGRE_SQL_PASSWORD);

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = SQLDatabaseDockerServiceConfig
                .builder()
                .name(POSTGRE_SQL_SERVICE_NAME)
                .dockerImage(POSTGRE_SQL_SERVICE_IMAGE)
                .scheme(POSTGRE_SQL_SCHEME)
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
        service.start(ServiceStartupContext.empty());

        // then
        var endpoint = Endpoint.of(POSTGRE_SQL_SCHEME, POSTGRE_SQL_HOST, POSTGRE_SQL_MAPPED_PORT);
        var envVariables = Map.of(POSTGRE_SQL_PASSWORD_ENV, POSTGRE_SQL_PASSWORD);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, POSTGRE_SQL_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, POSTGRE_SQL_SERVICE_NAME);
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

        var usernameProperty = "mysql.username";
        var usernameEnvVariable = "MYSQL_USERNAME";

        var passwordProperty = "redis.password";
        var passwordEnvVariable = "MYSQL_PASSWORD";

        var config = SQLDatabaseDockerServiceConfig
                .builder()
                .name(POSTGRE_SQL_SERVICE_NAME)
                .dockerImage(POSTGRE_SQL_SERVICE_IMAGE)
                .scheme(POSTGRE_SQL_SCHEME)
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
        service.start(ServiceStartupContext.empty());

        // then
        var endpoint = Endpoint.of(POSTGRE_SQL_SCHEME, POSTGRE_SQL_HOST, POSTGRE_SQL_MAPPED_PORT);
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());

        var usernamePropertyEntry = ConfigurationEntry.property(usernameProperty, POSTGRE_SQL_USERNAME);
        var usernameEnvVariableEntry = ConfigurationEntry.envVariable(usernameEnvVariable, POSTGRE_SQL_USERNAME);

        var passwordPropertyEntry = ConfigurationEntry.property(passwordProperty, POSTGRE_SQL_PASSWORD);
        var passwordEnvVariableEntry = ConfigurationEntry.envVariable(passwordEnvVariable, POSTGRE_SQL_PASSWORD);

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointPropertyEntry,
                endpointEnvVariableEntry,
                usernamePropertyEntry,
                usernameEnvVariableEntry,
                passwordPropertyEntry,
                passwordEnvVariableEntry
        );

        var envVariables = Map.of(
                POSTGRE_SQL_PASSWORD_ENV, POSTGRE_SQL_PASSWORD
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, POSTGRE_SQL_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, POSTGRE_SQL_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertSchemasInitialized(schemaInitializer, service, MYSQL_CREDENTIALS, schemas);
        assertEntriesRegistered(service, registeredEntries);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = SQLDatabaseDockerServiceConfig
                .builder()
                .name(POSTGRE_SQL_SERVICE_NAME)
                .dockerImage(POSTGRE_SQL_SERVICE_IMAGE)
                .scheme(POSTGRE_SQL_SCHEME)
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
        when(container.getHost()).thenReturn(POSTGRE_SQL_HOST);
        when(container.getExposedPorts()).thenReturn(List.of(POSTGRE_SQL_LISTENING_PORT));
        when(container.getMappedPort(POSTGRE_SQL_LISTENING_PORT)).thenReturn(POSTGRE_SQL_MAPPED_PORT);
    }

    private PostgreSQLDockerService createDatabaseService(SQLDatabaseDockerServiceConfig config,
                                                          GenericContainer<?> container,
                                                          DatabaseSchemaInitializer schemaInitializer) {
        var configurationRegistry = new MapConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);

        return new PostgreSQLDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                credentialsRegisterer,
                schemaInitializer
        );
    }

}
