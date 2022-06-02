package pl.netroute.hussar.core.api;

import java.util.List;

public interface Mock extends Accessible, Lifecycle {
    String getName();

    default List<String> getDependsOnMocks() {
        return List.of();
    }
}
