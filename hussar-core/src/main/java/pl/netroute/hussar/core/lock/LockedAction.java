package pl.netroute.hussar.core.lock;

import lombok.NonNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class LockedAction {
    private static final boolean FAIR_MODE = true;

    private static final Duration TRY_LOCK_TIMEOUT = Duration.ofSeconds(10L);

    private final ReentrantReadWriteLock lock;

    public LockedAction() {
        this.lock = new ReentrantReadWriteLock(FAIR_MODE);
    }

    public void exclusiveAction(@NonNull Runnable action) {
        lockedAction(toSupplier(action), true);
    }

    public void sharedAction(@NonNull Runnable action) {
        lockedAction(toSupplier(action), false);
    }

    public <T> T sharedAction(@NonNull Supplier<T> action) {
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
            var acquired = acquiredLock.tryLock(TRY_LOCK_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if(!acquired) {
                throw new IllegalStateException("Acquiring lock timed out");
            }

            return action.get();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not acquire lock", ex);
        } finally {
            acquiredLock.unlock();
        }
    }
}
