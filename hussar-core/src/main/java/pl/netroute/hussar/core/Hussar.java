package pl.netroute.hussar.core;

import lombok.NonNull;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.application.api.HussarApplicationRestart;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurerProvider;

import java.lang.reflect.Method;
import java.util.concurrent.ForkJoinPool;

/**
 * The main Hussar facade responsible for environment lifecycle management.
 */
public class Hussar {
    private final EnvironmentConfigurerProviderResolver configurerProviderResolver;
    private final EnvironmentOrchestrator environmentOrchestrator;
    private final EnvironmentRegistry environmentRegistry;
    private final ApplicationRestarter applicationRestarter;
    private final AnnotationDetector annotationDetector;

    Hussar(@NonNull EnvironmentConfigurerProviderResolver environmentConfigurerProviderResolver,
           @NonNull EnvironmentOrchestrator environmentOrchestrator,
           @NonNull EnvironmentRegistry environmentRegistry,
           @NonNull ApplicationRestarter applicationRestarter,
           @NonNull AnnotationDetector annotationDetector) {
        this.configurerProviderResolver = environmentConfigurerProviderResolver;
        this.environmentOrchestrator = environmentOrchestrator;
        this.environmentRegistry = environmentRegistry;
        this.applicationRestarter = applicationRestarter;
        this.annotationDetector = annotationDetector;
    }

    /**
     * Initializes Hussar environment for the given test class - if applicable.
     *
     * @param testObject the test object class
     */
    public void initializeFor(@NonNull Object testObject) {
        configurerProviderResolver
                .resolve(testObject)
                .ifPresent(environmentConfigurerProvider -> initializeEnvironment(testObject, environmentConfigurerProvider));
    }

    /**
     * Intercepts test method invocation - if applicable.
     *
     * @param testObject the test object
     * @param testMethod the test method
     */
    public void interceptTest(@NonNull Object testObject,
                              @NonNull Method testMethod) {
        environmentRegistry
                .getEntry(testObject)
                .ifPresent(environment -> executeBeforeTest(testMethod, environment));
    }

    /**
     * Shutdowns all Hussar environments.
     */
    public void shutdown() {
        environmentOrchestrator.shutdown();
        environmentRegistry.deleteEntries();
    }

    private void executeBeforeTest(Method testMethod, Environment environment) {
        var application = environment.application();

        annotationDetector.detect(testMethod, HussarApplicationRestart.class, annotation -> restartApplication(application));
    }

    private void initializeEnvironment(Object testObject, EnvironmentConfigurerProvider environmentConfigurerProvider) {
        var environment = environmentOrchestrator.initialize(environmentConfigurerProvider);

        cacheEnvironment(testObject, environment);
        injectServices(testObject, environment);
        injectApplication(testObject, environment);
    }

    private void restartApplication(Application application) {
        applicationRestarter.restart(application);
    }

    private void cacheEnvironment(Object testObject, Environment environment) {
        environmentRegistry.register(testObject, environment);
    }

    private void injectServices(Object testObject, Environment environment) {
        var servicesInjector = ServiceInjector.newInstance(environment);
        servicesInjector.inject(testObject);
    }

    private void injectApplication(Object testObject, Environment environment) {
        var applicationInjector = ApplicationInjector.newInstance(environment);
        applicationInjector.inject(testObject);
    }

    /**
     * Creates new Hussar instance.
     *
     * @return the Hussar instance
     */
    public static Hussar newInstance() {
        var environmentConfigurerResolver = new EnvironmentConfigurerProviderResolver();
        var environmentOrchestrator = new EnvironmentOrchestrator(
                new ServiceStarter(ForkJoinPool.commonPool()),
                new ServiceStopper(ForkJoinPool.commonPool())
        );

        var environmentRegistry = new EnvironmentRegistry();
        var applicationRestarter = new ApplicationRestarter();
        var annotationDetector = new AnnotationDetector();

        return new Hussar(environmentConfigurerResolver, environmentOrchestrator, environmentRegistry, applicationRestarter, annotationDetector);
    }

}
