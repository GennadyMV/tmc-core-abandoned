package fi.helsinki.cs.tmc.client.core.clientspecific;

import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestRunResult;

import java.io.File;

public interface UIInvoker {

    /* Code review */
    void invokeCodeReviewRequestCreationSuccessWindow();
    void invokerCodeReviewRequestCreationFailureWindow();


    /* Testrunner */
    void invokeTestResultWindow(TestRunResult testRunResult);
    void invokeTestsRunningWindow();
    void closeTestsRunningWindow();
    void invokeSubmitToServerWindow();
    void invokeSomePointsFromLocalTestsWindow(TestRunResult testRunResult);
    void invokeNoPointsFromLocalTestsWindow();


    /*  */
    void invokeSubmittingExerciseWindow();
    void closeSubmittingExerciseWindow();
    void invokeAllTestsPassedOnServerWindow();
    void invokeSomeTestsFailedOnServerWindow();

    /* IDE Interactions */
    void userVisibleException(String string);
    boolean openProject(File projectRoot);

}
