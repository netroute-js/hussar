package pl.netroute.hussar.core.service.resolver;

import pl.netroute.hussar.core.helper.ValidatorHelper;

public class DockerImageResolver {
    private static final String DOCKER_IMAGE_TEMPLATE = "%s:%s";

    private DockerImageResolver() {}

    public static String resolve(String dockerImage, String dockerImageVersion) {
        ValidatorHelper.requireNonEmpty(dockerImage, "dockerImage");
        ValidatorHelper.requireNonEmpty(dockerImageVersion, "dockerImageVersion");

        return String.format(DOCKER_IMAGE_TEMPLATE, dockerImage, dockerImageVersion);
    }

}
