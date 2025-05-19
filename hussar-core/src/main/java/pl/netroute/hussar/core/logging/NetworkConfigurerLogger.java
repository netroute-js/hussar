package pl.netroute.hussar.core.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.helper.StringHelper;

import java.util.Map;
import java.util.Optional;

@Slf4j
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkConfigurerLogger {
    private static final String NEW_LINE_DELIMITER = "\n";
    private static final String NOT_APPLICABLE = "N/A";

    private static final String NETWORK_CONFIGURED_TEMPLATE =
            """
            
            Network
            Name: %s
            Endpoints:
            %s
            """;

    private static final String NETWORK_ENDPOINTS_TEMPLATE = "- %s -> %s";

    public static void logNetworkConfigured(@NonNull String network,
                                            @NonNull Map<Endpoint, Endpoint> endpointsMap) {
        var formattedEndpoints = formatEndpoints(endpointsMap);

        var logText = NETWORK_CONFIGURED_TEMPLATE.formatted(network, formattedEndpoints);

        log.info(logText);
    }

    private static String formatEndpoints(Map<Endpoint, Endpoint> endpointsMap) {
        return Optional
                .of(endpointsMap.entrySet())
                .filter(actualEndpoints -> !actualEndpoints.isEmpty())
                .map(actualEndpoints -> StringHelper.join(NetworkConfigurerLogger::formatEndpointsMapping, NEW_LINE_DELIMITER, actualEndpoints))
                .orElse(NOT_APPLICABLE);
    }

    private static String formatEndpointsMapping(Map.Entry<Endpoint, Endpoint> endpointsMapping) {
        var endpoint = endpointsMapping.getKey();
        var upstreamEndpoint = endpointsMapping.getValue();

        return NETWORK_ENDPOINTS_TEMPLATE.formatted(endpoint.address(), upstreamEndpoint.address());
    }

}
