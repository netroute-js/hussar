package pl.netroute.hussar.core.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.network.DefaultNetworkControlScenarioVerifier.RecordableNetworkControl;

import java.time.Duration;
import java.util.List;

import static pl.netroute.hussar.core.network.DefaultNetworkControlScenarioVerifier.RecordableNetworkControl.*;
import static pl.netroute.hussar.core.test.helper.AwaitingHelper.eventually;
import static pl.netroute.hussar.core.test.helper.AwaitingHelper.waitFor;

public class DefaultNetworkScenarioTest {
    private static final Duration ONE_SECOND = Duration.ofSeconds(1L);
    private static final Duration TWO_SECONDS = Duration.ofSeconds(2L);

    private DefaultNetworkScenario networkScenario;
    private DefaultNetworkControlScenarioVerifier verifier;

    @BeforeEach
    public void setup() {
        var networkControl = new RecordableNetworkControl();

        networkScenario = new DefaultNetworkScenario(networkControl);
        verifier = new DefaultNetworkControlScenarioVerifier(networkControl);
    }

    @Test
    public void shouldStartMultiStepScenario() {
        // given
        // when
        networkScenario
                .delay(ONE_SECOND)
                .wait(TWO_SECONDS)
                .disable()
                .wait(ONE_SECOND)
                .enable()
                .wait(ONE_SECOND)
                .reset()
                .start();

        // then
        var recordedMethods = List.of(
                RecordMethod.DELAY,
                RecordMethod.DISABLE,
                RecordMethod.ENABLE,
                RecordMethod.RESET
        );

        eventually(() -> verifier.verifyMultiStepScenarioStarted(recordedMethods));
    }

    @Test
    public void shouldDoNothingWhenScenarioNotStarted() {
        // given
        // when
        networkScenario
                .disable()
                .wait(Duration.ofSeconds(1L))
                .enable();

        // then
        waitFor(Duration.ofSeconds(2L));

        verifier.verifyScenarioNotStarted();
    }

}