package pl.netroute.hussar.core.service;

import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.Optional;

public abstract class BaseDockerServiceConfigurer<S extends Service, C extends BaseDockerServiceConfigurer<S, C>> extends BaseServiceConfigurer<S, C> {
    private final String dockerImage;
    private final Optional<String> scheme;

    private String dockerImageVersion = "latest";

    public BaseDockerServiceConfigurer(String dockerImage, String scheme) {
        ValidatorHelper.requireNonEmpty(dockerImage, "dockerImage");

        this.dockerImage = dockerImage;
        this.scheme = Optional.ofNullable(scheme);
    }

    public C dockerImageVersion(String dockerImageVersion) {
        ValidatorHelper.requireNonEmpty(dockerImageVersion, "dockerImageVersion");

        this.dockerImageVersion = dockerImageVersion;

        return (C) this;
    }

    protected String getDockerImage() {
        return dockerImage;
    }

    protected String getDockerImageVersion() {
        return dockerImageVersion;
    }

    protected Optional<String> getScheme() {
        return scheme;
    }

    protected String resolveDockerImage() {
        return DockerImageResolver.resolve(dockerImage, dockerImageVersion);
    }
}
