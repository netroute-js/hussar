package pl.netroute.hussar.core.docker;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.docker.api.DockerRegistry;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class DockerRegistryResolverVerifier {

    void verifyDockerRegistryResolved(@NonNull DockerRegistry resolvedRegistry,
                                      @NonNull DockerRegistry expectedRegistry) {
        assertThat(resolvedRegistry).isEqualTo(expectedRegistry);
    }

}