package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;
import pl.netroute.hussar.core.domain.StubServiceConfigurer;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.environment.api.HussarEnvironment;
import pl.netroute.hussar.core.environment.api.LocalEnvironmentConfigurer;

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

        private final Application application;
        private final StubServiceConfigurer<ServiceTestA> standaloneServiceA;
        private final StubServiceConfigurer<ServiceTestB> standaloneServiceB;

        TestEnvironmentConfigurerProvider() {
            this.application = mock(Application.class);
            this.standaloneServiceA = new StubServiceConfigurer<>(ServiceTestA.class);
            this.standaloneServiceB = new StubServiceConfigurer<>(ServiceTestB.class);
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
