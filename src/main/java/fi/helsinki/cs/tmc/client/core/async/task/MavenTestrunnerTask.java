package fi.helsinki.cs.tmc.client.core.async.task;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskMonitor;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;
import fi.helsinki.cs.tmc.client.core.domain.Project;
import fi.helsinki.cs.tmc.client.core.domain.TestRunResult;
import fi.helsinki.cs.tmc.client.core.io.TestResultFileReader;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MavenTestrunnerTask  extends AbstractTask<TestRunResult> {

    public static final String DESCRIPTION = "Running tests";

    private static final Logger LOG = LogManager.getLogger();

    private static final int MAVEN_SUCCESS = 0;

    private final Project project;

    public MavenTestrunnerTask(final TaskListener listener, final Project project) {

        super(DESCRIPTION, listener, new TaskMonitor(1));

        this.project = project;
    }

    public abstract int runMavenGoal(String goal, Project project);

    @Override
    public TestRunResult work() throws TaskFailureException, InterruptedException {

        compile();

        test();

        final TestRunResult result = parseResult();

        return result;
    }

    private void compile() throws TaskFailureException, InterruptedException {

        final int result = runMavenGoal("test-compile", project);

        if (result != MAVEN_SUCCESS) {
            throw new TaskFailureException("Unable to compile project");
        }

        checkForInterrupt();

        getProgressMonitor().increment();
    }

    private void test() throws TaskFailureException, InterruptedException {

        final int result = runMavenGoal("fi.helsinki.cs.tmc:tmc-maven-plugin:1.6:test", project);

        if (result != MAVEN_SUCCESS) {
            throw new TaskFailureException("Unable to run TMC tests");
        }

        checkForInterrupt();

        getProgressMonitor().increment();
    }

    private TestRunResult parseResult() throws TaskFailureException, InterruptedException {

        final File resultFile = new File(project.getRootPath() + "/target/test_output.txt");
        final TestRunResult results;

        try {

            results = new TestResultFileReader().parseTestResults(resultFile);
            resultFile.delete();

        } catch (final IOException exception) {

            LOG.error("Unable to parse test file", exception);
            throw new TaskFailureException("Unable to parse test file.");
        }

        checkForInterrupt();

        getProgressMonitor().increment();

        return results;

    }
}
