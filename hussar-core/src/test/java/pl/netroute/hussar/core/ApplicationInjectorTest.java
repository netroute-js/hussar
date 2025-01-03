package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.application.api.HussarApplication;

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

    @Test
    public void shouldInjectApplicationWhenInheritancePresent() {
        // given
        var testInstance = new SubTestClassWithApplication();

        // when
        applicationInjector.inject(testInstance);

        // then
        assertApplicationInjected(testInstance, application);
        assertApplicationInjectionSkipped(testInstance);
    }

    private void assertApplicationInjected(TestClassWithApplication testInstance, Application application) {
        assertThat(testInstance.application).isEqualTo(application);
    }

    private void assertApplicationInjected(SubTestClassWithApplication testInstance, Application application) {
        assertThat(testInstance.baseApplication).isEqualTo(application);
        assertThat(testInstance.application).isEqualTo(application);
    }

    private void assertApplicationInjectionSkipped(TestClassWithApplication testInstance) {
        assertThat(testInstance.nonInjectableApplication).isNull();
    }

    private void assertApplicationInjectionSkipped(SubTestClassWithApplication testInstance) {
        assertThat(testInstance.baseNonInjectableApplication).isNull();
        assertThat(testInstance.nonInjectableApplication).isNull();
    }

    private static class TestClassWithApplication {

        @HussarApplication
        private Application application;

        private Application nonInjectableApplication;

    }

    private static class BaseTestClassWithApplication {

        @HussarApplication
        Application baseApplication;

        Application baseNonInjectableApplication;
    }

    private static class SubTestClassWithApplication extends BaseTestClassWithApplication {

        @HussarApplication
        Application application;

        Application nonInjectableApplication;
    }

}
