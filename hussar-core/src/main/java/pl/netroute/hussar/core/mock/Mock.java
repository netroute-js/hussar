package pl.netroute.hussar.core.mock;

import java.util.List;

public interface Mock {
    String getName();
    List<Endpoint> getEndpoints();

    void start();
    void stop();
}
