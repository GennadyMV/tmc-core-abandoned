package fi.helsinki.cs.tmc.client.core.io.reader;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestCaseResult;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestRunResult;
import fi.helsinki.cs.tmc.client.core.testrunner.json.JacksonStackTraceElementModule;
import fi.helsinki.cs.tmc.testrunner.TestCase;
import fi.helsinki.cs.tmc.testrunner.TestCaseList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestResultFileReader {

    public TestRunResult parseTestResults(final File resultsFile) throws IOException {

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JacksonStackTraceElementModule());

        final TestCaseList testCaseRecords = mapper.readValue(resultsFile, TestCaseList.class);

        if (testCaseRecords == null) {
            throw new IllegalArgumentException("Empty results from test runner");
        }

        final List<TestCaseResult> testCaseResults = new ArrayList<>();
        for (final TestCase testCase : testCaseRecords) {
            testCaseResults.add(TestCaseResult.fromTestCaseRecord(testCase));
        }

        return new TestRunResult(testCaseResults);
    }
}
