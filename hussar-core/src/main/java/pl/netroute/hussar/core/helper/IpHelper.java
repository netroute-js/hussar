package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Helper class for IP address related operations.
 * This utility class provides methods to retrieve network information
 * such as the local machine's routable IP address.
 */
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpHelper {
    private static final String GOOGLE_IP = "8.8.8.8";
    private static final int GOOGLE_PORT = 80;

    /**
     * Retrieves the local machine's routable IP address.
     *
     * @return the routable IP address of the local machine as a string
     * @throws IllegalStateException if the routable IP address cannot be determined
     */
    public static String getRoutableIP() {
        try(var socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName(GOOGLE_IP), GOOGLE_PORT);

            return socket.getLocalAddress().getHostAddress();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not get routable IP", ex);
        }
    }

}
