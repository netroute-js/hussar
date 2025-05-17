package pl.netroute.hussar.core.environment;

import pl.netroute.hussar.core.api.InternalUseOnly;

@InternalUseOnly
public record EnvironmentConfigurerContext() {

    /**
     * Returns default context.
     *
     * @return the actual default context.
     */
    public static EnvironmentConfigurerContext defaultContext() {
        return new EnvironmentConfigurerContext();
    }

}
