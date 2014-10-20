package fi.helsinki.cs.tmc.client.core.async.task;

import fi.helsinki.cs.tmc.client.core.async.Task;
import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskMonitor;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;
import fi.helsinki.cs.tmc.client.core.async.listener.AbstractTaskListener;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class AbstractTaskTest {

    private class InterruptInstantlyTask extends AbstractTask<String> {

        public InterruptInstantlyTask(final TaskListener listener) {

            super("interruptInstantlyTask", listener, new TaskMonitor(1));
        }

        @Override
        protected String work() throws InterruptedException, TaskFailureException {

            throw new InterruptedException();
        }

        @Override
        protected void cleanUp() {
            getMonitor().increment();
        }
    }

    private class FailInstantlyTask extends AbstractTask<String> {

        public FailInstantlyTask(final TaskListener listener) {

            super("failInstantlyTask", listener, new TaskMonitor(1));
        }

        @Override
        protected String work() throws InterruptedException, TaskFailureException {

            throw new TaskFailureException();
        }

        @Override
        protected void cleanUp() {
            getMonitor().increment();
        }
    }

    private class FinishInstantlyTask extends AbstractTask<String> {

        public FinishInstantlyTask(final TaskListener listener) {

            super("finishInstantlyTask", listener, new TaskMonitor(1));
        }

        @Override
        protected String work() throws InterruptedException, TaskFailureException {

            return "success";
        }

        @Override
        protected void cleanUp() {
            getMonitor().increment();
        }
    }

    private class NeverFinishTask extends AbstractTask<String> {

        public NeverFinishTask(final TaskListener listener) {

            super("neverFinishTask", listener, new TaskMonitor(1));
        }

        @Override
        protected String work() throws InterruptedException, TaskFailureException {

            while (true) {
                checkForInterrupt();
            }
        }

        @Override
        protected void cleanUp() {
            getMonitor().increment();
        }
    }

    private TaskListener listener;

    @Before
    public void setUp() {

        listener = mock(AbstractTaskListener.class);

    }

    @Test
    public void successCallsListenerCorrectly() {

        final Task task = new FinishInstantlyTask(listener);

        task.run();

        //Assert cleanUp() not called. Only increment is in cleanUp()
        assertEquals(0, task.getMonitor().progress());

        assertTrue(task.getMonitor().isStarted());
        assertEquals("finishInstantlyTask", task.getDescription());

        verify(listener).onStart();
        verify(listener).onSuccess(any(TaskResult.class));
        verify(listener).onEnd(any(TaskResult.class));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void interruptCallsListenerCorrectly() {

        final Task task = new InterruptInstantlyTask(listener);

        task.run();

        //Assert cleanUp() called. Only increment is in cleanUp()
        assertEquals(1, task.getMonitor().progress());

        assertTrue(task.getMonitor().isStarted());
        assertEquals("interruptInstantlyTask", task.getDescription());

        verify(listener).onStart();
        verify(listener).onInterrupt(any(TaskResult.class));
        verify(listener).onEnd(any(TaskResult.class));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void failureCallsListenerCorrectly() {

        final Task task = new FailInstantlyTask(listener);

        task.run();

        //Assert cleanUp() called. Only increment is in cleanUp()
        assertEquals(1, task.getMonitor().progress());

        assertTrue(task.getMonitor().isStarted());
        assertEquals("failInstantlyTask", task.getDescription());

        verify(listener).onStart();
        verify(listener).onFailure(any(TaskResult.class));
        verify(listener).onEnd(any(TaskResult.class));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void taskCanBeInterruptedWhenSubmittedToExecutorService() throws InterruptedException {

        final Task task = new NeverFinishTask(listener);
        final Future<?> job = Executors.newFixedThreadPool(1).submit(task);

        Thread.sleep(500);
        job.cancel(true);
        Thread.sleep(500);

        assertTrue(task.getMonitor().isStarted());
        assertEquals("neverFinishTask", task.getDescription());

        verify(listener).onStart();
        verify(listener).onInterrupt(any(TaskResult.class));
        verify(listener).onEnd(any(TaskResult.class));
        verifyNoMoreInteractions(listener);
    }

}
