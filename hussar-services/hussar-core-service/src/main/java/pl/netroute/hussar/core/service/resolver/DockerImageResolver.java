package pl.netroute.hussar.core.service.resolver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DockerImageResolver {
    private static final String DOCKER_IMAGE_TEMPLATE = "%s:%s";

    public static String resolve(@NonNull String dockerImage,
                                 @NonNull String dockerImageVersion) {
        return DOCKER_IMAGE_TEMPLATE.formatted(dockerImage, dockerImageVersion);
    }

}
