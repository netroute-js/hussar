package pl.netroute.hussar.core.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LockedActionTest {
    private LockedAction lockedAction;

    @BeforeEach
    public void setup() {
        lockedAction = new LockedAction();
    }

    @RepeatedTest(value = 10)
    public void shouldExecuteExclusiveAction() {
        // given
        List<String> values = new ArrayList<>();

        // when
        var exclusiveActionResults = IntStream
                .range(0, 10)
                .mapToObj(index -> CompletableFuture.runAsync(() -> lockedAction.exclusiveAction(new Producer(values))))
                .collect(Collectors.toUnmodifiableList());

        CompletableFuture
                .allOf(exclusiveActionResults.toArray(new CompletableFuture[0]))
                .join();

        // then
        assertSingleValueProduced(values);
    }

    @Test
    public void shouldDoSharedAction() {
        var maybeMoreValuesProduced = IntStream
                .range(0, 50)
                .mapToObj(index -> {
                    // given
                    List<String> values = new ArrayList<>();

                    // when
                    var exclusiveActionResults = IntStream
                            .range(0, 2)
                            .mapToObj(actionIndex -> CompletableFuture.runAsync(() -> lockedAction.sharedAction(new Producer(values))))
                            .collect(Collectors.toUnmodifiableList());

                    CompletableFuture
                            .allOf(exclusiveActionResults.toArray(new CompletableFuture[0]))
                            .join();

                    assertAtLeastTwoValuesProduced(values);
                    // then
                    try {

                        return true;
                    } catch(AssertionError ex) {
                        return false;
                    }
                })
                .filter(Boolean::booleanValue)
                .findFirst();

        assertThat(maybeMoreValuesProduced).isNotEmpty();
    }

    @Test
    public void shouldDoSharedActionAndReturn() {
        var maybeMoreValuesProduced = IntStream
                .range(0, 50)
                .mapToObj(index -> {
                    // given
                    List<String> values = new ArrayList<>();

                    // when
                    var exclusiveActionResults = IntStream
                            .range(0, 2)
                            .mapToObj(actionIndex -> CompletableFuture.runAsync(() -> lockedAction.sharedAction(toSupplier(new Producer(values)))))
                            .collect(Collectors.toUnmodifiableList());

                    CompletableFuture
                            .allOf(exclusiveActionResults.toArray(new CompletableFuture[0]))
                            .join();

                    // then
                    try {
                        assertAtLeastTwoValuesProduced(values);

                        return true;
                    } catch(AssertionError ex) {
                        return false;
                    }
                })
                .filter(Boolean::booleanValue)
                .findFirst();

        assertThat(maybeMoreValuesProduced).isNotEmpty();

    }

    private void assertSingleValueProduced(List<String> values) {
        assertThat(values).hasSize(1);

    }

    private void assertAtLeastTwoValuesProduced(List<String> values) {
        assertThat(values).hasSizeGreaterThanOrEqualTo(2);
    }

    private <T> Supplier<T> toSupplier(Runnable action) {
        return () -> {
            action.run();

            return null;
        };
    }

    private static class Producer implements Runnable {
        private final List<String> values;

        public Producer(List<String> values) {
            this.values = values;
        }

        @Override
        public void run() {
            if(values.isEmpty()) {
                values.add(UUID.randomUUID().toString());
            }
        }

    }

}
