package pl.netroute.hussar.service.wiremock;

import pl.netroute.hussar.core.service.BaseServiceNetworkIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.wiremock.api.WiremockDockerService;
import pl.netroute.hussar.service.wiremock.assertion.WiremockAssertionHelper;

import java.util.function.Consumer;

class WiremockDockerServiceNetworkIT extends BaseServiceNetworkIT<WiremockDockerService> {

    @Override
    protected ServiceTestMetadata<WiremockDockerService, Consumer<WiremockDockerService>> provideEnableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = WiremockDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<WiremockDockerService>) actualService -> {
            var wiremockAssertion = new WiremockAssertionHelper(actualService);
            wiremockAssertion.assertWiremockAccessible();
        };

        return ServiceTestMetadata
                .<WiremockDockerService, Consumer<WiremockDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<WiremockDockerService, Consumer<WiremockDockerService>> provideDisableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = WiremockDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<WiremockDockerService>) actualService -> {
            var wiremockAssertion = new WiremockAssertionHelper(actualService);
            wiremockAssertion.assertWiremockNotAccessible();
        };

        return ServiceTestMetadata
                .<WiremockDockerService, Consumer<WiremockDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<WiremockDockerService, Consumer<WiremockDockerService>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext context) {
        var service = WiremockDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<WiremockDockerService>) actualService -> {
            var wiremockAssertion = new WiremockAssertionHelper(actualService);
            wiremockAssertion.assertWiremockAccessible();
        };

        return ServiceTestMetadata
                .<WiremockDockerService, Consumer<WiremockDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<WiremockDockerService, Consumer<WiremockDockerService>> provideResetNetworkTestMetadata(ServiceConfigureContext context) {
        var service = WiremockDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<WiremockDockerService>) actualService -> {
            var wiremockAssertion = new WiremockAssertionHelper(actualService);
            wiremockAssertion.assertWiremockAccessible();
        };

        return ServiceTestMetadata
                .<WiremockDockerService, Consumer<WiremockDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
