package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.environment.EnvironmentStartupContext;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.environment.EnvironmentConfigurerContext;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.lock.LockedAction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EnvironmentOrchestrator {
    private final LockedAction lockedAction = new LockedAction();
    private final Map<Class<? extends EnvironmentConfigurerProvider>, Environment> initializedEnvironments = new ConcurrentHashMap<>();

    Environment initialize(@NonNull EnvironmentConfigurerProvider environmentConfigurerProvider) {
        var configurerType = environmentConfigurerProvider.getClass();

        return lockedAction.sharedAction(() -> initializedEnvironments.computeIfAbsent(configurerType, actualConfigurerType -> initializeEnvironment(environmentConfigurerProvider)));
    }

    void shutdown() {
        log.info("Shutting down all environments");

        lockedAction.exclusiveAction(() -> {
            initializedEnvironments
                    .values()
                    .forEach(Environment::shutdown);

            initializedEnvironments.clear();
        });
    }

    private Environment initializeEnvironment(EnvironmentConfigurerProvider provider) {
        log.info("Initializing environment for {}", provider.getClass());

        var environment = provider
                .provide()
                .configure(EnvironmentConfigurerContext.defaultContext());

        environment.start(EnvironmentStartupContext.defaultContext());

        return environment;
    }

}
