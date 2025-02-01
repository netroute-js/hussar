package pl.netroute.hussar.core.service.api;

/**
 * Hussar context used during {@link Service} startup.
 */
public record ServiceStartupContext() {

    /**
     * Returns default context.
     *
     * @return the actual default context.
     */
    public static ServiceStartupContext defaultContext() {
        return new ServiceStartupContext();
    }

}
