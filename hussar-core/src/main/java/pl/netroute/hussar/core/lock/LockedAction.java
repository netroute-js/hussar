package pl.netroute.hussar.core.lock;

import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * A class responsible for controlling the execution of an action.
 */
@InternalUseOnly
public class LockedAction {
    private static final boolean FAIR_MODE = true;

    private static final Duration TRY_LOCK_TIMEOUT = Duration.ofSeconds(10L);

    private final ReentrantReadWriteLock lock;

    /**
     * Creates new {@link LockedAction}.
     */
    public LockedAction() {
        this.lock = new ReentrantReadWriteLock(FAIR_MODE);
    }

    /**
     * Guarantees that the action will be executed exclusively. Only one thread can be executing this action.
     *
     * @param action - the {@link Runnable} that has to be executed exclusively.
     */
    public void exclusiveAction(@NonNull Runnable action) {
        lockedAction(toSupplier(action), true);
    }

    /**
     * Executes action without any guarantees. Multiple threads can be running in parallel.
     *
     * @param action - the {@link Runnable} that can be executed by multiple threads.
     */
    public void sharedAction(@NonNull Runnable action) {
        lockedAction(toSupplier(action), false);
    }

    /**
     * Executes action without any guarantees. Multiple threads can be running in parallel.
     *
     * @param <T>    the type parameter
     * @param action - the {@link Supplier} that can be executed by multiple threads.
     * @return the actual result of the processing
     */
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
