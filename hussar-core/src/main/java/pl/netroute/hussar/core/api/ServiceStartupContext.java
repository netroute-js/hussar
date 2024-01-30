package pl.netroute.hussar.core.api;

public record ServiceStartupContext() {

    public static ServiceStartupContext empty() {
        return new ServiceStartupContext();
    }

}
