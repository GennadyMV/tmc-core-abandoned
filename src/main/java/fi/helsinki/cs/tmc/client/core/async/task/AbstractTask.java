package fi.helsinki.cs.tmc.client.core.async.task;

import fi.helsinki.cs.tmc.client.core.async.Task;
import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskProgressListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractTask<S> implements Task<S> {

    private static final Logger LOG = LogManager.getLogger();

    private String description;
    private TaskListener listener;
    private List<TaskProgressListener> progressListeners;

    public AbstractTask(final String description, final TaskListener listener) {

        this.description = description;
        this.listener = listener;
        this.progressListeners = new ArrayList<>();
    }

    @Override
    public void addProgressListener(final TaskProgressListener progressListener) {

        progressListeners.add(progressListener);
    }

    @Override
    public boolean removeProgressListener(final TaskProgressListener progressListener) {

        return progressListeners.remove(progressListener);
    }

    @Override
    public void run() {

        for (TaskProgressListener progressListener : progressListeners) {
            progressListener.onStart();
        }

        TaskResult<S> result;
        try {

            result = new TaskResult<S>(TaskResult.Status.SUCCESS, work());

            listener.onSuccess(result);

        } catch (InterruptedException exception) {

            LOG.info("Backround task " + description + " was interrupted.", exception);

            result = new TaskResult<S>(TaskResult.Status.INTERRUPTED, null);

            cleanUp();

            listener.onInterrupt(result);

        } catch (TaskFailureException exception) {

            LOG.error("Background task" + description + " failed.", exception);

            result = new TaskResult<S>(TaskResult.Status.FAILURE, null, exception);

            cleanUp();

            listener.onFailure(result);

        }

        for (TaskProgressListener progressListener : progressListeners) {
            progressListener.onEnd();
        }
    }

    @Override
    public String getDescription() {

        return description;
    }

    protected abstract S work() throws InterruptedException, TaskFailureException;

    protected abstract void cleanUp();

    protected void checkForInterrupt() throws InterruptedException {

        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Task interrupted from outside");
        }
    }

    protected void setProgress(final int current, final int estimatedTotal) {

        for (TaskProgressListener progressListener : progressListeners) {
            progressListener.onProgress(current, estimatedTotal);
        }
    }
}
