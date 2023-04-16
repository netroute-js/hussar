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
    }

    private void injectServices(Object testObject, Environment environment) {
        var servicesInjector = ServicesInjector.newInstance(environment);
        servicesInjector.inject(testObject);
    }

    public static Hussar newInstance() {
        var configurerResolver = new EnvironmentConfigurerProviderResolver();
        var orchestrator = new EnvironmentOrchestrator(
                new PropertiesConfigurer(),
                new PropertiesCleaner(),
                new ServicesStarter(ForkJoinPool.commonPool()),
                new ServicesStopper(ForkJoinPool.commonPool())
        );

        return new Hussar(configurerResolver, orchestrator);
    }

}
