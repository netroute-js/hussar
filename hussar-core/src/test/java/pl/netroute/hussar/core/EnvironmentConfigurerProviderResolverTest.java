package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.test.HussarAwareTest;
import pl.netroute.hussar.core.test.PlainTest;
import pl.netroute.hussar.core.test.TestEnvironmentConfigurerProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvironmentConfigurerProviderResolverTest {
    private EnvironmentConfigurerProviderResolver resolver;

    @BeforeEach
    public void setup() {
        resolver = new EnvironmentConfigurerProviderResolver();
    }

    @Test
    public void shouldResolveConfigurer() {
        // given
        var testInstance = new HussarAwareTest();

        // when
        var configurerProvider = resolver
                .resolve(testInstance)
                .orElseThrow();

        // then
        assertThat(configurerProvider).isInstanceOf(TestEnvironmentConfigurerProvider.class);
    }

    @Test
    public void shouldNotResolveWhenAnnotationMissing() {
        // given
        var testInstance = new PlainTest();

        // when
        var maybeResolved = resolver.resolve(testInstance);

        // then
        assertThat(maybeResolved).isEmpty();
    }

}
