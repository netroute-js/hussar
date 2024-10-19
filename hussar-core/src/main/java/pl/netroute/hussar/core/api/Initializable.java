package pl.netroute.hussar.core.api;

/**
 * Hussar interface responsible for exposing methods to determine whether {@link Application} or {@link Service} is initialized.
 */
public interface Initializable {

    /**
     * Returns whether {@link Application} or {@link Service} is initialized.
     *
     * @return true if {@link Application} or {@link Service} is initialized. Returns false otherwise.
     */
    boolean isInitialized();

}
