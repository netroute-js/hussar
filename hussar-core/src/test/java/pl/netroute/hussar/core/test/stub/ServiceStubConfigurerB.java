package pl.netroute.hussar.core.test.stub;

import lombok.experimental.SuperBuilder;

import static org.mockito.Mockito.spy;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class ServiceStubConfigurerB extends BaseServiceConfigurerStub<ServiceStubB> {

    public static ServiceStubConfigurerB defaultStub() {
        var serviceConfigurer = ServiceStubConfigurerB
                .newInstance()
                .service(ServiceStubB.defaultStub())
                .done();

        return spy(serviceConfigurer);
    }

}
