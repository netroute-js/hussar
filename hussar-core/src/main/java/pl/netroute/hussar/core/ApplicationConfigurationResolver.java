package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ApplicationConfigurationResolver {

    @NonNull
    private final ApplicationConfigurationLoader applicationConfigurationLoader;

    @NonNull
    private final ApplicationConfigurationFlattener applicationConfigurationFlattener;

    Map<String, Object> resolve(@NonNull Application application,
                                @NonNull List<ConfigurationRegistry> externalConfiguration) {
        var mutableConfiguration = new HashMap<>(applicationConfigurationLoader.load(application));

        externalConfiguration
                .stream()
                .map(ConfigurationRegistry::getEntries)
                .flatMap(Collection::stream)
                .forEach(configuration -> replaceConfigurationEntry(mutableConfiguration, configuration));

        return Map.copyOf(mutableConfiguration);
    }

    private void replaceConfigurationEntry(Map<String, Object> applicationConfiguration, ConfigurationEntry configuration) {
        if(configuration instanceof EnvVariableConfigurationEntry) {
            replaceEnvironmentVariable(applicationConfiguration, (EnvVariableConfigurationEntry) configuration);
        } else if(configuration instanceof PropertyConfigurationEntry) {
            replaceProperty(applicationConfiguration, (PropertyConfigurationEntry) configuration);
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
                .filter(configuration -> envVariableName.equals(configuration.getValue()))
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
        var configurationMap = Map.of(configurationKey, configurationValue);

        return applicationConfigurationFlattener.flatten(configurationMap);
    }

    private void combineConfigurations(Map<String, Object> applicationConfiguration, Map<String, Object> configurationsToAppend) {
        configurationsToAppend
                .forEach((configurationKey, configurationValue) -> applicationConfiguration.merge(configurationKey, configurationValue, (oldConfigurationValue, newConfigurationValue) -> newConfigurationValue));
    }

}
