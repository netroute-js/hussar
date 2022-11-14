package pl.netroute.hussar.core;

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
                .ifPresent(environmentOrchestrator::initialize);
    }

    public void shutdown() {
        environmentOrchestrator.shutdown();
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
