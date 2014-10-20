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

}
