package pl.netroute.hussar.spring.boot;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.lock.LockedAction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SpringApplication implements Application {
    private static final String HOSTNAME = "localhost";

    private final Class<?> applicationClass;
    private final LockedAction lockedAction;

    private ConfigurableApplicationContext applicationContext;

    private SpringApplication(Class<?> applicationClass) {
        Objects.requireNonNull(applicationClass, "applicationClass is required");

        this.applicationClass = applicationClass;
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
                .getProperty(SpringProperties.SERVER_PORT, Integer.class);

        return Endpoint.of(SchemesHelper.HTTP_SCHEME, HOSTNAME, port);
    }

    private ConfigurableApplicationContext initializeApplication(ApplicationStartupContext startupContext) {
        var properties = SpringProperties.withDynamicPort(startupContext.getProperties());

        return new SpringApplicationBuilder(applicationClass)
                .initializers(context -> new PropertySourceConfigurer().configure(properties, context))
                .run();
    }

    public static SpringApplication newApplication(Class<?> applicationClass) {
        return new SpringApplication(applicationClass);
    }

}
