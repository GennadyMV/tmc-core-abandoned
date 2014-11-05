package fi.helsinki.cs.tmc.client.core.testrunner.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestRunResultTest {

    @Test
    public void canConstruct() {

        final List<TestCaseResult> subresults = new ArrayList<>();
        final TestRunResult result = new TestRunResult(subresults);

        assertEquals(subresults, result.getTestCaseResults());
    }

    @Test
    public void allTestsPassedReturnsTrueIfAllTestsWereSuccesful() {

        final List<TestCaseResult> subresults = new ArrayList<>();
        subresults.add(new TestCaseResult("test1", new String[]{ "point1.1" }, true, ""));
        subresults.add(new TestCaseResult("test1", new String[]{ "point1.2" }, true, ""));

        final TestRunResult result = new TestRunResult(subresults);

        assertTrue(result.allTestsPassed());
    }

    @Test
    public void allTestsPassedReturnFalseIfAnyTestWasUnsuccesfull() {

        final List<TestCaseResult> subresults = new ArrayList<>();
        subresults.add(new TestCaseResult("test2", new String[]{ "point2.1" }, true, ""));
        subresults.add(new TestCaseResult("test2", new String[]{ "point2.2" }, false, ""));

        final TestRunResult result = new TestRunResult(subresults);

        assertFalse(result.allTestsPassed());
    }

    @Test
    public void getAwardedPointsReturnsPointsThatHadAllAssociatedTestsPassed() {

        final List<TestCaseResult> subresults = new ArrayList<>();
        subresults.add(new TestCaseResult("test3", new String[]{ "point3.1" }, true, ""));

        final TestRunResult result = new TestRunResult(subresults);

        assertTrue(result.getAwardedPoints().contains("point3.1"));
    }

    @Test
    public void getAwardedPointsDoesNotReturnPointsThatHadAllAssociatedTestsFail() {

        final List<TestCaseResult> subresults = new ArrayList<>();
        subresults.add(new TestCaseResult("test4", new String[]{ "point4.1" }, false, ""));

        final TestRunResult result = new TestRunResult(subresults);

        assertFalse(result.getAwardedPoints().contains("point4.1"));
    }

    @Test
    public void getAwardedPointsDoesNotReturnPointsThatHadAnyAssociatedTestsFail() {

        final List<TestCaseResult> subresults = new ArrayList<>();
        subresults.add(new TestCaseResult("test5", new String[]{ "point5.1" }, true, ""));
        subresults.add(new TestCaseResult("test5", new String[]{ "point5.1" }, false, ""));

        final TestRunResult result = new TestRunResult(subresults);

        assertFalse(result.getAwardedPoints().contains("point5.1"));
    }

    @Test
    public void getPointNamesReturnsAllPointNames() {
        final List<TestCaseResult> subresults = new ArrayList<>();
        subresults.add(new TestCaseResult("test6", new String[]{ "point6.1" }, true, ""));
        subresults.add(new TestCaseResult("test6", new String[]{ "point6.2" }, false, ""));

        final TestRunResult result = new TestRunResult(subresults);

        assertTrue(result.getPointNames().contains("point6.1"));
        assertTrue(result.getPointNames().contains("point6.2"));
    }
}
