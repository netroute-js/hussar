package pl.netroute.hussar.service.wiremock;

import pl.netroute.hussar.core.service.api.ServiceConfigureContext;
import pl.netroute.hussar.service.wiremock.assertion.WiremockAssertionHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;

import java.util.Optional;

public class WiremockDockerServiceIT {
    private WiremockDockerService wiremockService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(wiremockService)
                .ifPresent(WiremockDockerService::shutdown);
    }

    @Test
    public void shouldStartWiremockService() {
        // given
        wiremockService = WiremockDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(new ServiceConfigureContext());

        // when
        wiremockService.start(ServiceStartupContext.empty());

        // then
        var wiremockAssertion = new WiremockAssertionHelper(wiremockService);
        wiremockAssertion.assertSingleEndpoint();
        wiremockAssertion.assertWiremockAccessible();
        wiremockAssertion.assertNoConfigurationsRegistered();
    }

    @Test
    public void shouldStartWiremockServiceWithFullConfiguration() {
        // given
        var name = "wiremock-instance";
        var dockerVersion = "2.34.0";
        var endpointProperty = "propertyA.wiremock.url";
        var endpointEnvVariable = "WIREMOCK_URL";

        wiremockService = WiremockDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .done()
                .configure(new ServiceConfigureContext());

        // when
        wiremockService.start(ServiceStartupContext.empty());

        // then
        var wiremockAssertion = new WiremockAssertionHelper(wiremockService);
        wiremockAssertion.assertSingleEndpoint();
        wiremockAssertion.assertWiremockAccessible();
        wiremockAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
        wiremockAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
    }

    @Test
    public void shouldShutdownWiremockService() {
        // given
        var name = "wiremock-instance";
        var dockerVersion = "2.34.0";

        wiremockService = WiremockDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .done()
                .configure(new ServiceConfigureContext());

        // when
        wiremockService.start(ServiceStartupContext.empty());

        var endpoint = EndpointHelper.getAnyEndpointOrFail(wiremockService);

        wiremockService.shutdown();

        // then
        var wiremockAssertion = new WiremockAssertionHelper(wiremockService);
        wiremockAssertion.assertWiremockNotAccessible(endpoint);
    }

}
