package pl.netroute.hussar.core.test.stub;

import lombok.experimental.SuperBuilder;

import static org.mockito.Mockito.spy;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class ServiceStubA extends BaseServiceStub {

    public static ServiceStubA defaultStub() {
        var service = ServiceStubA.newInstance().done();

        return spy(service);
    }

    public static ServiceStubA newStub(String name) {
        var service = ServiceStubA
                .newInstance()
                .name(name)
                .done();

        return spy(service);
    }

}
