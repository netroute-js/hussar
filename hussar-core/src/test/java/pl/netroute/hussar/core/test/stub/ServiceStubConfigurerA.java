package pl.netroute.hussar.core.test.stub;

import lombok.experimental.SuperBuilder;

import static org.mockito.Mockito.spy;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class ServiceStubConfigurerA extends BaseServiceConfigurerStub<ServiceStubA> {

    public static ServiceStubConfigurerA defaultStub() {
        var serviceConfigurer = ServiceStubConfigurerA
                .newInstance()
                .service(ServiceStubA.defaultStub())
                .done();

        return spy(serviceConfigurer);
    }

}
