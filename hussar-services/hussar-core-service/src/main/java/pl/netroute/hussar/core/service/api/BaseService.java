package pl.netroute.hussar.core.service.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.network.api.Network;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.network.api.NetworkControl;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;
import java.util.Optional;

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

    @NonNull
    protected final NetworkConfigurer networkConfigurer;

    protected Network network;

    @Override
    public final void start(@NonNull ServiceStartupContext context) {
        var serviceName = getName();

        log.info("Starting {} Service", serviceName);

        doBeforeServiceStartup(context);
        bootstrapService(context);
        configureNetwork();
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
    public final List<Endpoint> getEndpoints() {
        return Optional
                .ofNullable(network)
                .map(Network::getEndpoints)
                .orElse(List.of());
    }

    @Override
    public final ConfigurationRegistry getConfigurationRegistry() {
        return configurationRegistry;
    }

    @Override
    public NetworkControl getNetworkControl() {
        return Optional
                .ofNullable(network)
                .map(Network::getNetworkControl)
                .orElseThrow(() -> new IllegalStateException("NetworkControl could not be resolved. The Service has to be started first."));
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
        var endpoints = getEndpoints();

        registerEndpointUnderProperties(endpoints);
        registerEndpointUnderEnvironmentVariables(endpoints);
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

    private void configureNetwork() {
        var networkPrefix = config.getName();
        var endpoints = getInternalEndpoints();

        this.network = networkConfigurer.configure(networkPrefix, endpoints);
    }

    private void registerEndpointUnderProperties(List<Endpoint> endpoints) {
        config
                .getRegisterEndpointUnderProperties()
                .forEach(endpointProperty -> endpointRegisterer.registerUnderProperty(endpoints, endpointProperty));
    }

    private void registerEndpointUnderEnvironmentVariables(List<Endpoint> endpoints) {
        config
                .getRegisterEndpointUnderEnvironmentVariables()
                .forEach(endpointEnvVariable -> endpointRegisterer.registerUnderEnvironmentVariable(endpoints, endpointEnvVariable));
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

    protected abstract List<Endpoint> getInternalEndpoints();
}
