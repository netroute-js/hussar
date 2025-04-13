package pl.netroute.hussar.core.test;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.application.api.HussarApplication;
import pl.netroute.hussar.core.application.api.HussarApplicationRestart;
import pl.netroute.hussar.core.environment.api.HussarEnvironment;
import pl.netroute.hussar.core.service.api.HussarService;
import pl.netroute.hussar.core.test.stub.ApplicationStub;
import pl.netroute.hussar.core.test.stub.ServiceStubA;
import pl.netroute.hussar.core.test.stub.ServiceStubB;

@HussarEnvironment(configurerProvider = TestEnvironmentConfigurerProvider.class)
public class HussarAwareTest {
    public static final String TEST_METHOD_NAME = "test";
    public static final String TEST_RESTART_METHOD_NAME = "testRestart";

    @HussarApplication
    public ApplicationStub application;

    @HussarService
    public ServiceStubA serviceA;

    @HussarService
    public ServiceStubB serviceB;

    @Test
    public void test() {
    }

    @Test
    @HussarApplicationRestart
    public void testRestart() {
    }

}
