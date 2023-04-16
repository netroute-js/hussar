package pl.netroute.hussar.junit5.config;

import com.netroute.hussar.wiremock.WiremockServiceConfigurer;
import pl.netroute.hussar.core.EnvironmentConfigurer;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.spring.boot.SpringApplication;

public class TestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {
    public static final String WIREMOCK_A = "WiremockA";
    public static final String WIREMOCK_B = "WiremockB";

    public static final String PROPERTY_A = "propertyA";
    public static final String PROPERTY_A_VALUE = "propertyA_value";

    public static final String WIREMOCK_INSTANCE_A_URL_PROPERTY = "pl.netroute.wiremockA.url";
    public static final String WIREMOCK_INSTANCE_B_URL_PROPERTY = "pl.netroute.wiremockB.url";

    @Override
    public EnvironmentConfigurer provide() {
        var application = SpringApplication.newApplication(SimpleSpringApplication.class);

        var wiremockServiceA = WiremockServiceConfigurer
                .newInstance()
                .name(WIREMOCK_A)
                .registerEndpointUnderProperty(WIREMOCK_INSTANCE_A_URL_PROPERTY)
                .configure();

        var wiremockServiceB = WiremockServiceConfigurer
                .newInstance()
                .name(WIREMOCK_B)
                .registerEndpointUnderProperty(WIREMOCK_INSTANCE_B_URL_PROPERTY)
                .configure();

        return EnvironmentConfigurer
                .newConfigurer()
                .withProperty(PROPERTY_A, PROPERTY_A_VALUE)
                .withStandaloneService(wiremockServiceA)
                .withStandaloneService(wiremockServiceB)
                .withApplication(application);
    }

}
