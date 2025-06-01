package pl.netroute.hussar.service.rabbitmq.api;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.BaseDockerService;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;
import java.util.Optional;

/**
 * Hussar Docker {@link Service} representing RabbitMQ.
 */
public class RabbitMQDockerService extends BaseDockerService<RabbitMQDockerServiceConfig> {
    private static final int LISTENING_PORT = 5672;
    private static final int MANAGEMENT_API_LISTENING_PORT = 15672;

    private static final String RABBITMQ_DEFAULT_USER_ENV = "RABBITMQ_DEFAULT_USER";
    private static final String RABBITMQ_DEFAULT_PASS_ENV = "RABBITMQ_DEFAULT_PASS";

    private static final String RABBITMQ_USERNAME = "guest";
    private static final String RABBITMQ_PASSWORD = "password";

    @NonNull
    private final RabbitMQCredentialsRegisterer credentialsRegisterer;

    @NonNull
    private final RabbitMQQueueConfigurer queueConfigurer;

    @Getter
    @NonNull
    private final RabbitMQCredentials credentials;

    /**
     * Creates new {@link RabbitMQDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link RabbitMQDockerService}.
     * @param dockerNetwork - the {@link DockerNetwork} used by this {@link RabbitMQDockerService}.
     * @param config - the {@link RabbitMQDockerServiceConfig} used by this {@link RabbitMQDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link RabbitMQDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link RabbitMQDockerService}.
     * @param networkConfigurer - the  {@link NetworkConfigurer} used by this {@link RabbitMQDockerService}.
     * @param credentialsRegisterer - the {@link RabbitMQCredentialsRegisterer} used by this {@link RabbitMQDockerService}.
     * @param queueConfigurer - the {@link RabbitMQQueueConfigurer} used by this {@link RabbitMQDockerService}.
     */
    RabbitMQDockerService(@NonNull GenericContainer<?> container,
                          @NonNull DockerNetwork dockerNetwork,
                          @NonNull RabbitMQDockerServiceConfig config,
                          @NonNull ConfigurationRegistry configurationRegistry,
                          @NonNull EndpointRegisterer endpointRegisterer,
                          @NonNull NetworkConfigurer networkConfigurer,
                          @NonNull RabbitMQCredentialsRegisterer credentialsRegisterer,
                          @NonNull RabbitMQQueueConfigurer queueConfigurer) {
        super(container, dockerNetwork, config, configurationRegistry, endpointRegisterer, networkConfigurer);

        this.credentialsRegisterer = credentialsRegisterer;
        this.queueConfigurer = queueConfigurer;

        this.credentials = new RabbitMQCredentials(RABBITMQ_USERNAME, RABBITMQ_PASSWORD);
    }

    @Override
    protected List<Endpoint> getInternalEndpoints() {
        var endpoints = super.getInternalEndpoints();

        if(ManagementApiResolver.isSupported(config)) {
            return endpoints
                    .stream()
                    .filter(endpoint -> endpoint.port() != MANAGEMENT_API_LISTENING_PORT)
                    .toList();
        }

        return endpoints;
    }

    @Override
    protected List<Integer> getInternalPorts() {
        if(ManagementApiResolver.isSupported(config)) {
            return List.of(LISTENING_PORT, MANAGEMENT_API_LISTENING_PORT);
        }

        return List.of(LISTENING_PORT);
    }

    @Override
    protected void configureEnvVariables(GenericContainer<?> container) {
        super.configureEnvVariables(container);

        container.withEnv(RABBITMQ_DEFAULT_USER_ENV, RABBITMQ_USERNAME);
        container.withEnv(RABBITMQ_DEFAULT_PASS_ENV, RABBITMQ_PASSWORD);
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        var connectionFactory = createConnectionFactory();

        configureQueues(connectionFactory);

        registerCredentialsUnderProperties();
        registerCredentialsUnderEnvironmentVariables();
        registerManagementEndpointUnderProperties();
        registerManagementEndpointUnderEnvironmentVariables();
    }

    /**
     * Gets management endpoint.
     *
     * @return the management endpoint
     */
    public Optional<Endpoint> getManagementEndpoint() {
        if(ManagementApiResolver.isSupported(config)) {
            var host = container.getHost();
            var port = container.getMappedPort(MANAGEMENT_API_LISTENING_PORT);
            var endpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, host, port);

            return Optional.of(endpoint);
        }

        return Optional.empty();
    }

    private ConnectionFactory createConnectionFactory() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(this);

        var connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(endpoint.host());
        connectionFactory.setPort(endpoint.port());
        connectionFactory.setUsername(credentials.username());
        connectionFactory.setPassword(credentials.password());

        return connectionFactory;
    }

    private void configureQueues(ConnectionFactory connectionFactory) {
        var queues = config.getQueues();

        queues.forEach(queue -> queueConfigurer.configure(connectionFactory, queue));
    }

    private void registerCredentialsUnderProperties() {
        config.getRegisterUsernameUnderProperties()
              .forEach(usernameProperty -> credentialsRegisterer.registerUsernameUnderProperty(credentials, usernameProperty));

        config.getRegisterPasswordUnderProperties()
              .forEach(passwordProperty -> credentialsRegisterer.registerPasswordUnderProperty(credentials, passwordProperty));
    }

    private void registerCredentialsUnderEnvironmentVariables() {
        config.getRegisterUsernameUnderEnvironmentVariables()
              .forEach(usernameEnvVariable -> credentialsRegisterer.registerUsernameUnderEnvironmentVariable(credentials, usernameEnvVariable));

        config.getRegisterPasswordUnderEnvironmentVariables()
              .forEach(passwordEnvVariable -> credentialsRegisterer.registerPasswordUnderEnvironmentVariable(credentials, passwordEnvVariable));
    }

    private void registerManagementEndpointUnderProperties() {
        if(ManagementApiResolver.isSupported(config)) {
            var endpoints = getManagementEndpoint()
                    .map(List::of)
                    .orElse(List.of());

            config.getRegisterManagementEndpointUnderProperties()
                  .forEach(endpointProperty -> endpointRegisterer.registerUnderProperty(endpoints, endpointProperty));
        }
    }

    private void registerManagementEndpointUnderEnvironmentVariables() {
        if(ManagementApiResolver.isSupported(config)) {
            var endpoints = getManagementEndpoint()
                    .map(List::of)
                    .orElse(List.of());

            config.getRegisterManagementEndpointUnderEnvironmentVariables()
                  .forEach(endpointEnvVariable -> endpointRegisterer.registerUnderEnvironmentVariable(endpoints, endpointEnvVariable));
        }
    }

}
