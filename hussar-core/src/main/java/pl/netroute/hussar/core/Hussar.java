package pl.netroute.hussar.core;

import lombok.NonNull;
import pl.netroute.hussar.core.api.Environment;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;

import java.util.concurrent.ForkJoinPool;

public class Hussar {
    private final EnvironmentConfigurerProviderResolver configurerProviderResolver;
    private final EnvironmentOrchestrator environmentOrchestrator;

    Hussar(@NonNull EnvironmentConfigurerProviderResolver configurerProviderResolver,
           @NonNull EnvironmentOrchestrator environmentOrchestrator) {
        this.configurerProviderResolver = configurerProviderResolver;
        this.environmentOrchestrator = environmentOrchestrator;
    }

    public void initializeFor(@NonNull Object testObject) {
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

        var orchestrator = new EnvironmentOrchestrator(
                new ServiceStarter(ForkJoinPool.commonPool()),
                new ServiceStopper(ForkJoinPool.commonPool())
        );

        return new Hussar(configurerResolver, orchestrator);
    }

}
