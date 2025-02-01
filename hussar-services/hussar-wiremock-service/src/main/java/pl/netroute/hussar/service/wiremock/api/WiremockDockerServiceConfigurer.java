package pl.netroute.hussar.service.wiremock.api;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.docker.GenericContainerFactory;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.docker.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;

/**
 * Hussar {@link WiremockDockerService} configurer. This is the only way to create {@link WiremockDockerService}.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class WiremockDockerServiceConfigurer extends BaseDockerServiceConfigurer<WiremockDockerService> {
    private static final String DOCKER_IMAGE = "wiremock/wiremock";
    private static final String SERVICE = "wiremock_service";

    @Override
    public WiremockDockerService configure(@NonNull ServiceConfigureContext context) {
        var dockerRegistry = context.dockerRegistry();
        var dockerImage = DockerImageResolver.resolve(dockerRegistry, DOCKER_IMAGE, dockerImageVersion);
        var config = createConfig(dockerImage);
        var container = GenericContainerFactory.create(dockerImage);
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);

        return new WiremockDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer
        );
    }

    private WiremockDockerServiceConfig createConfig(DockerImageName dockerImage) {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);
        var scheme = SchemesHelper.HTTP_SCHEME;

        return WiremockDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(dockerImage.asCanonicalNameString())
                .scheme(scheme)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvironmentVariables)
                .build();
    }

}
