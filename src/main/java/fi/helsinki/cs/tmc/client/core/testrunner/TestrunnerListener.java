package fi.helsinki.cs.tmc.client.core.testrunner;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.UIInvoker;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestCaseResult;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestRunResult;

import java.util.List;

public class TestrunnerListener implements TaskListener {

    private final UIInvoker uiInvoker;

    public TestrunnerListener(final UIInvoker uiInvoker) {

        this.uiInvoker = uiInvoker;
    }

    @Override
    public void onSuccess(final TaskResult<? extends Object> result) {

        uiInvoker.closeTestsRunningWindow();

        final TestRunResult testRunResult = (TestRunResult) result.result();

        uiInvoker.invokeTestResultWindow(testRunResult.getTestCaseResults());

        if (allPassed(testRunResult.getTestCaseResults())) {
            uiInvoker.invokeSubmitToServerWindow();
        } else {
            uiInvoker.invokerSomeTestsFailedLocallyWindow();
        }
    }

    @Override
    public void onFailure(final TaskResult<? extends Object> result) {

        uiInvoker.closeTestsRunningWindow();
    }

    @Override
    public void onInterrupt(final TaskResult<? extends Object> result) {

        uiInvoker.closeTestsRunningWindow();
    }

    private boolean allPassed(final List<TestCaseResult> testCaseResults) {

        for (final TestCaseResult result : testCaseResults) {
            if (!result.isSuccessful()) {
                return false;
            }
        }

        return true;
    }
}
