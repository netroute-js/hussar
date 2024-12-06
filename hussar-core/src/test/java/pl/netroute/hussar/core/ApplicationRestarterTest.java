package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.application.Application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ApplicationRestarterTest {
    private ApplicationRestarter applicationRestarter;

    @BeforeEach
    public void setup() {
        applicationRestarter = new ApplicationRestarter();
    }

    @Test
    public void shouldRestartApplication() {
        // given
        var application = mock(Application.class);

        // when
        applicationRestarter.restart(application);

        // then
        assertApplicationRefreshed(application);
    }

    private void assertApplicationRefreshed(Application application) {
        verify(application).restart();
    }

}
