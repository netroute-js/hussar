package pl.netroute.hussar.junit5;

import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import pl.netroute.hussar.core.Hussar;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import java.util.Optional;

public class HussarJUnit5Extension implements BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor {
    private static final String HUSSAR_ENVIRONMENT = "HUSSAR";
    private static final Namespace HUSSAR_NAMESPACE = Namespace.create(HUSSAR_ENVIRONMENT);

    @Override
    public void beforeAll(ExtensionContext context) {
        initializeHussar(context);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        shutdownHussar(context);
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        getHussarInstance(context).initializeFor(testInstance);
    }

    private void initializeHussar(ExtensionContext context) {
        var store = getHussarStore(context);
        store.put(HUSSAR_ENVIRONMENT, Hussar.newInstance());
    }

    private void shutdownHussar(ExtensionContext context) {
        getHussarInstance(context).shutdown();

    }

    private Hussar getHussarInstance(ExtensionContext context) {
        Store store = getHussarStore(context);

        return Optional
                .ofNullable(store.get(HUSSAR_ENVIRONMENT))
                .map(hussar -> (Hussar) hussar)
                .orElseThrow(() -> new IllegalStateException("Expected Hussar instance to be present"));
    }

    private Store getHussarStore(ExtensionContext context) {
        return context
                .getRoot()
                .getStore(HUSSAR_NAMESPACE);
    }
}
