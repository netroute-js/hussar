package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

public class Hussar {
    private final EnvironmentConfigurerProviderResolver configurerProviderResolver;
    private final EnvironmentOrchestrator environmentOrchestrator;

    Hussar(EnvironmentConfigurerProviderResolver configurerProviderResolver,
           EnvironmentOrchestrator environmentOrchestrator) {
        Objects.requireNonNull(configurerProviderResolver, "configurerProviderResolver is required");
        Objects.requireNonNull(environmentOrchestrator, "environmentOrchestrator is required");

        this.configurerProviderResolver = configurerProviderResolver;
        this.environmentOrchestrator = environmentOrchestrator;
    }

    public void initializeFor(Object testObject) {
        Objects.requireNonNull(testObject, "testObject is required");

        configurerProviderResolver
                .resolve(testObject)
                .ifPresent(environmentConfigurerProvider -> initializeEnvironment(testObject, environmentConfigurerProvider));
    }

    public void shutdown() {
        environmentOrchestrator.shutdown();
    }

    private void initializeEnvironment(Object testObject, EnvironmentConfigurerProvider environmentConfigurerProvider) {
        Environment environment = environmentOrchestrator.initialize(environmentConfigurerProvider);

        injectServices(testObject, environment);
        injectApplication(testObject, environment);
    }

    private void injectServices(Object testObject, Environment environment) {
        var servicesInjector = ServiceInjector.newInstance(environment);
        servicesInjector.inject(testObject);
    }

    private void injectApplication(Object testObject, Environment environment) {
        var applicationInjector = ApplicationInjector.newInstance(environment);
        applicationInjector.inject(testObject);
    }

    public static Hussar newInstance() {
        var configurerResolver = new EnvironmentConfigurerProviderResolver();

        var applicationConfigurationFlattener = new ApplicationConfigurationFlattener();
        var applicationConfigurationLoader = new ApplicationConfigurationLoader(applicationConfigurationFlattener);

        var orchestrator = new EnvironmentOrchestrator(
                new ServiceStarter(ForkJoinPool.commonPool()),
                new ServiceStopper(ForkJoinPool.commonPool()),
                new ApplicationConfigurationResolver(applicationConfigurationLoader, applicationConfigurationFlattener)
        );

        return new Hussar(configurerResolver, orchestrator);
    }

}
