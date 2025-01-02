package pl.netroute.hussar.core.service;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceStartupContext;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

/**
 * A base class with default implementation/template for all Hussar {@link Service}.
 *
 * @param <C> the {@link BaseServiceConfig} parameter used by the {@link BaseService}.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseService<C extends BaseServiceConfig> implements Service {

    /**
     * An instance of a {@link Logger}.
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * An instance of {@link BaseServiceConfig} used by this {@link Service}.
     */
    @NonNull
    protected final C config;

    /**
     * An instance of {@link ConfigurationRegistry} used by this {@link Service}.
     * All the dynamic {@link ConfigurationEntry} will be registered in this instance.
     */
    @NonNull
    protected final ConfigurationRegistry configurationRegistry;

    /**
     * An instance of {@link EndpointRegisterer} used by this {@link Service}.
     * The {@link Endpoint} of this {@link Service} will be registered in this instance - if configured.
     */
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

    /**
     * A hook method to be invoked before the {@link Service} is started.
     *
     * @param context - the context used to pass additional data.
     */
    protected void doBeforeServiceStartup(ServiceStartupContext context) {
    }

    /**
     * A hook method to be invoked after the {@link Service} is started.
     *
     * @param context - the context used to pass additional data.
     */
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(this);

        registerEndpointUnderProperties(endpoint);
        registerEndpointUnderEnvironmentVariables(endpoint);
    }

    /**
     * A hook method to be invoked before the {@link Service} is shutdown.
     */
    protected void doBeforeServiceShutdown() {
    }

    /**
     * A hook method to be invoked after the {@link Service} is shutdown.
     */
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

    /**
     * Bootstraps {@link Service}.
     *
     * @param context - the context used to pass additional data.
     */
    protected abstract void bootstrapService(ServiceStartupContext context);

    /**
     * Shutdowns {@link Service}.
     */
    protected abstract void shutdownService();
}
