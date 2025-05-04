package pl.netroute.hussar.core.stub.helper;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericContainerStubHelper {

    public static void givenContainerAccessible(@NonNull GenericContainer<?> container,
                                                @NonNull GenericContainerAccessibility accessibility) {
        when(container.getHost()).thenReturn(accessibility.host());

        if(container instanceof FixedHostPortGenericContainer<?> fixedContainer) {
            configureFixedHostPortContainer(fixedContainer, accessibility);
        } else {
            configureGenericContainer(container, accessibility);
        }
    }

    private static void configureFixedHostPortContainer(FixedHostPortGenericContainer<?> container,
                                                        GenericContainerAccessibility accessibility) {
        var exposedPorts = accessibility.exposedPorts();

        when(container.getBoundPortNumbers()).thenReturn(exposedPorts);
    }

    private static void configureGenericContainer(GenericContainer<?> container,
                                                  GenericContainerAccessibility accessibility) {
        when(container.getExposedPorts()).thenReturn(accessibility.exposedPorts());

        accessibility
                .mappedPorts()
                .forEach((containerPort, hostPort) -> when(container.getMappedPort(containerPort)).thenReturn(hostPort));
    }

    @Builder(toBuilder = true)
    public record GenericContainerAccessibility(@NonNull String host,
                                                @NonNull @Singular List<Integer> exposedPorts,
                                                @NonNull @Singular Map<Integer, Integer> mappedPorts) {
    }

}
