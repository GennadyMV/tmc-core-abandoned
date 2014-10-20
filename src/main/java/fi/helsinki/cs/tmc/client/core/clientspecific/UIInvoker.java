package fi.helsinki.cs.tmc.client.core.clientspecific;

import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestCaseResult;

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


}

