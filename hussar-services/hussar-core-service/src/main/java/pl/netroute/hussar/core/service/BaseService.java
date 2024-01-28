package pl.netroute.hussar.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;

import java.util.Objects;

public abstract class BaseService<C extends BaseServiceConfig> implements Service {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final C config;
    protected final ConfigurationRegistry configurationRegistry;

    private final EndpointRegisterer endpointRegisterer;

    public BaseService(C config) {
        Objects.requireNonNull(config, "config is required");

        this.config = config;
        this.configurationRegistry = new MapConfigurationRegistry();
        this.endpointRegisterer = new EndpointRegisterer(configurationRegistry);
    }

    @Override
    public final void start(ServiceStartupContext context) {
        var serviceName = getName();

        log.info("Starting {} Service", serviceName);

        doBeforeServiceStartup(context);
        bootstrapService(context);
        doAfterServiceStartup(context);

        log.info("Started {} Service", serviceName);
    }

    @Override
    public final void shutdown() {
        var serviceName = getName();

        log.info("Stopping {} Service", serviceName);

        doBeforeServiceShutdown();
        shutdownService();
        doAfterServiceShutdown();

        log.info("Stopped {} Service", serviceName);
    }

    @Override
    public final ConfigurationRegistry getConfigurationRegistry() {
        return configurationRegistry;
    }

    @Override
    public final String getName() {
        return config.getName();
    }

    protected void doBeforeServiceStartup(ServiceStartupContext context) {
    }

    protected void doAfterServiceStartup(ServiceStartupContext context) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(this);

        registerEndpointUnderProperties(endpoint);
        registerEndpointUnderEnvironmentVariables(endpoint);
    }

    protected void doBeforeServiceShutdown() {
    }

    protected void doAfterServiceShutdown() {
    }

    private void registerEndpointUnderProperties(Endpoint endpoint) {
        config
                .getRegisterEndpointUnderProperties()
                .forEach(endpointProperty -> endpointRegisterer.registerUnderProperty(endpoint, endpointProperty));
    }

    private void registerEndpointUnderEnvironmentVariables(Endpoint endpoint) {
        config
                .getRegisterEndpointUnderEnvironmentVariables()
                .forEach(endpointEnvVariable -> endpointRegisterer.registerUnderEnvironmentVariable(endpoint, endpointEnvVariable));
    }

    protected abstract void bootstrapService(ServiceStartupContext context);
    protected abstract void shutdownService();
}
