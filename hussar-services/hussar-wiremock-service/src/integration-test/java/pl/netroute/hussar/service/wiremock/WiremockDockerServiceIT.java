package pl.netroute.hussar.service.wiremock;

import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.BaseServiceIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.wiremock.api.WiremockDockerService;
import pl.netroute.hussar.service.wiremock.api.WiremockDockerServiceConfigurer;
import pl.netroute.hussar.service.wiremock.assertion.WiremockAssertionHelper;

import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class WiremockDockerServiceIT extends BaseServiceIT<WiremockDockerService> {

    @Override
    protected ServiceTestMetadata<WiremockDockerService, Consumer<WiremockDockerService>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var service = WiremockDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<WiremockDockerService>) actualService -> {
            var wiremockAssertion = new WiremockAssertionHelper(actualService);
            wiremockAssertion.assertSingleEndpoint();
            wiremockAssertion.assertWiremockAccessible();
            wiremockAssertion.assertNoConfigurationsRegistered();
        };

        return ServiceTestMetadata
                .<WiremockDockerService, Consumer<WiremockDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<WiremockDockerService, Consumer<WiremockDockerService>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var name = "wiremock-instance";
        var dockerVersion = "2.34.0";
        var startupTimeout = Duration.ofSeconds(90L);
        var endpointProperty = "propertyA.wiremock.url";
        var endpointEnvVariable = "WIREMOCK_URL";

        var service = WiremockDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .startupTimeout(startupTimeout)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .done()
                .configure(context);

        var assertion = (Consumer<WiremockDockerService>) actualService -> {
            var wiremockAssertion = new WiremockAssertionHelper(actualService);
            wiremockAssertion.assertSingleEndpoint();
            wiremockAssertion.assertWiremockAccessible();
            wiremockAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
            wiremockAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
        };

        return ServiceTestMetadata
                .<WiremockDockerService, Consumer<WiremockDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<WiremockDockerService, BiConsumer<WiremockDockerService, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext configureContext) {
        var service = WiremockDockerServiceTestFactory.createMinimallyConfigured(configureContext);

        var assertion = (BiConsumer<WiremockDockerService, List<Endpoint>>) (actualService, endpoints) -> {
            var endpoint = endpoints.getFirst();

            var wiremockAssertion = new WiremockAssertionHelper(actualService);
            wiremockAssertion.assertWiremockNotAccessible(endpoint);
        };

        return ServiceTestMetadata
                .<WiremockDockerService, BiConsumer<WiremockDockerService, List<Endpoint>>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
