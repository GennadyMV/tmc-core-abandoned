package fi.helsinki.cs.tmc.client.core.testrunner.maven;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;
import fi.helsinki.cs.tmc.client.core.async.task.AbstractTask;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestRunResult;

public abstract class AbstractTestrunnerTask extends AbstractTask<TestRunResult> {

    public AbstractTestrunnerTask(final String description, final TaskListener listener) {

        super(description, listener);
    }

    @Override
    public TestRunResult work() throws TaskFailureException, InterruptedException {

        setProgress(0, 4);

        compile();

        checkForInterrupt();
        setProgress(1, 4);

        test();

        checkForInterrupt();
        setProgress(2, 4);

        final TestRunResult result = parseResult();

        checkForInterrupt();
        setProgress(3, 4);

        runValidator(result);

        setProgress(4, 4);

        return result;
    }

    protected abstract void compile() throws TaskFailureException, InterruptedException;

    protected abstract void test() throws TaskFailureException, InterruptedException;

    protected abstract TestRunResult parseResult() throws TaskFailureException, InterruptedException;

    protected abstract TestRunResult runValidator(TestRunResult result) throws TaskFailureException, InterruptedException;
}
