package pl.netroute.hussar.core.service.resolver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.utility.DockerImageName;

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
    public static DockerImageName resolve(@NonNull String dockerRegistryUrl,
                                          @NonNull String dockerImage,
                                          @NonNull String dockerImageVersion) {
        if(isCustomRegistryConfigured(dockerRegistryUrl)) {
            return resolveDockerImageForCustomRegistry(dockerRegistryUrl, dockerImage, dockerImageVersion);
        }

        return resolveDockerImageForHubRegistry(dockerImage, dockerImageVersion);
    }

    private static boolean isCustomRegistryConfigured(String dockerRegistryUrl) {
        return !dockerRegistryUrl.isBlank();
    }

    private static DockerImageName resolveDockerImageForCustomRegistry(@NonNull String dockerRegistryUrl,
                                                                       @NonNull String dockerImage,
                                                                       @NonNull String dockerImageVersion) {
        var resolvedDockerRegistryUrl = resolveDockerRegistryUrl(dockerRegistryUrl);
        var customRegistryDockerImage = resolvedDockerRegistryUrl + dockerImage;
        var fullDockerImage = DOCKER_IMAGE_TEMPLATE.formatted(customRegistryDockerImage, dockerImageVersion);

        return DockerImageName
                .parse(fullDockerImage)
                .asCompatibleSubstituteFor(dockerImage);
    }

    private static DockerImageName resolveDockerImageForHubRegistry(@NonNull String dockerImage,
                                                                    @NonNull String dockerImageVersion) {
        var fullDockerImage = DOCKER_IMAGE_TEMPLATE.formatted(dockerImage, dockerImageVersion);

        return DockerImageName.parse(fullDockerImage);
    }

    private static String resolveDockerRegistryUrl(String dockerRegistryUrl) {
        if(!dockerRegistryUrl.trim().isEmpty() && !dockerRegistryUrl.endsWith(PATH_SEPARATOR)) {
            return dockerRegistryUrl + PATH_SEPARATOR;
        }

        return dockerRegistryUrl;
    }

}
