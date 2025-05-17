package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.helper.ReflectionHelper;
import pl.netroute.hussar.core.network.DefaultNetworkRestore;
import pl.netroute.hussar.core.network.api.NetworkRestore;
import pl.netroute.hussar.core.network.api.HussarNetworkRestore;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceRegistry;

import java.lang.reflect.Field;

@Slf4j
@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class NetworkRestoreInjector {
    private static final Class<HussarNetworkRestore> HUSSAR_NETWORK_RESTORE_ANNOTATION_CLASS = HussarNetworkRestore.class;

    @NonNull
    private final ServiceRegistry serviceRegistry;

    void inject(@NonNull Object targetInstance) {
        var annotatedFields = ReflectionHelper.getFieldsAnnotatedWith(targetInstance, HUSSAR_NETWORK_RESTORE_ANNOTATION_CLASS);

        if(!CollectionHelper.isEmpty(annotatedFields)) {
            var networkRestore = createNetworkRestore();

            annotatedFields.forEach(networkRestoreField -> injectGlobalNetworkRestore(targetInstance, networkRestoreField, networkRestore));
        }
    }

    private void injectGlobalNetworkRestore(Object targetInstance, Field networkRestoreField, NetworkRestore networkRestore) {
        log.info("Injecting {} into {}", networkRestore.getClass().getSimpleName(), targetInstance.getClass().getSimpleName());

        ReflectionHelper.setValue(targetInstance, networkRestoreField, networkRestore);
    }

    private NetworkRestore createNetworkRestore() {
        var networkControls = serviceRegistry
                .getEntries()
                .stream()
                .map(Service::getNetworkControl)
                .toList();

        return new DefaultNetworkRestore(networkControls);
    }

    static NetworkRestoreInjector newInstance(@NonNull Environment environment) {
        var serviceRegistry = environment.getServiceRegistry();

        return new NetworkRestoreInjector(serviceRegistry);
    }
}
