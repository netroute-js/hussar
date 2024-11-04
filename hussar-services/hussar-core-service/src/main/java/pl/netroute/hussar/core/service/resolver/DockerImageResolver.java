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
    private static final String PATH_SEPARATOR = "/";

    /**
     * Resolves formatted Docker image.
     *
     * @param dockerRegistryUrl - the docker registry URL.
     * @param dockerImage - the docker image.
     * @param dockerImageVersion - the docker image version.
     * @return the formatted Docker image.
     */
    public static String resolve(@NonNull String dockerRegistryUrl,
                                 @NonNull String dockerImage,
                                 @NonNull String dockerImageVersion) {
        var resolvedDockerRegistryUrl = resolveDockerRegistryUrl(dockerRegistryUrl);
        var fullDockerImage = resolvedDockerRegistryUrl + dockerImage;

        return DOCKER_IMAGE_TEMPLATE.formatted(fullDockerImage, dockerImageVersion);
    }

    private static String resolveDockerRegistryUrl(String dockerRegistryUrl) {
        if(!dockerRegistryUrl.trim().isEmpty() && !dockerRegistryUrl.endsWith(PATH_SEPARATOR)) {
            return dockerRegistryUrl + PATH_SEPARATOR;
        }

        return dockerRegistryUrl;
    }

}
