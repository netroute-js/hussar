package pl.netroute.hussar.core.stub.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.docker.DockerHostResolver;

import static org.mockito.Mockito.when;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DockerHostResolverStubHelper {
    public static final String LOCALHOST = "localhost";
    public static final String NON_LOCALHOST = "docker";

    public static void givenDockerLocalhost(@NonNull DockerHostResolver dockerHostResolver) {
        when(dockerHostResolver.isLocalHost()).thenReturn(true);
        when(dockerHostResolver.getHost()).thenReturn(LOCALHOST);
    }

    public static void givenDockerNonLocalhost(@NonNull DockerHostResolver dockerHostResolver) {
        when(dockerHostResolver.isLocalHost()).thenReturn(false);
        when(dockerHostResolver.getHost()).thenReturn(NON_LOCALHOST);
    }

}
