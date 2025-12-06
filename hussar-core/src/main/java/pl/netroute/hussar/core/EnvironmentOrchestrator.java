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
import pl.netroute.hussar.core.helper.TimerHelper;
import pl.netroute.hussar.core.lock.LockedAction;
import pl.netroute.hussar.core.logging.EnvironmentLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EnvironmentOrchestrator {
    private final LockedAction lockedAction = new LockedAction();

    private final Map<Class<? extends EnvironmentConfigurerProvider>, EnvironmentInitializationResult> initializedEnvironments = new ConcurrentHashMap<>();

    Environment initialize(@NonNull EnvironmentConfigurerProvider environmentConfigurerProvider) {
        var configurerType = environmentConfigurerProvider.getClass();
        var initializationResult = lockedAction.sharedAction(() -> initializedEnvironments.computeIfAbsent(configurerType, actualConfigurerType -> initializeEnvironment(environmentConfigurerProvider)));

        return switch (initializationResult) {
            case EnvironmentInitializationResult.EnvironmentInitializedResult result -> result.environment();
            case EnvironmentInitializationResult.EnvironmentInitializationFailedResult result -> throw new HussarException("Environment initialization failed", result.exception());
        };
    }

    void shutdown() {
        log.info("Shutting down all Hussar environments");

        lockedAction.exclusiveAction(() -> {
            initializedEnvironments
                    .values()
                    .stream()
                    .map(EnvironmentInitializationResult::environment)
                    .forEach(this::shutdownEnvironment);

            initializedEnvironments.clear();
        });
    }

    private EnvironmentInitializationResult initializeEnvironment(EnvironmentConfigurerProvider provider) {
        var environment = provider
                .provide()
                .configure(EnvironmentConfigurerContext.defaultContext());

        try {
            var startupDuration = TimerHelper.measure(() -> environment.start(EnvironmentStartupContext.defaultContext()));
            EnvironmentLogger.logEnvironmentStartup(provider, environment, startupDuration);

            return EnvironmentInitializationResult.initialized(environment);
        } catch(Exception ex) {
            return EnvironmentInitializationResult.failed(environment, ex);
        }
    }

    private void shutdownEnvironment(Environment environment) {
        try {
            environment.shutdown();
        } catch (Exception ex) {
            log.warn("Error during shutting down environment.", ex);
        }
    }

    private sealed interface EnvironmentInitializationResult permits
            EnvironmentInitializationResult.EnvironmentInitializedResult,
            EnvironmentInitializationResult.EnvironmentInitializationFailedResult {

        Environment environment();

        record EnvironmentInitializedResult(@NonNull Environment environment) implements EnvironmentInitializationResult{
        }

        record EnvironmentInitializationFailedResult(@NonNull Environment environment, @NonNull Exception exception) implements EnvironmentInitializationResult {
        }

        private static EnvironmentInitializedResult initialized(@NonNull Environment environment) {
            return new EnvironmentInitializedResult(environment);
        }

        private static EnvironmentInitializationFailedResult failed(@NonNull Environment environment, @NonNull Exception exception) {
            return new EnvironmentInitializationFailedResult(environment, exception);
        }

    }

}
