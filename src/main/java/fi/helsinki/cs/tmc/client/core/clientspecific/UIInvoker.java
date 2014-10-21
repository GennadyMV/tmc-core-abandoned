package fi.helsinki.cs.tmc.client.core.clientspecific;

import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestCaseResult;

import java.io.File;
import java.util.List;

public interface UIInvoker {

    /* Code review */
    void invokeCodeReviewRequestCreationSuccessWindow();
    void invokerCodeReviewRequestCreationFailureWindow();


    /* Testrunner */
    void invokeTestResultWindow(List<TestCaseResult> testCaseResults);
    void invokeTestsRunningWindow();
    void closeTestsRunningWindow();
    void invokeSubmitToServerWindow();
    void invokerSomeTestsFailedLocallyWindow();


    /* IDE Interactions */
    boolean openProject(File projectRoot);
}
