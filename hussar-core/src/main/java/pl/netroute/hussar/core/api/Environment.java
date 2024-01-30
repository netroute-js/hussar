package pl.netroute.hussar.core.api;

public interface Environment {
    Application application();
    ConfigurationRegistry configurationRegistry();
    ServiceRegistry serviceRegistry();
}
