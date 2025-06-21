package pl.netroute.hussar.core.network;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.network.DefaultNetworkControlScenarioVerifier.RecordableNetworkControl.RecordMethod;
import pl.netroute.hussar.core.network.api.NetworkControl;
import pl.netroute.hussar.core.network.api.NetworkScenario;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DefaultNetworkControlScenarioVerifier {
    private final RecordableNetworkControl networkControl;

    void verifyScenarioNotStarted() {
        var recordedMethods = networkControl.getRecordedMethods();

        assertThat(recordedMethods).isEmpty();
    }

    void verifyMultiStepScenarioStarted(@NonNull List<RecordMethod> recordedMethods) {
        var actualRecordedMethods = networkControl.getRecordedMethods();

        assertThat(actualRecordedMethods).containsExactlyElementsOf(recordedMethods);
    }

    static class RecordableNetworkControl implements NetworkControl {

        @NonNull
        private final List<RecordMethod> recordedMethods;

        RecordableNetworkControl() {
            this.recordedMethods = new ArrayList<>();
        }

        @Override
        public void enable() {
            recordedMethods.add(RecordMethod.ENABLE);
        }

        @Override
        public void disable() {
            recordedMethods.add(RecordMethod.DISABLE);
        }

        @Override
        public void delay(@NonNull Duration delay) {
            recordedMethods.add(RecordMethod.DELAY);
        }

        @Override
        public void reset() {
            recordedMethods.add(RecordMethod.RESET);
        }

        @Override
        public NetworkScenario scenario() {
            throw new UnsupportedOperationException("Not supported operation");
        }

        public List<RecordMethod> getRecordedMethods() {
            return List.copyOf(recordedMethods);
        }

        enum RecordMethod {
            ENABLE,
            DISABLE,
            DELAY,
            RESET
        }

    }

}