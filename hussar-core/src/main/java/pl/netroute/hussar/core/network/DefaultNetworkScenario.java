package pl.netroute.hussar.core.network;

import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.network.api.NetworkControl;
import pl.netroute.hussar.core.network.api.NetworkScenario;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@InternalUseOnly
class DefaultNetworkScenario implements NetworkScenario {

    @NonNull
    private final NetworkControl networkControl;

    @NonNull
    private final List<DeferredNetworkAction> networkActions;

    private Duration interActionDelay = Duration.ZERO;

    DefaultNetworkScenario(@NonNull NetworkControl networkControl) {
        this.networkControl = networkControl;
        this.networkActions = new ArrayList<>();
    }

    @Override
    public NetworkScenario enable() {
        var enableAction = new DeferredNetworkAction(networkControl::enable, interActionDelay);
        networkActions.add(enableAction);

        return this;
    }

    @Override
    public NetworkScenario disable() {
        var disableAction = new DeferredNetworkAction(networkControl::disable, interActionDelay);
        networkActions.add(disableAction);

        return this;
    }

    @Override
    public NetworkScenario delay(@NonNull Duration delay) {
        var delayAction = new DeferredNetworkAction(() -> networkControl.delay(delay), interActionDelay);
        networkActions.add(delayAction);

        return this;
    }

    @Override
    public NetworkScenario reset() {
        var resetAction = new DeferredNetworkAction(networkControl::reset, interActionDelay);
        networkActions.add(resetAction);

        return this;
    }

    @Override
    public NetworkScenario wait(@NonNull Duration waitFor) {
        this.interActionDelay = interActionDelay.plus(waitFor);

        return this;
    }

    @Override
    public void start() {
        CompletableFuture.runAsync(() -> runScenario(networkActions));
    }

    private void runScenario(List<DeferredNetworkAction> networkActions) {
        try(var scheduler = Executors.newSingleThreadScheduledExecutor()) {
            networkActions.forEach(networkAction -> runNetworkAction(scheduler, networkAction));
        }
    }

    private void runNetworkAction(ScheduledExecutorService scheduler, DeferredNetworkAction networkAction) {
        var action = networkAction.action();
        var delayMillis = networkAction.delay().toMillis();

        scheduler.schedule(action, delayMillis, TimeUnit.MILLISECONDS);
    }

    private record DeferredNetworkAction(@NonNull Runnable action, @NonNull Duration delay) {
    }
}
