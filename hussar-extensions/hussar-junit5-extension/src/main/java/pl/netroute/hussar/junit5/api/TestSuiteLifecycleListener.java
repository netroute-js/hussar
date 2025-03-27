package pl.netroute.hussar.junit5.api;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

public class TestSuiteLifecycleListener implements TestExecutionListener {

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        var hussarInstance = HussarJUnit5Extension.getHussarInstance();

        hussarInstance.shutdown();
    }

}
