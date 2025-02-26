package pl.netroute.hussar.spring.boot.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.helper.PortFinderHelper;

import java.util.HashSet;
import java.util.Set;

import static pl.netroute.hussar.core.configuration.api.ConfigurationEntry.property;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DynamicConfigurationConfigurer {
    public static final String SERVER_PORT = "server.port";

    static Set<ConfigurationEntry> configure(@NonNull Set<ConfigurationEntry> externalConfigurations) {
        var mutableExternalConfigurations = new HashSet<>(externalConfigurations);
        mutableExternalConfigurations.add(property(SERVER_PORT, PortFinderHelper.findFreePort() + ""));

        return Set.copyOf(mutableExternalConfigurations);
    }

}
