package fi.helsinki.cs.tmc.client.core.testrunner;

import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.UIInvoker;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestCaseResult;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestRunResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class TestrunnerListenerTest {

    private TestrunnerListener listener;
    private UIInvoker invoker;
    private TaskResult<TestRunResult> taskResult;

    @Before
    public void setUp() throws Exception {

        invoker = mock(UIInvoker.class);
        listener = new TestrunnerListener(invoker);

        taskResult = new TaskResult<>(buildTestRunResult(true, true, false));

    }

    private TestRunResult buildTestRunResult(final boolean... results) {

        final List<TestCaseResult> caseResults = new ArrayList<>();

        for (boolean success : results) {
            caseResults.add(new TestCaseResult("resultName", success, "resultMessage"));
        }

        return new TestRunResult(caseResults);
    }

    @Test
    public void onSuccessClosesTestsRunningWindow() {

        listener.onSuccess(taskResult);
        verify(invoker).closeTestsRunningWindow();
    }

    @Test
    public void onFailureClosesTestsRunningWindow() {

        listener.onFailure(taskResult);
        verify(invoker).closeTestsRunningWindow();
    }

    @Test
    public void onInterruptClosesTestsRunningWindow() {

        listener.onInterrupt(taskResult);
        verify(invoker).closeTestsRunningWindow();
    }

    @Test
    public void onSuccessOpensSomeTestsFailedWindowIfAnyTestWasUnsuccessfull() {

        listener.onSuccess(taskResult);
        verify(invoker).invokerSomeTestsFailedLocallyWindow();
    }

    @Test
    public void onSuccessOpensSubmitToServerWindowIfAnyTestWasUnsuccessfull() {

        taskResult = new TaskResult<>(buildTestRunResult(true, true));
        listener.onSuccess(taskResult);
        verify(invoker).invokeSubmitToServerWindow();
    }
}
