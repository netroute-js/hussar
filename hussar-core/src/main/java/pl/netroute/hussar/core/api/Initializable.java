package pl.netroute.hussar.core.api;

/**
 * Hussar interface responsible for exposing methods to determine component is initialized.
 */
public interface Initializable {

    /**
     * Returns whether component is initialized.
     *
     * @return true if component is initialized. Returns false otherwise.
     */
    boolean isInitialized();

}
