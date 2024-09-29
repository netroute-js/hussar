package pl.netroute.hussar.core.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LockedActionTest {
    private LockedAction lockedAction;

    @BeforeEach
    public void setup() {
        lockedAction = new LockedAction();
    }

    @RepeatedTest(value = 1000)
    public void shouldExecuteExclusiveAction() {
        // given
        var incrementor = new Incrementor();
        var threads = 10;

        // when
        var exclusiveActionResults = IntStream
                .range(0, threads)
                .mapToObj(index -> CompletableFuture.runAsync(() -> lockedAction.exclusiveAction(incrementor)))
                .toList();

        CompletableFuture
                .allOf(exclusiveActionResults.toArray(new CompletableFuture[0]))
                .join();

        // then
        assertIncremented(incrementor, threads * Incrementor.RUN_THRESHOLD);
    }

    @RepeatedTest(value = 1000)
    public void shouldDoSharedAction() {
        // given
        var incrementor = new Incrementor();
        var threads = 10;

        // when
        var exclusiveActionResults = IntStream
                .range(0, threads)
                .mapToObj(actionIndex -> CompletableFuture.runAsync(() -> lockedAction.sharedAction(incrementor)))
                .toList();

        CompletableFuture
                .allOf(exclusiveActionResults.toArray(new CompletableFuture[0]))
                .join();

        // then
        assertIncrementedDifferentThan(incrementor, threads * Incrementor.RUN_THRESHOLD);
    }

    @Test
    public void shouldDoSharedActionAndReturn() {
        // given
        var incrementor = new Incrementor();
        var threads = 10;

        // when
        var exclusiveActionResults = IntStream
                .range(0, threads)
                .mapToObj(actionIndex -> CompletableFuture.runAsync(() -> lockedAction.sharedAction(toSupplier(incrementor))))
                .toList();

        CompletableFuture
                .allOf(exclusiveActionResults.toArray(new CompletableFuture[0]))
                .join();

        // then
        assertIncrementedDifferentThan(incrementor, threads * Incrementor.RUN_THRESHOLD);
    }

    private void assertIncremented(Incrementor incrementor, int expectedCounter) {
        assertThat(incrementor.getCounter()).isEqualTo(expectedCounter);
    }

    private void assertIncrementedDifferentThan(Incrementor incrementor, int notExpectedCounter) {
        assertThat(incrementor.getCounter()).isNotEqualTo(notExpectedCounter);
    }

    private <T> Supplier<T> toSupplier(Runnable action) {
        return () -> {
            action.run();

            return null;
        };
    }

    private static class Incrementor implements Runnable {
        private static final int INITIAL_COUNTER_VALUE = 0;
        private static final int RUN_THRESHOLD = 5000000;

        private int counter;

        public Incrementor() {
            this.counter = INITIAL_COUNTER_VALUE;
        }

        @Override
        public void run() {
            IntStream
                .range(0, RUN_THRESHOLD)
                .forEach(index -> counter++);
        }

        public int getCounter() {
            return counter;
        }

    }

}
