package pl.netroute.hussar.core.service.resolver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * A custom Docker image resolver.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DockerImageResolver {
    private static final String DOCKER_IMAGE_TEMPLATE = "%s:%s";

    /**
     * Resolves formatted Docker image.
     *
     * @param dockerImage - the docker image.
     * @param dockerImageVersion - the docker image version.
     * @return the formatted Docker image.
     */
    public static String resolve(@NonNull String dockerImage,
                                 @NonNull String dockerImageVersion) {
        return DOCKER_IMAGE_TEMPLATE.formatted(dockerImage, dockerImageVersion);
    }

}
