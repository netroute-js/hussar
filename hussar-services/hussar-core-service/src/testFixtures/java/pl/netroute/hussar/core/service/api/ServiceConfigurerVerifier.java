package pl.netroute.hussar.core.service.api;

import lombok.NonNull;
import pl.netroute.hussar.core.docker.api.DockerRegistry;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceConfigurerVerifier {

    public void verifyServiceDockerRegistryConfigured(@NonNull BaseDockerService<? extends BaseDockerServiceConfig> service, @NonNull DockerRegistry expectedRegistry) {
        var dockerImage = service.config.getDockerImage();
        var dockerRegistryUrl = expectedRegistry.url();

        assertThat(dockerImage).startsWith(dockerRegistryUrl);
    }

}
