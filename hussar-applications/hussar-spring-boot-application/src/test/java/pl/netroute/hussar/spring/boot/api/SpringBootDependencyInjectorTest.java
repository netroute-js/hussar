package pl.netroute.hussar.spring.boot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SpringBootDependencyInjectorTest {
    private ConfigurableApplicationContext applicationContext;
    private SpringBootDependencyInjector dependencyInjector;

    @BeforeEach
    public void setup() {
        applicationContext = mock(ConfigurableApplicationContext.class, RETURNS_DEEP_STUBS);

        dependencyInjector = new SpringBootDependencyInjector(applicationContext);
    }

    @Test
    public void shouldInjectDependencies() {
        // given
        var testInstance = new Object();

        // when
        dependencyInjector.injectDependencies(testInstance);

        // then
        assertDependenciesInjected(testInstance);
    }

    private void assertDependenciesInjected(Object testInstance) {
        var beanFactory = applicationContext.getAutowireCapableBeanFactory();

        var autowiredMode = 0;
        var dependencyCheck = false;

        verify(beanFactory).autowireBeanProperties(testInstance, autowiredMode, dependencyCheck);
    }

}
