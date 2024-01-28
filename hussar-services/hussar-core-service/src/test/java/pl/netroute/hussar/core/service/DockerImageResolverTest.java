package pl.netroute.hussar.core.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class DockerImageResolverTest {
    private static final String DOCKER_IMAGE_TEMPLATE = "%s:%s";

    @ParameterizedTest
    @ValueSource(strings = {
            "latest",
            "23.10"
    })
    public void shouldResolveImage(String imageVersion) {
        // given
        var baseImage = "ubuntu";

        // when
        var resolvedImage = DockerImageResolver.resolve(baseImage, imageVersion);

        // then
        var expectedImage = String.format(DOCKER_IMAGE_TEMPLATE, baseImage, imageVersion);

        assertResolvedImage(resolvedImage, expectedImage);
    }

    private void assertResolvedImage(String resolvedImage, String expectedImage) {
        assertThat(resolvedImage).isEqualTo(expectedImage);
    }

}
