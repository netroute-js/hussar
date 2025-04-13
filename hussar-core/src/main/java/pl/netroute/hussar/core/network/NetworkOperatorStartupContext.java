package pl.netroute.hussar.core.network;

import pl.netroute.hussar.core.api.InternalUseOnly;

@InternalUseOnly
public record NetworkOperatorStartupContext() {

    public static NetworkOperatorStartupContext defaultContext() {
        return new NetworkOperatorStartupContext();
    }

}
