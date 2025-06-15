package pl.netroute.hussar.core.service.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.docker.api.DockerRegistry;

import java.time.Duration;

/**
 * A base configurer class for all Hussar Docker {@link Service}. The configurer is responsible for configuring {@link Service}.
 *
 * @param <S> the type of the {@link Service} to be configured.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public abstract class BaseDockerServiceConfigurer<S extends Service> extends BaseServiceConfigurer<S> {

    /**
     * The default Docker image version.
     */
    @NonNull
    protected final @Builder.Default String dockerImageVersion = "latest";

    /**
     * The default startup timeout of Docker container
     */
    @NonNull
    protected final @Builder.Default Duration startupTimeout = Duration.ofSeconds(90L);

    /**
     * Overridden docker registry for this specific {@link BaseDockerService}
     */
    protected final DockerRegistry dockerRegistry;

}
