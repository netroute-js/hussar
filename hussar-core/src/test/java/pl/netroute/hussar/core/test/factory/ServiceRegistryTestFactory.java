package pl.netroute.hussar.core.test.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.core.service.api.DefaultServiceRegistry;
import pl.netroute.hussar.core.service.api.ServiceRegistry;
import pl.netroute.hussar.core.test.stub.ServiceStubA;
import pl.netroute.hussar.core.test.stub.ServiceStubB;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceRegistryTestFactory {

    public static ServiceRegistry create() {
        var serviceA = ServiceStubA.defaultStub();
        var serviceB = ServiceStubB.defaultStub();

        return DefaultServiceRegistry.of(serviceA, serviceB);
    }

}
