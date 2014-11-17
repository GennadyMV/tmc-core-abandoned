package fi.helsinki.cs.tmc.client.core.testrunner;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.UIInvoker;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestRunResult;

public class TestrunnerListener implements TaskListener {

    private final UIInvoker uiInvoker;

    public TestrunnerListener(final UIInvoker uiInvoker) {

        this.uiInvoker = uiInvoker;
    }

    @Override
    public void onSuccess(final TaskResult<?> result) {

        uiInvoker.closeTestsRunningWindow();

        final TestRunResult testRunResult = (TestRunResult) result.result();

        uiInvoker.invokeTestResultWindow(testRunResult);

        if (testRunResult.isSubmittable()) {
            uiInvoker.invokeSubmitToServerWindow();
        } else if (testRunResult.gotPartialPoints()) {
            uiInvoker.invokeSomePointsFromLocalTestsWindow(testRunResult);
        } else {
            uiInvoker.invokeNoPointsFromLocalTestsWindow();
        }
    }

    @Override
    public void onFailure(final TaskResult<?> result) {

        uiInvoker.closeTestsRunningWindow();
    }

    @Override
    public void onInterrupt(final TaskResult<?> result) {

        uiInvoker.closeTestsRunningWindow();
    }
}
