package pl.netroute.hussar.core.service;

import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.Objects;
import java.util.Optional;

public abstract class BaseDockerServiceConfig extends BaseServiceConfig {
    private final String dockerImage;
    private final Optional<String> scheme;

    public BaseDockerServiceConfig(String name, String dockerImage, Optional<String> scheme) {
        super(name);

        ValidatorHelper.requireNonEmpty(dockerImage, "dockerImage is required");
        Objects.requireNonNull(scheme, "scheme is required");

        this.dockerImage = dockerImage;
        this.scheme = scheme;
    }

    public String getDockerImage() {
        return dockerImage;
    }

    public Optional<String> getScheme() {
        return scheme;
    }

}
