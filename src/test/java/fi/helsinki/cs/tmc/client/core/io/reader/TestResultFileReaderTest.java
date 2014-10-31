package fi.helsinki.cs.tmc.client.core.io.reader;

import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestCaseResult;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestRunResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestResultFileReaderTest {

    private TestResultFileReader reader;

    @Before
    public void setUp() {

        reader = new TestResultFileReader();
    }

    @Test
    public void canReadFileWithAllSuccesfull() throws IOException {

        final File file = getFileFromResources("testrun_results/all_passed.txt");
        final TestRunResult result = reader.parseTestResults(file);

        assertEquals(11, result.getTestCaseResults().size());

        for (TestCaseResult testCaseResult : result.getTestCaseResults()) {
            assertTrue(testCaseResult.isSuccessful());
        }
    }


    @Test
    public void canReadFileWithFailedTests() throws IOException {

        final File file = getFileFromResources("testrun_results/some_failed.txt");
        final TestRunResult result = reader.parseTestResults(file);

        assertEquals(11, result.getTestCaseResults().size());

        int passed = 0;
        for (TestCaseResult testCaseResult : result.getTestCaseResults()) {
            if (testCaseResult.isSuccessful()) {
                passed++;
            }
        }

        assertEquals(10, passed);
    }

    @Test
    public void failedTestsContainMessages() throws IOException {

        final File file = getFileFromResources("testrun_results/some_failed.txt");
        final TestRunResult result = reader.parseTestResults(file);

        for (TestCaseResult testCaseResult : result.getTestCaseResults()) {
            if (!testCaseResult.isSuccessful()) {
                assertNotNull(testCaseResult.getMessage());
                assertFalse(testCaseResult.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void failedTestsContainExceptionsWithStacktraces() throws IOException {

        final File file = getFileFromResources("testrun_results/some_failed.txt");
        final TestRunResult result = reader.parseTestResults(file);

        for (TestCaseResult testCaseResult : result.getTestCaseResults()) {
            if (!testCaseResult.isSuccessful()) {
                assertNotNull(testCaseResult.getException());
                assertTrue(testCaseResult.getException().stackTrace.length != 0);
            }
        }
    }

    @Test
    public void canHandleResultFileWithNoResults() throws IOException {

        final File file = getFileFromResources("testrun_results/no_results.txt");
        final TestRunResult result = reader.parseTestResults(file);

        assertEquals(0, result.getTestCaseResults().size());
    }

    @Test(expected = FileNotFoundException.class)
    public void throwsOnMissingResultFile() throws IOException {

        final File folder = getFileFromResources("testrun_results");
        final File file = new File(folder, "no_such_file.txt");

        assertFalse("The path used for testing with nonexisting files contains an existing file! Path: " + file.getAbsolutePath(), file.exists());

        reader.parseTestResults(file);
    }

    @Test(expected = IOException.class)
    public void throwsOnCompletelyEmptyFile() throws IOException {

        final File file = getFileFromResources("testrun_results/empty_file.txt");
        reader.parseTestResults(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnDirectory() throws IOException {

        final File folder = getFileFromResources("testrun_results");
        reader.parseTestResults(folder);
    }

    private File getFileFromResources(final String path) {

        final String absolutePath = getClass().getClassLoader().getResource(path).getFile();

        return new File(absolutePath);
    }

}
