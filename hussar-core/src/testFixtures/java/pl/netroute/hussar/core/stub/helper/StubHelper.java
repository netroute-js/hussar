package pl.netroute.hussar.core.stub.helper;

import lombok.NonNull;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class StubHelper {

    public static <T> T defaultStub(@NonNull Class<T> type) {
        return mock(type, Mockito.RETURNS_DEEP_STUBS);
    }

}
