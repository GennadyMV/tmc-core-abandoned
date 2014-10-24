package fi.helsinki.cs.tmc.client.core.async.listener;

import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.async.TaskResult.Status;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;
import fi.helsinki.cs.tmc.client.core.clientspecific.UIInvoker;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class DownloadExerciseTaskListenerTest {

    private DownloadExerciseTaskListener listener;
    private UIInvoker uiInvoker;

    @Before
    public void setUp() {

        uiInvoker = mock(UIInvoker.class);
        listener = new DownloadExerciseTaskListener(uiInvoker);
    }

    @Test
    public void canCallOnStart() {

        listener.onStart();
    }

    @Test
    public void canCallOnEnd() {

        listener.onEnd(null);
    }

    @Test
    public void canCallOnInterrupt() {

        listener.onInterrupt(null);
    }

    @Test
    public void onFailureInvokesForUserVisibleError() {

        final String message = "thing failed because of another thing. sorry.";
        final TaskResult<File> result = new TaskResult<>(Status.FAILURE, null, new TaskFailureException(message));

        listener.onFailure(result);

        verify(uiInvoker).userVisibleException(contains(message));
    }

    @Test
    public void onSuccessTellsInvokerToOpenProject() {

        final File file = new File("nosuchfile");
        final TaskResult<File> result = new TaskResult<>(file);

        listener.onSuccess(result);

        verify(uiInvoker).openProject(file);
    }
}
