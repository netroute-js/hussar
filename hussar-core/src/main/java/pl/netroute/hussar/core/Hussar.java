package pl.netroute.hussar.core;

import java.util.Objects;

public class Hussar {
    private final EnvironmentConfigurerProviderResolver setupProviderResolver;
    private final EnvironmentOrchestrator environmentOrchestrator;

    private Hussar(EnvironmentConfigurerProviderResolver setupProviderResolver,
                   EnvironmentOrchestrator environmentOrchestrator) {
        this.setupProviderResolver = setupProviderResolver;
        this.environmentOrchestrator = environmentOrchestrator;
    }

    public void initializeFor(Object testObject) {
        Objects.requireNonNull(testObject, "testObject is required");

        setupProviderResolver
                .resolve(testObject)
                .ifPresent(environmentOrchestrator::initialize);
    }

    public void shutdown() {
        environmentOrchestrator.shutdown();
    }

}
