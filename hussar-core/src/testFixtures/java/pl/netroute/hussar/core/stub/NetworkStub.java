package pl.netroute.hussar.core.stub;

import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.network.api.Network;
import pl.netroute.hussar.core.network.api.NetworkControl;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.util.List;

import static org.mockito.Mockito.spy;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class NetworkStub implements Network {

    @NonNull
    private final List<Endpoint> endpoints;

    @NonNull
    @Builder.Default
    private final NetworkControl networkControl = StubHelper.defaultStub(NetworkControl.class);

    @Override
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    @Override
    public NetworkControl getNetworkControl() {
        return networkControl;
    }

    public static NetworkStub newStub(@NonNull List<Endpoint> endpoints) {
        var network = NetworkStub
                .newInstance()
                .endpoints(endpoints)
                .done();

        return spy(network);
    }

}
