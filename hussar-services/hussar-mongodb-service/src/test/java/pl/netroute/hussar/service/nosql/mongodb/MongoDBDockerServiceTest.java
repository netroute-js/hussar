package pl.netroute.hussar.service.nosql.mongodb;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBCredentials;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertSingleEndpoint;

public class MongoDBDockerServiceTest {
    private static final String MONGO_DB_HOST = "localhost";
    private static final int MONGO_DB_LISTENING_PORT = 27017;
    private static final int MONGO_DB_MAPPED_PORT = 27000;

    private static final String MONGO_DB_SERVICE_NAME = "mongodb-service";
    private static final String MONGO_DB_SERVICE_IMAGE = "mongo";

    private static final String MONGO_DB_SCHEME = "mongodb://";

    private static final String MONGO_DB_USERNAME_ENV = "MONGO_INITDB_ROOT_USERNAME";
    private static final String MONGO_DB_PASSWORD_ENV = "MONGO_INITDB_ROOT_PASSWORD";

    private static final String MONGO_DB_USERNAME = "mongo";
    private static final String MONGO_DB_PASSWORD = "test";

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = MongoDBDockerServiceConfig
                .builder()
                .name(MONGO_DB_SERVICE_NAME)
                .dockerImage(MONGO_DB_SERVICE_IMAGE)
                .scheme(MONGO_DB_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var service = createMongoDBService(config, container);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = Endpoint.of(MONGO_DB_SCHEME, MONGO_DB_HOST, MONGO_DB_MAPPED_PORT);
        var envVariables = Map.of(
            MONGO_DB_USERNAME_ENV, MONGO_DB_USERNAME,
            MONGO_DB_PASSWORD_ENV, MONGO_DB_PASSWORD
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, MONGO_DB_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, MONGO_DB_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNoEntriesRegistered(service);
    }

    @Test
    public void shouldStartExtendedService() {
        // given
        var endpointProperty = "endpoint.url";
        var endpointEnvVariable = "ENDPOINT_URL";

        var usernameProperty = "mongo.username";
        var usernameEnvVariable = "MONGO_USERNAME";

        var passwordProperty = "mongo.password";
        var passwordEnvVariable = "MONGO_PASSWORD";

        var config = MongoDBDockerServiceConfig
                .builder()
                .name(MONGO_DB_SERVICE_NAME)
                .dockerImage(MONGO_DB_SERVICE_IMAGE)
                .scheme(MONGO_DB_SCHEME)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of(usernameEnvVariable))
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of(passwordEnvVariable))
                .build();

        var container = createStubContainer();
        var service = createMongoDBService(config, container);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = Endpoint.of(MONGO_DB_SCHEME, MONGO_DB_HOST, MONGO_DB_MAPPED_PORT);
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());

        var usernamePropertyEntry = ConfigurationEntry.property(usernameProperty, MONGO_DB_USERNAME);
        var usernameEnvVariableEntry = ConfigurationEntry.envVariable(usernameEnvVariable, MONGO_DB_USERNAME);

        var passwordPropertyEntry = ConfigurationEntry.property(passwordProperty, MONGO_DB_PASSWORD);
        var passwordEnvVariableEntry = ConfigurationEntry.envVariable(passwordEnvVariable, MONGO_DB_PASSWORD);

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointPropertyEntry,
                endpointEnvVariableEntry,
                usernamePropertyEntry,
                usernameEnvVariableEntry,
                passwordPropertyEntry,
                passwordEnvVariableEntry
        );

        var envVariables = Map.of(
                MONGO_DB_USERNAME_ENV, MONGO_DB_USERNAME,
                MONGO_DB_PASSWORD_ENV, MONGO_DB_PASSWORD
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, MONGO_DB_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, MONGO_DB_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertEntriesRegistered(service, registeredEntries);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = MongoDBDockerServiceConfig
                .builder()
                .name(MONGO_DB_SERVICE_NAME)
                .dockerImage(MONGO_DB_SERVICE_IMAGE)
                .scheme(SchemesHelper.HTTP_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var service = createMongoDBService(config, container);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.defaultContext());
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    @Test
    public void shouldGetCredentials() {
        // given
        var config = MongoDBDockerServiceConfig
                .builder()
                .name(MONGO_DB_SERVICE_NAME)
                .dockerImage(MONGO_DB_SERVICE_IMAGE)
                .scheme(SchemesHelper.HTTP_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var service = createMongoDBService(config, container);

        // when
        var credentials = service.getCredentials();

        // then
        assertCredentials(credentials);
    }

    private MongoDBDockerService createMongoDBService(MongoDBDockerServiceConfig config,
                                                      GenericContainer<?> container) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new MongoDBCredentialsRegisterer(configurationRegistry);

        return new MongoDBDockerService(container, config, configurationRegistry, endpointRegisterer, credentialsRegisterer);
    }

    private GenericContainer<?> createStubContainer() {
        return mock(GenericContainer.class, RETURNS_DEEP_STUBS);
    }

    private void givenContainerAccessible(GenericContainer<?> container) {
        when(container.getHost()).thenReturn(MONGO_DB_HOST);
        when(container.getExposedPorts()).thenReturn(List.of(MONGO_DB_LISTENING_PORT));
        when(container.getMappedPort(MONGO_DB_LISTENING_PORT)).thenReturn(MONGO_DB_MAPPED_PORT);
    }

    private void assertCredentials(MongoDBCredentials credentials) {
        assertThat(credentials.username()).isEqualTo(MONGO_DB_USERNAME);
        assertThat(credentials.password()).isEqualTo(MONGO_DB_PASSWORD);
    }
}
