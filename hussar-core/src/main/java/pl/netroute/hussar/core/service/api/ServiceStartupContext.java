package pl.netroute.hussar.core.service.api;

/**
 * Hussar context used during {@link Service} startup.
 */
public record ServiceStartupContext() {

    /**
     * Returns an empty/default context.
     *
     * @return the actual empty/default context.
     */
    public static ServiceStartupContext empty() {
        return new ServiceStartupContext();
    }

}
