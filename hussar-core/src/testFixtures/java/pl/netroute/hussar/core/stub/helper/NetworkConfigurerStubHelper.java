package pl.netroute.hussar.core.stub.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.network.api.Network;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.stub.NetworkStub;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkConfigurerStubHelper {
    private static final String NETWORK_HOST = "localhost";
    private static final String ENDPOINT_REGEX_TEMPLATE = "^%shussar-svc-[a-f0-9]{8}-[a-f0-9]{8}:%d$";

    public static Network givenNetworkConfigured(@NonNull NetworkConfigurer networkConfigurer,
                                                 @NonNull String networkPrefix,
                                                 @NonNull String scheme,
                                                 @NonNull Integer... ports) {
        var expectedPorts = List.of(ports);
        var endpoints = expectedPorts
                .stream()
                .map(port -> Endpoint.of(scheme, NETWORK_HOST, port))
                .toList();

        var network = NetworkStub.newStub(endpoints);
        when(networkConfigurer.configure(eq(networkPrefix), argThat(actualEndpoints -> endpointsMatch(actualEndpoints, scheme, expectedPorts)))).thenReturn(network);

        return network;
    }

    private static boolean endpointsMatch(List<Endpoint> endpoints, String expectedSchema, List<Integer> expectedPorts) {
        var expectedEndpointSize = expectedPorts.size();

        if(expectedEndpointSize != endpoints.size()) {
            return false;
        }

        return IntStream
                .range(0, expectedEndpointSize)
                .filter(index -> endpointMatch(endpoints.get(index), expectedSchema, expectedPorts.get(index)))
                .count() == expectedEndpointSize;
    }

    private static boolean endpointMatch(Endpoint endpoint, String expectedSchema, Integer expectedPort) {
        var endpointRegex = String.format(ENDPOINT_REGEX_TEMPLATE, expectedSchema, expectedPort);

        return endpoint.address().matches(endpointRegex);
    }

}
