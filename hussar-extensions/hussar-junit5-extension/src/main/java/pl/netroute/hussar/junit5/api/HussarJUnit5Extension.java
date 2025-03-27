package pl.netroute.hussar.junit5.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import pl.netroute.hussar.core.Hussar;

/**
 * Hussar extension to seamlessly integrate with JUnit5 testing framework.
 */
@Slf4j
public class HussarJUnit5Extension implements BeforeEachCallback, TestInstancePostProcessor {
    private static final Hussar HUSSAR_INSTANCE = Hussar.newInstance();

    @Override
    public void beforeEach(ExtensionContext context) {
        interceptTest(context);
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        HUSSAR_INSTANCE.initializeFor(testInstance);
    }

    private void interceptTest(ExtensionContext context) {
        var testInstance = context
                .getTestInstance()
                .orElseThrow(() -> new IllegalStateException("Could not find test instance"));

        var testMethod = context
                .getTestMethod()
                .orElseThrow(() -> new IllegalStateException("Could not find test method"));

        HUSSAR_INSTANCE.interceptTest(testInstance, testMethod);
    }

    static Hussar getHussarInstance() {
        return HUSSAR_INSTANCE;
    }

}
