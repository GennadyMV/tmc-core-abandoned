package fi.helsinki.cs.tmc.core.async.listener;

import fi.helsinki.cs.tmc.core.async.TaskResult;
import fi.helsinki.cs.tmc.core.domain.TestCaseResult;
import fi.helsinki.cs.tmc.core.domain.TestRunResult;
import fi.helsinki.cs.tmc.core.ui.UIInvoker;

import java.util.List;

public class TestrunnerListener extends AbstractTaskListener {

    public TestrunnerListener(final UIInvoker uiInvoker) {
        
        super(uiInvoker);
    }

    @Override
    public void onStart() { 
        
        getUIInvoker().invokeTestsRunningWindow();
    }

    @Override
    public void onSuccess(final TaskResult<? extends Object> result) {

        final TestRunResult testRunResult = (TestRunResult) result.result();

        getUIInvoker().invokeTestResultWindow(testRunResult.getTestCaseResults());

        if (allPassed(testRunResult.getTestCaseResults())) {
            getUIInvoker().invokeSubmitToServerWindow();
        }
    }

    @Override
    public void onFailure(final TaskResult<? extends Object> result) { }


    @Override
    public void onInterrupt(final TaskResult<? extends Object> result) { }


    @Override
    public void onEnd(final TaskResult<? extends Object> result) { 
        
        getUIInvoker().closeTestsRunningWindow();
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
