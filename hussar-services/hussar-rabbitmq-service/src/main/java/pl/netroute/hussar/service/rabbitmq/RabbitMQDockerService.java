package pl.netroute.hussar.service.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQCredentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RabbitMQDockerService extends BaseDockerService<RabbitMQDockerServiceConfig> {
    private static final int LISTENING_PORT = 5672;
    private static final int MANAGEMENT_API_LISTENING_PORT = 15672;

    private static final String RABBITMQ_DEFAULT_USER_ENV = "RABBITMQ_DEFAULT_USER";
    private static final String RABBITMQ_DEFAULT_PASS_ENV = "RABBITMQ_DEFAULT_PASS";

    private static final String RABBITMQ_USERNAME = "guest";
    private static final String RABBITMQ_PASSWORD = "password";

    private static final String RABBITMQ_MANAGEMENT_VERSION = "management";

    private final RabbitMQCredentialsRegisterer credentialsRegisterer;
    private final RabbitMQQueueConfigurer queueConfigurer;
    private final RabbitMQCredentials credentials;

    RabbitMQDockerService(@NonNull GenericContainer<?> container,
                          @NonNull RabbitMQDockerServiceConfig config,
                          @NonNull ConfigurationRegistry configurationRegistry,
                          @NonNull EndpointRegisterer endpointRegisterer,
                          @NonNull RabbitMQCredentialsRegisterer credentialsRegisterer,
                          @NonNull RabbitMQQueueConfigurer queueConfigurer) {
        super(container, config, configurationRegistry, endpointRegisterer);

        this.credentialsRegisterer = credentialsRegisterer;
        this.queueConfigurer = queueConfigurer;

        this.credentials = new RabbitMQCredentials(RABBITMQ_USERNAME, RABBITMQ_PASSWORD);
    }

    @Override
    public List<Endpoint> getEndpoints() {
        var endpoints = super.getEndpoints();

        if(isManagementApiSupported()) {
            var managementApiPort = container.getMappedPort(MANAGEMENT_API_LISTENING_PORT);

            return endpoints
                    .stream()
                    .filter(endpoint -> endpoint.port() != managementApiPort)
                    .toList();
        }

        return endpoints;
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        configureExposedPorts(container);

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
    }

    public RabbitMQCredentials getCredentials() {
        return credentials;
    }

    public Optional<Endpoint> getManagementEndpoint() {
        if(isManagementApiSupported()) {
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

    private void configureExposedPorts(GenericContainer<?> container) {
        var exposedPorts = new ArrayList<Integer>();
        exposedPorts.add(LISTENING_PORT);

        if(isManagementApiSupported()) {
            exposedPorts.add(MANAGEMENT_API_LISTENING_PORT);
        }

        container.withExposedPorts(exposedPorts.toArray(new Integer[0]));
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

    private boolean isManagementApiSupported() {
        var dockerImage = config.getDockerImage();

        return dockerImage.contains(RABBITMQ_MANAGEMENT_VERSION);
    }

}