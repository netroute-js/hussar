package pl.netroute.hussar.service.sql.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.GenericContainerStubHelper.GenericContainerAccessibility;
import pl.netroute.hussar.core.stub.helper.StubHelper;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static pl.netroute.hussar.core.assertion.helper.NetworkConfigurerAssertionHelper.assertNetworkConfigured;
import static pl.netroute.hussar.core.docker.DockerHostResolver.DOCKER_BRIDGE_HOST;
import static pl.netroute.hussar.core.docker.DockerHostResolver.DOCKER_HOST_GATEWAY;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExtraHostConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNetworkControl;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertSingleEndpoint;
import static pl.netroute.hussar.core.stub.helper.GenericContainerStubHelper.givenContainerAccessible;
import static pl.netroute.hussar.core.stub.helper.NetworkConfigurerStubHelper.givenNetworkConfigured;
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
    private static final SQLDatabaseCredentials POSTGRE_SQL_CREDENTIALS = new SQLDatabaseCredentials(POSTGRE_SQL_USERNAME, POSTGRE_SQL_PASSWORD);

    private NetworkConfigurer networkConfigurer;
    private DatabaseSchemaInitializer schemaInitializer;

    private GenericContainerAccessibility containerAccessibility;

    @BeforeEach
    public void setup() {
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);
        schemaInitializer = StubHelper.defaultStub(DatabaseSchemaInitializer.class);

        containerAccessibility = GenericContainerAccessibility
                .builder()
                .host(POSTGRE_SQL_HOST)
                .exposedPort(POSTGRE_SQL_LISTENING_PORT)
                .mappedPort(POSTGRE_SQL_LISTENING_PORT, POSTGRE_SQL_MAPPED_PORT)
                .build();
    }

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

        var endpoint = Endpoint.of(POSTGRE_SQL_SCHEME, POSTGRE_SQL_HOST, POSTGRE_SQL_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createDatabaseService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, POSTGRE_SQL_SERVICE_NAME, endpoint);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var envVariables = Map.of(POSTGRE_SQL_PASSWORD_ENV, POSTGRE_SQL_PASSWORD);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, POSTGRE_SQL_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerExtraHostConfigured(container, DOCKER_BRIDGE_HOST, DOCKER_HOST_GATEWAY);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, POSTGRE_SQL_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNetworkControl(service);
        assertNoSchemaInitialized(schemaInitializer);
        assertNoEntriesRegistered(service);
        assertNetworkConfigured(networkConfigurer, POSTGRE_SQL_SERVICE_NAME, endpoint);
    }

    @Test
    public void shouldStartExtendedService() {
        // given
        var schemaA = SQLDatabaseSchema.scriptLess("schemaA");
        var schemaB = new SQLDatabaseSchema("schemaB", "/some/location");
        var schemas = Set.of(schemaA, schemaB);

        var endpointProperty = "endpoint.url";
        var endpointEnvVariable = "ENDPOINT_URL";

        var usernameProperty = "postgres.username";
        var usernameEnvVariable = "POSTGRES_USERNAME";

        var passwordProperty = "postgres.password";
        var passwordEnvVariable = "POSTGRES_PASSWORD";

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

        var endpoint = Endpoint.of(POSTGRE_SQL_SCHEME, POSTGRE_SQL_HOST, POSTGRE_SQL_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createDatabaseService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, POSTGRE_SQL_SERVICE_NAME, endpoint);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
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
        assertContainerExtraHostConfigured(container, DOCKER_BRIDGE_HOST, DOCKER_HOST_GATEWAY);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, POSTGRE_SQL_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNetworkControl(service);
        assertSchemasInitialized(schemaInitializer, service, POSTGRE_SQL_CREDENTIALS, schemas);
        assertEntriesRegistered(service, registeredEntries);
        assertNetworkConfigured(networkConfigurer, POSTGRE_SQL_SERVICE_NAME, endpoint);
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

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createDatabaseService(config, container);

        givenContainerAccessible(container, containerAccessibility);

        // when
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    private PostgreSQLDockerService createDatabaseService(SQLDatabaseDockerServiceConfig config,
                                                          GenericContainer<?> container) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);

        return new PostgreSQLDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                credentialsRegisterer,
                schemaInitializer
        );
    }
}