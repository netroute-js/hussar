package pl.netroute.hussar.spring.boot;

import org.springframework.context.ConfigurableApplicationContext;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.lock.LockedAction;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.helper.PropertiesHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SpringApplication implements Application {
    private static final String HOSTNAME = "localhost";

    private final Class<?> applicationClass;
    private final RandomPortConfigurer portConfigurer;
    private final LockedAction lockedAction;

    private ConfigurableApplicationContext applicationContext;

    private SpringApplication(Class<?> applicationClass) {
        Objects.requireNonNull(applicationClass, "applicationClass is required");

        this.applicationClass = applicationClass;
        this.portConfigurer = new RandomPortConfigurer();
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
    public void start() {
        lockedAction.exclusiveAction(() ->
            Optional
                    .ofNullable(applicationContext)
                    .ifPresentOrElse(
                            appContext -> {},
                            () -> this.applicationContext = initializeApplication()
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
        var port = PropertiesHelper
                .getIntProperty(SpringProperties.SERVER_PORT)
                .orElseThrow(() -> new IllegalStateException("Could not resolve SpringBoot's application port"));

        return Endpoint.of(SchemesHelper.HTTP_SCHEME, HOSTNAME, port);
    }

    private ConfigurableApplicationContext initializeApplication() {
        portConfigurer.configure();

        return new org.springframework.boot.SpringApplication(applicationClass).run();
    }

    public static SpringApplication newApplication(Class<?> applicationClass) {
        return new SpringApplication(applicationClass);
    }

}
