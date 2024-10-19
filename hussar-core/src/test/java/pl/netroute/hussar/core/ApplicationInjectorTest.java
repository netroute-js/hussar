package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.application.HussarApplication;
import pl.netroute.hussar.core.api.application.Application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ApplicationInjectorTest {
    private Application application;
    private ApplicationInjector applicationInjector;

    @BeforeEach
    public void setup() {
        application = mock(Application.class);

        applicationInjector = new ApplicationInjector(application);
    }

    @Test
    public void shouldInjectApplication() {
        // given
        var testInstance = new TestClassWithApplication();

        // when
        applicationInjector.inject(testInstance);

        // then
        assertApplicationInjected(testInstance, application);
        assertApplicationInjectionSkipped(testInstance);
    }

    private void assertApplicationInjected(TestClassWithApplication testInstance, Application application) {
        assertThat(testInstance.application).isEqualTo(application);
    }

    private void assertApplicationInjectionSkipped(TestClassWithApplication testInstance) {
        assertThat(testInstance.nonInjectableApplication).isNull();
    }

    private static class TestClassWithApplication {

        @HussarApplication
        private Application application;

        private Application nonInjectableApplication;

    }

}
