package pl.netroute.hussar.core.environment;

import pl.netroute.hussar.core.api.InternalUseOnly;

@InternalUseOnly
public record EnvironmentStartupContext() {

    /**
     * Returns default context.
     *
     * @return the actual default context.
     */
    public static EnvironmentStartupContext defaultContext() {
        return new EnvironmentStartupContext();
    }

}
