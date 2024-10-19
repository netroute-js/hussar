package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A port finder helper.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PortFinderHelper {
    private static final int START_PORT_SCAN_RANGE = 30000;
    private static final int END_PORT_SCAN_RANGE = 40000;

    /**
     * Finds the first free port in local machine.
     * The port range is between 30000 and 40000.
     *
     * @return first free port in a given range.
     */
    public static int findFreePort() {
        return IntStream
                .range(START_PORT_SCAN_RANGE, END_PORT_SCAN_RANGE)
                .mapToObj(PortFinderHelper::verifyPortAvailable)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find free port in range"));
    }

    private static Optional<Integer> verifyPortAvailable(int port) {
        try(var socket = new ServerSocket(port)) {
            return Optional.of(port);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

}
