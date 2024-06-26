package pl.netroute.hussar.spring.boot;

import lombok.NonNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.lock.LockedAction;

import java.util.List;
import java.util.Optional;

public class SpringApplication implements Application {
    private static final String HOSTNAME = "localhost";

    private final Class<?> applicationClass;
    private final PropertySourceConfigurer propertySourceConfigurer;
    private final LockedAction lockedAction;

    private ConfigurableApplicationContext applicationContext;

    private SpringApplication(@NonNull Class<?> applicationClass,
                              @NonNull PropertySourceConfigurer propertySourceConfigurer) {
        this.applicationClass = applicationClass;
        this.propertySourceConfigurer = propertySourceConfigurer;
        this.lockedAction = new LockedAction();
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

    private Endpoint resolveEndpoint() {
        var port = applicationContext
                .getEnvironment()
                .getProperty(DynamicConfigurationConfigurer.SERVER_PORT, Integer.class);

        return Endpoint.of(SchemesHelper.HTTP_SCHEME, HOSTNAME, port);
    }

    private ConfigurableApplicationContext initializeApplication(ApplicationStartupContext startupContext) {
        var externalConfigurations = DynamicConfigurationConfigurer.configure(startupContext.externalConfigurations());

        return new SpringApplicationBuilder(applicationClass)
                .initializers(context -> propertySourceConfigurer.configure(externalConfigurations, context))
                .run();
    }

    public static SpringApplication newApplication(@NonNull Class<?> applicationClass) {
        var configurationResolver = new ConfigurationResolver();
        var propertySourceConfigurer = new PropertySourceConfigurer(configurationResolver);

        return new SpringApplication(applicationClass, propertySourceConfigurer);
    }

}
