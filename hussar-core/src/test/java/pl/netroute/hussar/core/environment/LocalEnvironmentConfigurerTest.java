package pl.netroute.hussar.core.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.environment.api.LocalEnvironmentConfigurer;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceConfigurer;
import pl.netroute.hussar.core.test.factory.ConfigurationEntryTestFactory;
import pl.netroute.hussar.core.test.stub.ApplicationStub;
import pl.netroute.hussar.core.test.stub.ServiceStubConfigurerA;
import pl.netroute.hussar.core.test.stub.ServiceStubConfigurerB;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LocalEnvironmentConfigurerTest {
    private LocalEnvironmentConfigurerVerifier verifier = new LocalEnvironmentConfigurerVerifier();

    @BeforeEach
    public void setup() {
        verifier = new LocalEnvironmentConfigurerVerifier();
    }

    @Test
    public void shouldConfigureEnvironment() {
        // given
        var dockerRegistry = new DockerRegistry("netroute.pl/registry");

        var propertyConfiguration = ConfigurationEntryTestFactory.createProperty();
        var envVariableConfiguration = ConfigurationEntryTestFactory.createEnvVariable();
        var externalConfigurations = Set.<ConfigurationEntry>of(propertyConfiguration, envVariableConfiguration);

        var application = ApplicationStub.defaultStub();
        var serviceA = ServiceStubConfigurerA.defaultStub();
        var serviceB = ServiceStubConfigurerB.defaultStub();
        var services = Set.<ServiceConfigurer<? extends Service>>of(serviceA, serviceB);

        // when
        var environment = LocalEnvironmentConfigurer
                .newInstance()
                .withDockerRegistry(dockerRegistry)
                .withProperty(propertyConfiguration.name(), propertyConfiguration.value())
                .withEnvironmentVariable(envVariableConfiguration.name(), envVariableConfiguration.value())
                .withApplication(application)
                .withService(serviceA)
                .withService(serviceB)
                .done()
                .configure(EnvironmentConfigurerContext.defaultContext());

        // then
        verifier.verifyEnvironmentConfigured(environment, application, dockerRegistry, externalConfigurations, services);
    }

    @Test
    public void shouldFailConfiguringEnvironmentWhenApplicationIsMissing() {
        // given
        // when
        // then
        assertThatThrownBy(() -> LocalEnvironmentConfigurer
                    .newInstance()
                    .done()
                    .configure(EnvironmentConfigurerContext.defaultContext()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("application is marked non-null but is null");
    }

}
