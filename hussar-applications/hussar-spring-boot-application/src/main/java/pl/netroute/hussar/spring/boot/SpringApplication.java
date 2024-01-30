package pl.netroute.hussar.spring.boot;

import lombok.NonNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.lock.LockedAction;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class SpringApplication implements Application {
    private static final String HOSTNAME = "localhost";

    private final Class<?> applicationClass;
    private final Path configurationFile;
    private final LockedAction lockedAction;

    private ConfigurableApplicationContext applicationContext;

    private SpringApplication(@NonNull Class<?> applicationClass,
                              Path configurationFile) {
        if(!isConfigurationFilePresent(configurationFile)) {
            configurationFile = null;
        }

        this.applicationClass = applicationClass;
        this.configurationFile = configurationFile;
        this.lockedAction = new LockedAction();
    }

    @Override
    public Optional<Path> getConfigurationFile() {
        return Optional.ofNullable(configurationFile);
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return lockedAction.sharedAction(() -> {
            if(isInitialized()) {
                return List.of(resolveEndpoint());
            }

            return List.of();
        });
    }

    @Override
    public boolean isInitialized() {
        return lockedAction.sharedAction(() ->
                Optional
                        .ofNullable(applicationContext)
                        .map(ConfigurableApplicationContext::isActive)
                        .orElse(Boolean.FALSE)
        );
    }

    @Override
    public void start(ApplicationStartupContext context) {
        lockedAction.exclusiveAction(() ->
            Optional
                    .ofNullable(applicationContext)
                    .ifPresentOrElse(
                            appContext -> {},
                            () -> this.applicationContext = initializeApplication(context)
                    )
        );
    }

    @Override
    public void shutdown() {
        lockedAction.exclusiveAction(() ->
                Optional
                        .ofNullable(applicationContext)
                        .ifPresent(ConfigurableApplicationContext::close)
        );
    }

    private boolean isConfigurationFilePresent(Path configurationFile) {
        return Optional
                .ofNullable(configurationFile)
                .map(Path::toFile)
                .map(File::exists)
                .orElse(Boolean.FALSE);
    }

    private Endpoint resolveEndpoint() {
        var port = applicationContext
                .getEnvironment()
                .getProperty(PropertiesFactory.SERVER_PORT, Integer.class);

        return Endpoint.of(SchemesHelper.HTTP_SCHEME, HOSTNAME, port);
    }

    private ConfigurableApplicationContext initializeApplication(ApplicationStartupContext startupContext) {
        var properties = PropertiesFactory.createWithDynamicPort(startupContext.properties());

        return new SpringApplicationBuilder(applicationClass)
                .initializers(context -> new PropertySourceConfigurer().configure(properties, context))
                .run();
    }

    public static SpringApplication newApplication(@NonNull Class<?> applicationClass) {
        var configurationFile = new ConfigurationFileResolver()
                .resolveDefault(applicationClass)
                .orElse(null);

        return new SpringApplication(applicationClass, configurationFile);
    }

    public static SpringApplication newApplication(@NonNull Class<?> applicationClass,
                                                   @NonNull Path configurationFile) {
        return new SpringApplication(applicationClass, configurationFile);
    }

}
