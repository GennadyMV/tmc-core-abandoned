package fi.helsinki.cs.tmc.client.core.async.task;

import fi.helsinki.cs.tmc.client.core.async.Task;
import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskProgressListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AbstractTaskTest {

    private class InterruptInstantlyTask extends AbstractTask<String> {

        public InterruptInstantlyTask(final TaskListener listener) {

            super("interruptInstantlyTask", listener);
        }

        @Override
        protected String work() throws InterruptedException, TaskFailureException {

            throw new InterruptedException();
        }

        @Override
        protected void cleanUp() {
            setProgress(1, 1);
        }
    }

    private class FailInstantlyTask extends AbstractTask<String> {

        public FailInstantlyTask(final TaskListener listener) {

            super("failInstantlyTask", listener);
        }

        @Override
        protected String work() throws InterruptedException, TaskFailureException {

            throw new TaskFailureException();
        }

        @Override
        protected void cleanUp() {
            setProgress(1, 1);
        }
    }

    private class FinishInstantlyTask extends AbstractTask<String> {

        public FinishInstantlyTask(final TaskListener listener) {

            super("finishInstantlyTask", listener);
        }

        @Override
        protected String work() throws InterruptedException, TaskFailureException {

            return "success";
        }

        @Override
        protected void cleanUp() {
            setProgress(1, 1);
        }
    }

    private class NeverFinishTask extends AbstractTask<String> {

        public NeverFinishTask(final TaskListener listener) {

            super("neverFinishTask", listener);
        }

        @Override
        protected String work() throws InterruptedException, TaskFailureException {

            while (true) {
                checkForInterrupt();
            }
        }

        @Override
        protected void cleanUp() {
            setProgress(1, 1);
        }
    }

    private TaskListener listener;
    private TaskProgressListener progressListener;

    @Before
    public void setUp() {

        listener = mock(TaskListener.class);
        progressListener = mock(TaskProgressListener.class);
    }

    @Test
    public void successCallsListenerCorrectly() {

        final Task task = new FinishInstantlyTask(listener);
        task.addProgressListener(progressListener);

        task.run();

        //Assert cleanUp() not called. Only increment is in cleanUp()
        verify(progressListener, times(0)).onProgress(1, 1);

        assertEquals("finishInstantlyTask", task.getDescription());

        verify(progressListener).onStart();
        verify(listener).onSuccess(any(TaskResult.class));
        verify(progressListener).onEnd();
        verifyNoMoreInteractions(listener, progressListener);
    }

    @Test
    public void interruptCallsListenerCorrectly() {

        final Task task = new InterruptInstantlyTask(listener);
        task.addProgressListener(progressListener);

        task.run();

        assertEquals("interruptInstantlyTask", task.getDescription());

        verify(progressListener).onStart();
        verify(listener).onInterrupt(any(TaskResult.class));

        // cleanup() called
        verify(progressListener).onProgress(1, 1);

        verify(progressListener).onEnd();
        verifyNoMoreInteractions(listener, progressListener);
    }

    @Test
    public void failureCallsListenerCorrectly() {

        final Task task = new FailInstantlyTask(listener);
        task.addProgressListener(progressListener);

        task.run();

        assertEquals("failInstantlyTask", task.getDescription());

        verify(progressListener).onStart();
        verify(listener).onFailure(any(TaskResult.class));

        // cleanup() called
        verify(progressListener).onProgress(1, 1);

        verify(progressListener).onEnd();
        verifyNoMoreInteractions(listener, progressListener);
    }

    @Test
    public void taskCanBeInterruptedWhenSubmittedToExecutorService() throws InterruptedException {

        final Task task = new NeverFinishTask(listener);
        task.addProgressListener(progressListener);
        final Future<?> job = Executors.newFixedThreadPool(1).submit(task);

        Thread.sleep(500);
        job.cancel(true);
        Thread.sleep(500);

        assertEquals("neverFinishTask", task.getDescription());

        verify(progressListener).onStart();
        verify(listener).onInterrupt(any(TaskResult.class));
        verify(progressListener).onEnd();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void canSetMultipleProgressListenersAndRemoveThem() throws InterruptedException, ExecutionException {

        final Task task = new AbstractTask<Object>(null, mock(TaskListener.class)) {

            @Override
            protected Object work() throws InterruptedException, TaskFailureException {

                setProgress(1, 1);

                return null;
            }

            @Override
            protected void cleanUp() { }
        };

        final TaskProgressListener progressListener1 = mock(TaskProgressListener.class);
        final TaskProgressListener progressListener2 = mock(TaskProgressListener.class);
        final TaskProgressListener progressListener3 = mock(TaskProgressListener.class);

        task.addProgressListener(progressListener1);
        task.addProgressListener(progressListener2);
        task.addProgressListener(progressListener3);

        task.removeProgressListener(progressListener2);

        Executors.newSingleThreadExecutor().submit(task).get();

        verify(progressListener1, times(1)).onStart();
        verify(progressListener2, times(0)).onStart();
        verify(progressListener3, times(1)).onStart();
    }


}
