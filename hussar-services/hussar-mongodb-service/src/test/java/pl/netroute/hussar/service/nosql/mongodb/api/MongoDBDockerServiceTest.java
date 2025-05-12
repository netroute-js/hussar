package pl.netroute.hussar.service.nosql.mongodb.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.GenericContainerStubHelper.GenericContainerAccessibility;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.core.assertion.helper.NetworkConfigurerAssertionHelper.assertNetworkConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
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

public class MongoDBDockerServiceTest {
    private static final String MONGO_DB_HOST = "localhost";
    private static final int MONGO_DB_LISTENING_PORT = 27017;
    private static final int MONGO_DB_MAPPED_PORT = 27000;

    private static final String MONGO_DB_ENDPOINT_WITH_CREDENTIALS_TEMPLATE = "%s%s:%s@%s:%d";

    private static final String MONGO_DB_SERVICE_NAME = "mongodb-service";
    private static final String MONGO_DB_SERVICE_IMAGE = "mongo";

    private static final String MONGO_DB_SCHEME = "mongodb://";

    private static final String MONGO_DB_USERNAME_ENV = "MONGO_INITDB_ROOT_USERNAME";
    private static final String MONGO_DB_PASSWORD_ENV = "MONGO_INITDB_ROOT_PASSWORD";

    private static final String MONGO_DB_USERNAME = "mongo";
    private static final String MONGO_DB_PASSWORD = "test";

    private NetworkConfigurer networkConfigurer;

    private GenericContainerAccessibility containerAccessibility;

    @BeforeEach
    public void setup() {
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);

        containerAccessibility = GenericContainerAccessibility
                .builder()
                .host(MONGO_DB_HOST)
                .exposedPort(MONGO_DB_LISTENING_PORT)
                .mappedPort(MONGO_DB_LISTENING_PORT, MONGO_DB_MAPPED_PORT)
                .build();
    }

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
                .registerEndpointWithCredentialsUnderProperties(Set.of())
                .registerEndpointWithCredentialsUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var endpoint = Endpoint.of(MONGO_DB_SCHEME, MONGO_DB_HOST, MONGO_DB_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createMongoDBService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, MONGO_DB_SERVICE_NAME, endpoint);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
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
        assertNetworkControl(service);
        assertNoEntriesRegistered(service);
        assertNetworkConfigured(networkConfigurer, MONGO_DB_SERVICE_NAME, endpoint);
    }

    @Test
    public void shouldStartExtendedService() {
        // given
        var endpointProperty = "endpoint.url";
        var endpointEnvVariable = "ENDPOINT_URL";

        var endpointWithCredentialsProperty = "endpoint.credentials.url";
        var endpointWithCredentialsEnvVariable = "ENDPOINT_CREDENTIALS_URL";

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
                .registerEndpointWithCredentialsUnderProperties(Set.of(endpointWithCredentialsProperty))
                .registerEndpointWithCredentialsUnderEnvironmentVariables(Set.of(endpointWithCredentialsEnvVariable))
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of(usernameEnvVariable))
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of(passwordEnvVariable))
                .build();

        var endpoint = Endpoint.of(MONGO_DB_SCHEME, MONGO_DB_HOST, MONGO_DB_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createMongoDBService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, MONGO_DB_SERVICE_NAME, endpoint);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());

        var endpointWithCredentials = MONGO_DB_ENDPOINT_WITH_CREDENTIALS_TEMPLATE.formatted(MONGO_DB_SCHEME, MONGO_DB_USERNAME, MONGO_DB_PASSWORD, MONGO_DB_HOST, MONGO_DB_MAPPED_PORT);
        var endpointWithCredentialsPropertyEntry = ConfigurationEntry.property(endpointWithCredentialsProperty, endpointWithCredentials);
        var endpointWithCredentialsEnvVariableEntry = ConfigurationEntry.envVariable(endpointWithCredentialsEnvVariable, endpointWithCredentials);

        var usernamePropertyEntry = ConfigurationEntry.property(usernameProperty, MONGO_DB_USERNAME);
        var usernameEnvVariableEntry = ConfigurationEntry.envVariable(usernameEnvVariable, MONGO_DB_USERNAME);

        var passwordPropertyEntry = ConfigurationEntry.property(passwordProperty, MONGO_DB_PASSWORD);
        var passwordEnvVariableEntry = ConfigurationEntry.envVariable(passwordEnvVariable, MONGO_DB_PASSWORD);

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointPropertyEntry,
                endpointEnvVariableEntry,
                endpointWithCredentialsPropertyEntry,
                endpointWithCredentialsEnvVariableEntry,
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
        assertNetworkControl(service);
        assertEntriesRegistered(service, registeredEntries);
        assertNetworkConfigured(networkConfigurer, MONGO_DB_SERVICE_NAME, endpoint);
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
                .registerEndpointWithCredentialsUnderProperties(Set.of())
                .registerEndpointWithCredentialsUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createMongoDBService(config, container);

        givenContainerAccessible(container, containerAccessibility);

        // when
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
                .registerEndpointWithCredentialsUnderProperties(Set.of())
                .registerEndpointWithCredentialsUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
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
        var endpointWithCredentialsRegisterer = new MongoDBEndpointWithCredentialsRegisterer(configurationRegistry);
        var credentialsRegisterer = new MongoDBCredentialsRegisterer(configurationRegistry);

        return new MongoDBDockerService(container, config, configurationRegistry, endpointRegisterer, networkConfigurer, endpointWithCredentialsRegisterer, credentialsRegisterer);
    }

    private void assertCredentials(MongoDBCredentials credentials) {
        assertThat(credentials.username()).isEqualTo(MONGO_DB_USERNAME);
        assertThat(credentials.password()).isEqualTo(MONGO_DB_PASSWORD);
    }
}
