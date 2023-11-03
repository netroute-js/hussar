package com.netroute.hussar.service.wiremock;

import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.core.service.api.RegistrableConfigurationEntry;

import java.util.Set;

public class WiremockDockerService extends BaseDockerService<WiremockDockerServiceConfig> {
    private static final int HTTP_PORT = 8080;

    WiremockDockerService(WiremockDockerServiceConfig config) {
        super(config);
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container.withExposedPorts(HTTP_PORT);
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        registerUrlEndpoint(config.getRegisterEndpointUnderEntries());
    }

    private void registerUrlEndpoint(Set<RegistrableConfigurationEntry> endpointEntries) {
        if(!endpointEntries.isEmpty()) {
            var endpointAddress = getEndpoints().get(0).getAddress();

            endpointEntries
                    .stream()
                    .map(registrableEntry -> registrableEntry.toResolvedConfigurationEntry(endpointAddress))
                    .forEach(configurationRegistry::register);
        }
    }

}
