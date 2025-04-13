package pl.netroute.hussar.core.test.stub;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import static org.mockito.Mockito.spy;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class ServiceStubB extends BaseServiceStub {

    public static ServiceStubB defaultStub() {
        var service = ServiceStubB.newInstance().done();

        return spy(service);
    }

    public static ServiceStubB newStub(@NonNull String name) {
        var service = ServiceStubB
                .newInstance()
                .name(name)
                .done();

        return spy(service);
    }

}
