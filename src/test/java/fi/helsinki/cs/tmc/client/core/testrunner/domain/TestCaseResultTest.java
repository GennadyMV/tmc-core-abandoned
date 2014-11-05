package fi.helsinki.cs.tmc.client.core.testrunner.domain;

import fi.helsinki.cs.tmc.testrunner.TestCase;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;


public class TestCaseResultTest {

    private TestCaseResult result;

    @Before
    public void setUp() throws Exception {

        this.result = new TestCaseResult("name", new String[]{"point1"}, true, "message");
    }

    @Test
    public void canConstruct() {

        assertEquals("name", result.getName());
        assertTrue(result.isSuccessful());
        assertEquals(1, result.getPointNames().length);
        assertEquals("point1", result.getPointNames()[0]);
        assertEquals("message", result.getMessage());
        assertNull(result.getException());
    }

    @Test
    public void canCreateFromTestCase() {

        final IOException exception = new IOException("exception message");
        final TestCase testCase = new TestCase("class", "method", new String[0]);
        testCase.testFailed(new Failure(null, exception));

        result = TestCaseResult.fromTestCaseRecord(testCase);

        assertEquals(exception.getMessage(), result.getException().message);
        assertFalse(result.isSuccessful());
    }

    @Test
    public void isSuccesfulIfNoExceptionInTestCase() {

        final TestCase testCase = new TestCase("class", "method", new String[0]);
        testCase.testStarted();
        testCase.testFinished();

        result = TestCaseResult.fromTestCaseRecord(testCase);

        assertTrue(result.isSuccessful());
    }
}
