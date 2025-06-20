package pl.netroute.hussar.service.sql.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.StubHelper;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNetworkConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStartupTimeoutConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertDirectEndpoints;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEndpoints;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNetworkControl;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.stub.helper.NetworkConfigurerStubHelper.givenNetworkConfigured;
import static pl.netroute.hussar.service.sql.assertion.DatabaseSchemaInitializerAssertionHelper.assertNoSchemaInitialized;
import static pl.netroute.hussar.service.sql.assertion.DatabaseSchemaInitializerAssertionHelper.assertSchemasInitialized;

public class MySQLDockerServiceTest {
    private static final int MYSQL_LISTENING_PORT = 3306;

    private static final Duration MYSQL_STARTUP_TIMEOUT = Duration.ofSeconds(90);

    private static final String MYSQL_SERVICE_NAME = "mysql-service";
    private static final String MYSQL_SERVICE_IMAGE = "mysql";

    private static final String MYSQL_DIRECT_NETWORK = "direct-" + MYSQL_SERVICE_NAME;

    private static final String MYSQL_SCHEME = "jdbc:mysql://";

    private static final String MYSQL_PASSWORD_ENV = "MYSQL_ROOT_PASSWORD";

    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "test";
    private static final SQLDatabaseCredentials MYSQL_CREDENTIALS = new SQLDatabaseCredentials(MYSQL_USERNAME, MYSQL_PASSWORD);

    private DockerNetwork dockerNetwork;
    private NetworkConfigurer networkConfigurer;
    private DatabaseSchemaInitializer schemaInitializer;

    @BeforeEach
    public void setup() {
        dockerNetwork = StubHelper.defaultStub(DockerNetwork.class);
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);
        schemaInitializer = StubHelper.defaultStub(DatabaseSchemaInitializer.class);
    }

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = SQLDatabaseDockerServiceConfig
                .builder()
                .name(MYSQL_SERVICE_NAME)
                .dockerImage(MYSQL_SERVICE_IMAGE)
                .startupTimeout(MYSQL_STARTUP_TIMEOUT)
                .scheme(MYSQL_SCHEME)
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

        var network = givenNetworkConfigured(networkConfigurer, MYSQL_SERVICE_NAME, MYSQL_SCHEME, MYSQL_LISTENING_PORT);
        var directNetwork = givenNetworkConfigured(networkConfigurer, MYSQL_DIRECT_NETWORK, MYSQL_SCHEME, MYSQL_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var envVariables = Map.of(MYSQL_PASSWORD_ENV, MYSQL_PASSWORD);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, MYSQL_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerStartupTimeoutConfigured(container, MYSQL_STARTUP_TIMEOUT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, MYSQL_SERVICE_NAME);
        assertEndpoints(service, network);
        assertDirectEndpoints(service, directNetwork);
        assertNetworkControl(service);
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

        var passwordProperty = "mysql.password";
        var passwordEnvVariable = "MYSQL_PASSWORD";

        var config = SQLDatabaseDockerServiceConfig
                .builder()
                .name(MYSQL_SERVICE_NAME)
                .dockerImage(MYSQL_SERVICE_IMAGE)
                .startupTimeout(MYSQL_STARTUP_TIMEOUT)
                .scheme(MYSQL_SCHEME)
                .databaseSchemas(schemas)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of(usernameEnvVariable))
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of(passwordEnvVariable))
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createDatabaseService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, MYSQL_SERVICE_NAME, MYSQL_SCHEME, MYSQL_LISTENING_PORT);
        var directNetwork = givenNetworkConfigured(networkConfigurer, MYSQL_DIRECT_NETWORK, MYSQL_SCHEME, MYSQL_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = network.getEndpoints().getFirst();
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());

        var usernamePropertyEntry = ConfigurationEntry.property(usernameProperty, MYSQL_USERNAME);
        var usernameEnvVariableEntry = ConfigurationEntry.envVariable(usernameEnvVariable, MYSQL_USERNAME);

        var passwordPropertyEntry = ConfigurationEntry.property(passwordProperty, MYSQL_PASSWORD);
        var passwordEnvVariableEntry = ConfigurationEntry.envVariable(passwordEnvVariable, MYSQL_PASSWORD);

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointPropertyEntry,
                endpointEnvVariableEntry,
                usernamePropertyEntry,
                usernameEnvVariableEntry,
                passwordPropertyEntry,
                passwordEnvVariableEntry
        );

        var envVariables = Map.of(
                MYSQL_PASSWORD_ENV, MYSQL_PASSWORD
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, MYSQL_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerStartupTimeoutConfigured(container, MYSQL_STARTUP_TIMEOUT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, MYSQL_SERVICE_NAME);
        assertEndpoints(service, network);
        assertDirectEndpoints(service, directNetwork);
        assertNetworkControl(service);
        assertSchemasInitialized(schemaInitializer, service, MYSQL_CREDENTIALS, schemas);
        assertEntriesRegistered(service, registeredEntries);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = SQLDatabaseDockerServiceConfig
                .builder()
                .name(MYSQL_SERVICE_NAME)
                .dockerImage(MYSQL_SERVICE_IMAGE)
                .startupTimeout(MYSQL_STARTUP_TIMEOUT)
                .scheme(MYSQL_SCHEME)
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

        // when
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    private MySQLDockerService createDatabaseService(SQLDatabaseDockerServiceConfig config,
                                                     GenericContainer<?> container) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);

        return new MySQLDockerService(
                container,
                dockerNetwork,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                credentialsRegisterer,
                schemaInitializer
        );
    }
}
