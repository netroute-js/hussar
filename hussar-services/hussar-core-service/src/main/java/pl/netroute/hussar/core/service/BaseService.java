package pl.netroute.hussar.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceStartupContext;

import java.util.Objects;

public abstract class BaseService<C extends BaseServiceConfig> implements Service {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final C config;
    protected final ConfigurationRegistry configurationRegistry;

    public BaseService(C config) {
        Objects.requireNonNull(config, "config is required");

        this.config = config;
        this.configurationRegistry = new MapConfigurationRegistry();
    }

    @Override
    public final void start(ServiceStartupContext context) {
        log.info("Starting {} Service", getName());

        doBeforeServiceStartup(context);
        bootstrapService(context);
        doAfterServiceStartup(context);
    }

    @Override
    public final void shutdown() {
        log.info("Stopping {} Service", getName());

        doBeforeServiceShutdown();
        shutdownService();
        doAfterServiceShutdown();
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
    }

    protected void doBeforeServiceShutdown() {
    }

    protected void doAfterServiceShutdown() {
    }

    protected abstract void bootstrapService(ServiceStartupContext context);
    protected abstract void shutdownService();
}
