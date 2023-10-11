package pl.netroute.hussar.junit5.config;

import com.netroute.hussar.wiremock.WiremockServiceConfigurer;
import pl.netroute.hussar.core.EnvironmentConfigurer;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.service.api.RegistrableConfigurationEntry;
import pl.netroute.hussar.spring.boot.SpringApplication;

public class TestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {
    public static final String WIREMOCK_A = "WiremockA";
    public static final String WIREMOCK_B = "WiremockB";

    public static final String PROPERTY_A = "propertyA";
    public static final String SUB_PROPERTY_A = "subA";
    public static final String PROPERTY_B = "propertyB";
    public static final String WIREMOCK_INSTANCE_A_URL_PROPERTY = "wiremockA.url";
    public static final String WIREMOCK_INSTANCE_B_URL_PROPERTY = "wiremockB.url";

    public static final String ENV_VARIABLE_A = "SOME_ENV_VARIABLE_A";

    public static final String PROPERTY_A_VALUE = "propertyA_value";
    public static final String PROPERTY_B_VALUE = "default-valueB";

    public static final String ENV_VARIABLE_A_VALUE = "SOME_ENV_VARIABLE_A_VALUE";
    public static final String WIREMOCK_INSTANCE_B_URL_ENV_VARIABLE = "WIREMOCK_B_URL";

    @Override
    public EnvironmentConfigurer provide() {
        var application = SpringApplication.newApplication(SimpleSpringApplication.class);

        var wiremockServiceA = WiremockServiceConfigurer
                .newInstance()
                .name(WIREMOCK_A)
                .registerEndpointUnderEntry(RegistrableConfigurationEntry.property(WIREMOCK_INSTANCE_A_URL_PROPERTY))
                .configure();

        var wiremockServiceB = WiremockServiceConfigurer
                .newInstance()
                .name(WIREMOCK_B)
                .registerEndpointUnderEntry(RegistrableConfigurationEntry.envVariable(WIREMOCK_INSTANCE_B_URL_ENV_VARIABLE))
                .configure();

        return EnvironmentConfigurer
                .newConfigurer()
                .withStaticConfigurationEntry(ConfigurationEntry.property(PROPERTY_A, PROPERTY_A_VALUE))
                .withStaticConfigurationEntry(ConfigurationEntry.envVariable(ENV_VARIABLE_A, ENV_VARIABLE_A_VALUE))
                .withStandaloneService(wiremockServiceA)
                .withStandaloneService(wiremockServiceB)
                .withApplication(application);
    }

}
