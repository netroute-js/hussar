package pl.netroute.hussar.core.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

@RequiredArgsConstructor
public abstract class BaseService<C extends BaseServiceConfig> implements Service {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @NonNull
    protected final C config;

    @NonNull
    protected final ConfigurationRegistry configurationRegistry;

    @NonNull
    protected final EndpointRegisterer endpointRegisterer;

    @Override
    public final void start(@NonNull ServiceStartupContext context) {
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
