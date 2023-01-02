package pl.netroute.hussar.core.lock;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class LockedAction {
    private static final Duration TRY_LOCK_TIMEOUT = Duration.ofSeconds(10L);

    private final ReentrantReadWriteLock lock;

    public LockedAction() {
        this.lock = new ReentrantReadWriteLock();
    }

    public void exclusiveAction(Runnable action) {
        Objects.requireNonNull(action, "action is required");

        lockedAction(toSupplier(action), true);
    }

    public void sharedAction(Runnable action) {
        Objects.requireNonNull(action, "action is required");

        lockedAction(toSupplier(action), false);
    }

    public <T> T sharedAction(Supplier<T> action) {
        Objects.requireNonNull(action, "action is required");

        return lockedAction(action, false);
    }

    private <T> Supplier<T> toSupplier(Runnable action) {
        return () -> {
            action.run();

            return null;
        };
    }

    private <T> T lockedAction(Supplier<T> action, boolean isWriteLock) {
        var acquiredLock = isWriteLock ? lock.writeLock() : lock.readLock();

        try {
            acquiredLock.tryLock(TRY_LOCK_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

            return action.get();
        } catch (InterruptedException ex) {
            throw new IllegalStateException("Could not acquire lock", ex);
        } finally {
            acquiredLock.unlock();
        }
    }
}
