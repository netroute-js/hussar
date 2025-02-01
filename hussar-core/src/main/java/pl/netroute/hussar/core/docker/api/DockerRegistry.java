package pl.netroute.hussar.core.docker.api;

import lombok.NonNull;

public record DockerRegistry(@NonNull String url) {
    private static final String DEFAULT_URL = "";

    public static DockerRegistry defaultRegistry() {
        return new DockerRegistry(DEFAULT_URL);
    }

}
