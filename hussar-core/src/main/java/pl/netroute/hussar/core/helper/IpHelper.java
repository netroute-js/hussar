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
 * 
 * <p>This class is for internal use only and should not be used directly
 * by external applications.</p>
 */
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpHelper {
    private static final String GOOGLE_IP = "8.8.8.8";
    private static final int GOOGLE_PORT = 80;

    /**
     * Retrieves the local machine's routable IP address.
     * 
     * <p>This method works by creating a UDP socket and connecting it to a public IP address
     * (Google's DNS server 8.8.8.8). When a socket is "connected" to a remote address,
     * the operating system selects the local interface that would be used to reach that
     * address, which gives us the routable IP address of the machine.</p>
     * 
     * <p>Note that this method does not actually send any data to the remote address,
     * it only uses the socket connection mechanism to determine the appropriate local interface.</p>
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
