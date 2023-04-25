package pl.netroute.hussar.core.api;

public interface Service extends Accessible, Lifecycle<ServiceStartupContext> {
    String getName();
}
