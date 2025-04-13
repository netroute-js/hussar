package pl.netroute.hussar.core.test.stub;

import lombok.NonNull;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class Mock {

    public static <T> T defaultMock(@NonNull Class<T> type) {
        return mock(type, Mockito.RETURNS_DEEP_STUBS);
    }

}
