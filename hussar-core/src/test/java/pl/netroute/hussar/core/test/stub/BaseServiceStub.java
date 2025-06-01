package pl.netroute.hussar.core.test.stub;

import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.datafaker.Faker;
import org.mockito.Mockito;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.network.api.NetworkControl;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.test.factory.ConfigurationRegistryTestFactory;
import pl.netroute.hussar.core.test.factory.EndpointTestFactory;

import java.util.List;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
abstract class BaseServiceStub implements Service {
    private static final Faker FAKER = new Faker();

    @Builder.Default
    private final String name = FAKER.internet().domainName();

    @Builder.Default
    private final ConfigurationRegistry configurationRegistry = ConfigurationRegistryTestFactory.create();

    @Builder.Default
    private final NetworkControl networkControl = Mockito.mock(NetworkControl.class);

    @Builder.Default
    private final List<Endpoint> endpoints = List.of(EndpointTestFactory.createHttp());

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    @Override
    public void start(@NonNull ServiceStartupContext context) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public ConfigurationRegistry getConfigurationRegistry() {
        return configurationRegistry;
    }

    @Override
    public NetworkControl getNetworkControl() {
        return networkControl;
    }

}
