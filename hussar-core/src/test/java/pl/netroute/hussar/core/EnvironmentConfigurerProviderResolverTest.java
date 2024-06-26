package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.annotation.HussarEnvironment;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.LocalEnvironmentConfigurer;
import pl.netroute.hussar.core.api.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class EnvironmentConfigurerProviderResolverTest {
    private EnvironmentConfigurerProviderResolver resolver;

    @BeforeEach
    public void setup() {
        resolver = new EnvironmentConfigurerProviderResolver();
    }

    @Test
    public void shouldResolveConfigurer() {
        // given
        // when
        var configurerProvider = resolver
                .resolve(new ConfigurerAwareTest())
                .orElseThrow();

        // then
        assertThat(configurerProvider).isInstanceOf(TestEnvironmentConfigurerProvider.class);
    }

    @Test
    public void shouldNotResolveWhenAnnotationMissing() {
        // given
        // when
        var maybeResolved = resolver.resolve(new NoConfigurerAwareTest());

        // then
        assertThat(maybeResolved).isEmpty();
    }

    static class NoConfigurerAwareTest {
    }

    @HussarEnvironment(configurerProvider = TestEnvironmentConfigurerProvider.class)
    static class ConfigurerAwareTest {
    }

    static class TestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {
        private static final String PROPERTY_1 = "some.property";
        private static final String PROPERTY_VALUE_1 = "some_property_value";

        private static final String ENV_VARIABLE_1 = "SOME_ENV_VARIABLE";
        private static final String ENV_VARIABLE_VALUE_1 = "some_env_variable_value";

        private final Application application = mock(Application.class);
        private final Service standaloneServiceA = mock(Service.class);
        private final Service standaloneServiceB = mock(Service.class);

        public TestEnvironmentConfigurerProvider() {
        }

        @Override
        public LocalEnvironmentConfigurer provide() {
            return LocalEnvironmentConfigurer
                    .newInstance()
                    .withProperty(PROPERTY_1, PROPERTY_VALUE_1)
                    .withEnvironmentVariable(ENV_VARIABLE_1, ENV_VARIABLE_VALUE_1)
                    .withApplication(application)
                    .withService(standaloneServiceA)
                    .withService(standaloneServiceB)
                    .done();
        }

    }
}
