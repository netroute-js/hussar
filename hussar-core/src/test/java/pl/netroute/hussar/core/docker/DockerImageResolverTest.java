package pl.netroute.hussar.core.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.utility.DockerImageName;

public class DockerImageResolverTest {
    private static final String DOCKER_IMAGE_TEMPLATE = "%s:%s";
    private static final String DOCKER_IMAGE_WITH_CUSTOM_REGISTRY_TEMPLATE = "%s/%s:%s";

    @ParameterizedTest
    @ValueSource(strings = {
            "latest",
            "23.10"
    })
    public void shouldResolveImage(String imageVersion) {
        // given
        var dockerRegistryURL = "";
        var baseImage = "ubuntu";

        // when
        var resolvedImage = DockerImageResolver.resolve(dockerRegistryURL, baseImage, imageVersion);

        // then
        var expectedImage = String.format(DOCKER_IMAGE_TEMPLATE, baseImage, imageVersion);

        assertResolvedImage(resolvedImage, expectedImage);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "latest",
            "23.10"
    })
    public void shouldResolveImageWithCustomDockerRegistryURL(String imageVersion) {
        // given
        var dockerRegistryURL = "custom-docker.netroute.pl";
        var baseImage = "ubuntu";

        // when
        var resolvedImage = DockerImageResolver.resolve(dockerRegistryURL, baseImage, imageVersion);

        // then
        var expectedImage = String.format(DOCKER_IMAGE_WITH_CUSTOM_REGISTRY_TEMPLATE, dockerRegistryURL, baseImage, imageVersion);

        assertResolvedImage(resolvedImage, expectedImage);
    }

    private void assertResolvedImage(DockerImageName resolvedImage, String expectedImage) {
        Assertions.assertThat(resolvedImage.asCanonicalNameString()).isEqualTo(expectedImage);
    }

}
