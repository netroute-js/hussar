package pl.netroute.hussar.spring.boot.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.dependency.api.DependencyInjector;

@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class SpringBootDependencyInjector implements DependencyInjector {
    private static final int AUTOWIRE_MODE = 0;
    private static final boolean DEPENDENCY_CHECK = false;

    private final ConfigurableApplicationContext applicationContext;

    @Override
    public void injectDependencies(@NonNull Object testInstance) {
        var beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBeanProperties(testInstance, AUTOWIRE_MODE, DEPENDENCY_CHECK);
        beanFactory.initializeBean(testInstance, testInstance.getClass().getCanonicalName());
    }

}
