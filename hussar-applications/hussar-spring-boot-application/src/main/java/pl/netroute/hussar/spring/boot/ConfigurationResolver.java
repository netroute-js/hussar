package pl.netroute.hussar.spring.boot;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.PropertyConfigurationEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ConfigurationResolver {

    Map<String, Object> resolve(@NonNull Map<String, Object> applicationConfigurations,
                                @NonNull Set<ConfigurationEntry> externalConfigurations) {
        var mutableConfiguration = new HashMap<>(applicationConfigurations);

        externalConfigurations
                .forEach(configuration -> replaceConfigurationEntry(mutableConfiguration, configuration));

        return Map.copyOf(mutableConfiguration);
    }

    private void replaceConfigurationEntry(Map<String, Object> applicationConfiguration, ConfigurationEntry configuration) {
        if(configuration instanceof EnvVariableConfigurationEntry environmentVariable) {
            replaceEnvironmentVariable(applicationConfiguration, environmentVariable);
        } else if(configuration instanceof PropertyConfigurationEntry property) {
            replaceProperty(applicationConfiguration, property);
        } else {
            throw new IllegalStateException("Unsupported ConfigurationEntry - " + configuration.getClass());
        }
    }

    private void replaceEnvironmentVariable(Map<String, Object> applicationConfiguration, EnvVariableConfigurationEntry envVariable) {
        var envVariableName = envVariable.formattedName();
        var envVariableValue = envVariable.value();

        var configurationsToReplace = applicationConfiguration
                .entrySet()
                .stream()
                .filter(configuration -> configuration.getValue() != null)
                .filter(configuration -> envVariableName.equals(configuration.getValue().toString()))
                .map(Map.Entry::getKey)
                .toList();

        removeConfigurations(applicationConfiguration, configurationsToReplace);

        configurationsToReplace
                .stream()
                .map(configuration -> resolveConfigurations(configuration, envVariableValue))
                .forEach(resolvedConfiguration -> combineConfigurations(applicationConfiguration, resolvedConfiguration));
    }

    private void replaceProperty(Map<String, Object> applicationConfiguration, PropertyConfigurationEntry property) {
        var propertyName = property.formattedName();
        var propertyValue = property.value();
        var resolvedConfigurations = resolveConfigurations(propertyName, propertyValue);

        var configurationsToReplace = applicationConfiguration
                .entrySet()
                .stream()
                .filter(configuration -> configuration.getKey().startsWith(propertyName))
                .map(Map.Entry::getKey)
                .toList();

        removeConfigurations(applicationConfiguration, configurationsToReplace);
        combineConfigurations(applicationConfiguration, resolvedConfigurations);
    }

    private void removeConfigurations(Map<String, Object> applicationConfiguration, List<String> configurationsToRemove) {
        configurationsToRemove.forEach(applicationConfiguration::remove);
    }

    private Map<String, Object> resolveConfigurations(String configurationKey, Object configurationValue) {
        return Map.of(configurationKey, configurationValue);
    }

    private void combineConfigurations(Map<String, Object> applicationConfiguration, Map<String, Object> configurationsToAppend) {
        configurationsToAppend
                .forEach((configurationKey, configurationValue) -> applicationConfiguration.merge(configurationKey, configurationValue, (oldConfigurationValue, newConfigurationValue) -> newConfigurationValue));
    }

}
