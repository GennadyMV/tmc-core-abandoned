package fi.helsinki.cs.tmc.client.core.async.listener;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.UIInvoker;
import fi.helsinki.cs.tmc.client.core.domain.SubmissionResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubmitExerciseTaskListener implements TaskListener {

    private static final Logger LOG = LogManager.getLogger();
    
    private final UIInvoker uiInvoker;

    public SubmitExerciseTaskListener(final UIInvoker uiInvoker) {

        this.uiInvoker = uiInvoker;
    }

    @Override
    public void onSuccess(final TaskResult<?> result) {

        if (result == null || !(result.result() instanceof SubmissionResult)) {
            LOG.error("Null or wrong type of result when calling SubmitExerciseTaskListener");
            uiInvoker.userVisibleException("Something went wrong");
            
            return;
        }

        uiInvoker.closeSubmittingExerciseWindow();

        final SubmissionResult submissionResult = (SubmissionResult) result.result();

        if (submissionResult.allTestCasesSucceeded()) {
            uiInvoker.invokeAllTestsPassedOnServerWindow();
        } else {
            uiInvoker.invokeSomeTestsFailedOnServerWindow();
        }

        uiInvoker.invokeTestResultWindow(submissionResult.asTestRunResult());
    }

    @Override
    public void onFailure(final TaskResult<?> result) {

        uiInvoker.closeSubmittingExerciseWindow();

        uiInvoker.userVisibleException("Server communication error.");
    }

    @Override
    public void onInterrupt(final TaskResult<?> result) {

        uiInvoker.closeSubmittingExerciseWindow();
    }
}
