package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.service.wiremock.WiremockDockerServiceConfigurer;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.WIREMOCK_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.WIREMOCK_ALTERNATIVE_URL_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WiremockServiceFactory {
    public static final String WIREMOCK_NAME = "wiremock-instance";

    public static WiremockDockerServiceConfigurer create() {
        var dockerImageVersion = "3.6.0";

        return WiremockDockerServiceConfigurer
                .newInstance()
                .name(WIREMOCK_NAME)
                .dockerImageVersion(dockerImageVersion)
                .registerEndpointUnderProperty(WIREMOCK_ALTERNATIVE_URL_PROPERTY)
                .registerEndpointUnderEnvironmentVariable(WIREMOCK_URL_ENV_VARIABLE)
                .done();
    }

}
