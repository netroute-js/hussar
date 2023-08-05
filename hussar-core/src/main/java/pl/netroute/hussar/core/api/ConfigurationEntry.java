package pl.netroute.hussar.core.api;

public interface ConfigurationEntry {
    String getName();
    String getFormattedName();
    String getValue();

    static EnvVariableConfigurationEntry envVariable(String name, String value) {
        return new EnvVariableConfigurationEntry(name, value);
    }

    static PropertyConfigurationEntry property(String name, String value) {
        return new PropertyConfigurationEntry(name, value);
    }

}
