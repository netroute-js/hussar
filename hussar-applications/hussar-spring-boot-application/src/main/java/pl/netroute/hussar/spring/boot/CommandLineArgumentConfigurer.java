package pl.netroute.hussar.spring.boot;

import lombok.NonNull;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;

import java.util.List;
import java.util.Set;

class CommandLineArgumentConfigurer {
    private static final String ARGUMENT_TEMPLATE = "--%s=%s";

    List<String> configure(@NonNull Set<ConfigurationEntry> externalConfigurations) {
        return externalConfigurations
                .stream()
                .map(this::mapToArgument)
                .toList();
    }

    private String mapToArgument(@NonNull ConfigurationEntry entry) {
        var name = entry.name();
        var value = entry.value();

        return ARGUMENT_TEMPLATE.formatted(name, value);
    }

}
