package pl.netroute.hussar.core.network.api;

import lombok.NonNull;

import java.time.Duration;

public interface NetworkScenario {
    NetworkScenario enable();
    NetworkScenario disable();
    NetworkScenario delay(@NonNull Duration delay);
    NetworkScenario reset();
    NetworkScenario wait(@NonNull Duration waitFor);
    void start();
}
