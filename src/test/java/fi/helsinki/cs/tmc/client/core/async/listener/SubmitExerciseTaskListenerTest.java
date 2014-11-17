package fi.helsinki.cs.tmc.client.core.async.listener;

import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.UIInvoker;
import fi.helsinki.cs.tmc.client.core.domain.SubmissionResult;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestCaseResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class SubmitExerciseTaskListenerTest {

    private UIInvoker invoker;
    private SubmitExerciseTaskListener listener;
    private SubmissionResult submissionResult;
    private TaskResult<SubmissionResult> result;

    @Before
    public void setUp() {

        invoker = mock(UIInvoker.class);
        listener = new SubmitExerciseTaskListener(invoker);
        submissionResult = new SubmissionResult();
        result = new TaskResult<>(submissionResult);
    }

    @Test
    public void onFailureClosesSubmittingExerciseWindow() {

        listener.onFailure(result);

        verify(invoker).closeSubmittingExerciseWindow();
        verify(invoker).userVisibleException(any(String.class));
        verifyNoMoreInteractions(invoker);
    }

    @Test
    public void onInterruptClosesSubmittingExerciseWindow() {

        listener.onInterrupt(result);

        verify(invoker).closeSubmittingExerciseWindow();
        verifyNoMoreInteractions(invoker);
    }

    @Test
    public void onSuccessClosesSubmittingExerciseWindow() {

        listener.onSuccess(result);

        verify(invoker).closeSubmittingExerciseWindow();
    }

    @Test
    public void onSuccessInvokesTestResultsWindow() {

        final List<TestCaseResult> testCases = new ArrayList<>();
        testCases.add(new TestCaseResult("", new String[]{""}, true, ""));

        submissionResult.setTestCases(testCases);

        listener.onSuccess(result);

        verify(invoker).invokeTestResultWindow(submissionResult.asTestRunResult());
    }

    @Test
    public void onSuccessInvokesAllTestsPassedOnServerWindowIfAllTestsPassed() {

        final List<TestCaseResult> testCases = new ArrayList<>();
        testCases.add(new TestCaseResult("", new String[]{""}, true, ""));

        submissionResult.setTestCases(testCases);

        listener.onSuccess(result);

        verify(invoker).invokeAllTestsPassedOnServerWindow();
    }

    @Test
    public void onSuccessInvokesSomeTestsFailedOnServerWindowIfSomeTestsFailed() {

        final List<TestCaseResult> testCases = new ArrayList<>();
        testCases.add(new TestCaseResult("", new String[]{""}, true, ""));
        testCases.add(new TestCaseResult("", new String[]{""}, false, ""));

        submissionResult.setTestCases(testCases);

        listener.onSuccess(result);

        verify(invoker).invokeSomeTestsFailedOnServerWindow();
    }
}
