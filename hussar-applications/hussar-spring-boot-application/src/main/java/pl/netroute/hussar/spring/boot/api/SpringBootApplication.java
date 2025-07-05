package pl.netroute.hussar.spring.boot.api;

import lombok.NonNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.application.ApplicationStartupContext;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.dependency.api.DependencyInjector;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.lock.LockedAction;

import java.util.List;
import java.util.Optional;

import static pl.netroute.hussar.spring.boot.api.DynamicConfigurationConfigurer.SERVER_PORT;

/**
 * An actual implementation of {@link Application}. It guarantees seamless integration for testing Spring Boot applications.
 */
public class SpringBootApplication implements Application {
    private static final String HOSTNAME = "localhost";

    private final Class<?> applicationClass;
    private final CommandLineArgumentConfigurer argumentConfigurer;
    private final LockedAction lockedAction;

    private ConfigurableApplicationContext applicationContext;
    private ApplicationStartupContext applicationStartupContext;
    private SpringBootDependencyInjector dependencyInjector;

    private SpringBootApplication(@NonNull Class<?> applicationClass,
                                  @NonNull CommandLineArgumentConfigurer argumentConfigurer) {
        this.applicationClass = applicationClass;
        this.argumentConfigurer = argumentConfigurer;
        this.lockedAction = new LockedAction();
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return lockedAction.sharedAction(() -> {
            if(isInitialized()) {
                return resolveEndpoint()
                        .map(List::of)
                        .orElse(List.of());
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
    public void start(@NonNull ApplicationStartupContext context) {
        lockedAction.exclusiveAction(() ->
            Optional
                    .ofNullable(applicationContext)
                    .ifPresentOrElse(
                            appContext -> {},
                            () -> doStart(context)
                    )
        );
    }

    @Override
    public void restart() {
        shutdown();
        start(applicationStartupContext);
    }

    @Override
    public void shutdown() {
        lockedAction.exclusiveAction(() ->
                Optional
                        .ofNullable(applicationContext)
                        .ifPresent(this::doShutdown)
        );
    }

    @Override
    public DependencyInjector getDependencyInjector() {
        return dependencyInjector;
    }

    private Optional<Endpoint> resolveEndpoint() {
        return Optional
                .ofNullable(applicationContext.getEnvironment().getProperty(SERVER_PORT, Integer.class))
                .map(port -> Endpoint.of(SchemesHelper.HTTP_SCHEME, HOSTNAME, port));
    }

    private ConfigurableApplicationContext initializeApplication(ApplicationStartupContext startupContext) {
        var externalConfigurations = DynamicConfigurationConfigurer.configure(startupContext.externalConfigurations());
        var commandLineArguments = argumentConfigurer.configure(externalConfigurations);

        return new SpringApplicationBuilder(applicationClass)
                .addCommandLineProperties(true)
                .run(commandLineArguments.toArray(new String[0]));
    }

    private void doStart(ApplicationStartupContext context) {
        this.applicationStartupContext = context;
        this.applicationContext = initializeApplication(context);
        this.dependencyInjector = new SpringBootDependencyInjector(applicationContext);
    }

    private void doShutdown(ConfigurableApplicationContext applicationContext) {
        applicationContext.close();

        this.applicationContext = null;
    }

    /**
     * Factory method to create {@link SpringBootApplication}.
     *
     * @param applicationClass - the main class of your Spring Boot application.
     * @return the instance of {@link SpringBootApplication}
     */
    public static SpringBootApplication newApplication(@NonNull Class<?> applicationClass) {
        var argumentConfigurer = new CommandLineArgumentConfigurer();

        return new SpringBootApplication(applicationClass, argumentConfigurer);
    }

}
