package pl.netroute.hussar.core.environment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mockito.ArgumentCaptor;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceConfigurer;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class LocalEnvironmentConfigurerVerifier {

    void verifyEnvironmentConfigured(@NonNull Environment environment,
                                     @NonNull Application expectedApplication,
                                     @NonNull DockerRegistry expectedDockerRegistry,
                                     @NonNull Set<ConfigurationEntry> expectedExternalConfigurations,
                                     @NonNull Set<ServiceConfigurer<? extends Service>> expectedServiceConfigurers) {
        verifyApplicationConfigured(environment, expectedApplication);
        verifyServicesConfigured(environment, expectedDockerRegistry, expectedServiceConfigurers);
        verifyExternalConfigurationConfigured(environment, expectedExternalConfigurations);
    }

    private static void verifyApplicationConfigured(Environment environment, Application expectedApplication) {
        assertThat(environment.getApplication()).isEqualTo(expectedApplication);
    }

    private static void verifyServicesConfigured(Environment environment,
                                                 DockerRegistry expectedDockerRegistry,
                                                 Set<ServiceConfigurer<? extends Service>> expectedServiceConfigurers) {
        var contextCaptor = ArgumentCaptor.forClass(ServiceConfigureContext.class);
        expectedServiceConfigurers.forEach(serviceConfigurer -> verify(serviceConfigurer).configure(contextCaptor.capture()));

        var actualContext = contextCaptor.getValue();
        assertThat(actualContext.dockerRegistry()).isEqualTo(expectedDockerRegistry);

        var services = environment.getServiceRegistry().getEntries();
        assertThat(services).hasSize(expectedServiceConfigurers.size());
    }

    private static void verifyExternalConfigurationConfigured(Environment environment,
                                                              Set<ConfigurationEntry> expectedExternalConfigurations) {
        var externalConfigurations = environment.getConfigurationRegistry().getEntries();

        assertThat(externalConfigurations).containsExactlyInAnyOrderElementsOf(expectedExternalConfigurations);
    }

}
