package pl.netroute.hussar.core.environment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.service.api.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvironmentConfigurationExtractor {

    public static ConfigurationRegistry extract(@NonNull Environment environment) {
        var staticConfigurations = extractStaticConfigurations(environment);
        var servicesConfigurations = extractServicesConfigurations(environment);
        var mergedConfigurations = CollectionHelper.mergeSets(staticConfigurations, servicesConfigurations);

        return new DefaultConfigurationRegistry(mergedConfigurations);
    }

    private static Set<ConfigurationEntry> extractStaticConfigurations(Environment environment) {
        return environment
                .getConfigurationRegistry()
                .getEntries();
    }

    private static Set<ConfigurationEntry> extractServicesConfigurations(Environment environment) {
        return environment
                .getServiceRegistry()
                .getEntries()
                .stream()
                .map(Service::getConfigurationRegistry)
                .map(ConfigurationRegistry::getEntries)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

}
