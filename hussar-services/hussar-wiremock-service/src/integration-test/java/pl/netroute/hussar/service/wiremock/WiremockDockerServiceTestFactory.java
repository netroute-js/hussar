package pl.netroute.hussar.service.wiremock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.wiremock.api.WiremockDockerService;
import pl.netroute.hussar.service.wiremock.api.WiremockDockerServiceConfigurer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WiremockDockerServiceTestFactory {

    public static WiremockDockerService createMinimallyConfigured(@NonNull ServiceConfigureContext context) {
        return WiremockDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(context);
    }

}
