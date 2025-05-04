package pl.netroute.hussar.service.kafka;

import pl.netroute.hussar.core.service.BaseServiceNetworkIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.kafka.api.KafkaDockerService;
import pl.netroute.hussar.service.kafka.assertion.KafkaAssertionHelper;

import java.util.function.Consumer;

class KafkaDockerServiceNetworkIT extends BaseServiceNetworkIT<KafkaDockerService> {

    @Override
    protected ServiceTestMetadata<KafkaDockerService, Consumer<KafkaDockerService>> provideEnableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = KafkaDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<KafkaDockerService>) actualService -> {
            var kafkaAssertion = new KafkaAssertionHelper(actualService);
            kafkaAssertion.asserKafkaAccessible();
        };

        return ServiceTestMetadata
                .<KafkaDockerService, Consumer<KafkaDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<KafkaDockerService, Consumer<KafkaDockerService>> provideDisableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = KafkaDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<KafkaDockerService>) actualService -> {
            var kafkaAssertion = new KafkaAssertionHelper(actualService);
            kafkaAssertion.assertKafkaNotAccessible();
        };

        return ServiceTestMetadata
                .<KafkaDockerService, Consumer<KafkaDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<KafkaDockerService, Consumer<KafkaDockerService>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext context) {
        var service = KafkaDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<KafkaDockerService>) actualService -> {
            var kafkaAssertion = new KafkaAssertionHelper(actualService);
            kafkaAssertion.asserKafkaAccessible();
        };

        return ServiceTestMetadata
                .<KafkaDockerService, Consumer<KafkaDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<KafkaDockerService, Consumer<KafkaDockerService>> provideResetNetworkTestMetadata(ServiceConfigureContext context) {
        var service = KafkaDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<KafkaDockerService>) actualService -> {
            var kafkaAssertion = new KafkaAssertionHelper(actualService);
            kafkaAssertion.asserKafkaAccessible();
        };

        return ServiceTestMetadata
                .<KafkaDockerService, Consumer<KafkaDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
