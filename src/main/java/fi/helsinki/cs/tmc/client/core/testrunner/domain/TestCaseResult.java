package fi.helsinki.cs.tmc.client.core.testrunner.domain;

import fi.helsinki.cs.tmc.testrunner.CaughtException;
import fi.helsinki.cs.tmc.testrunner.TestCase;

import static fi.helsinki.cs.tmc.testrunner.TestCase.Status.PASSED;

/**
 * Class that stores the result of a single test case.
 */
public class TestCaseResult {

    private String name;
    private String[] pointNames;
    private boolean successful;
    private String message;
    private CaughtException exception;

    public TestCaseResult() { }

    public TestCaseResult(final String name, final String[] pointNames, final boolean successful, final String message) {

        this.name = name;
        this.pointNames = pointNames;
        this.successful = successful;
        this.message = message;
    }

    public String getName() {

        return name;
    }

    public String[] getPointNames() {

        return pointNames;
    }

    public boolean isSuccessful() {

        return successful;
    }

    public String getMessage() {

        return message;
    }

    public CaughtException getException() {

        return exception;
    }

    /**
     * Creates a TestCaseResult from a TestCase probably returned by a local run
     * of tmc-junit-runner.
     */
    public static TestCaseResult fromTestCaseRecord(final TestCase testCase) {

        final TestCaseResult testCaseResult = new TestCaseResult();

        testCaseResult.name = testCase.className + " " + testCase.methodName;
        testCaseResult.pointNames = testCase.pointNames;
        testCaseResult.successful = testCase.status == PASSED;
        testCaseResult.message = testCase.message;
        testCaseResult.exception = testCase.exception;

        return testCaseResult;
    }
}
