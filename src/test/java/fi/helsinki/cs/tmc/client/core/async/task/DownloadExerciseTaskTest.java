package fi.helsinki.cs.tmc.client.core.async.task;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.Settings;
import fi.helsinki.cs.tmc.client.core.domain.Course;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.domain.Zip;
import fi.helsinki.cs.tmc.client.core.stub.StubSettings;
import fi.helsinki.cs.tmc.client.core.testutil.MockTMCServer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.mockito.Mockito.mock;

public class DownloadExerciseTaskTest {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(8089);

    private static final MockTMCServer SERVER = new MockTMCServer();

    private DownloadExerciseTask task;

    private TaskListener listener;
    private Settings settings;
    private Exercise exercise;
    private Course course;

    private ExecutorService executor;

    @BeforeClass
    public static void setUpClass() {

        SERVER.initialiseServer();
    }

    @Before
    public void setUp() {

        course = new Course("c1");

        exercise = new Exercise("e1", "c1");
        exercise.setId(1);
        exercise.setCourse(course);
        exercise.setDownloadUrl("http://localhost:8089/exercises/1.zip");

        settings = new StubSettings("http://localhost:8089/", "7", "Core", "1", course, "password", "username", null);

        listener = mock(TaskListener.class);

        executor = Executors.newSingleThreadExecutor();

        task = new DownloadExerciseTask(listener, settings, exercise);
    }

    @Test
    public void canMakeSuccessfulRequest() throws InterruptedException, ExecutionException {

        task = new DownloadExerciseTask(new TaskListener() {

            @Override
            public void onSuccess(final TaskResult<? extends Object> result) {
                final Zip resultZip = (Zip) result.result();
                org.junit.Assert.assertArrayEquals(MockTMCServer.EXERCISE_ZIP_CONTENT, resultZip.getBytes());
            }

            @Override
            public void onStart() { }

            @Override
            public void onInterrupt(final TaskResult<? extends Object> result) {

                org.junit.Assert.fail("Request should succeed");
            }

            @Override
            public void onFailure(final TaskResult<? extends Object> result) {

                org.junit.Assert.fail("Request should succeed");
            }

            @Override
            public void onEnd(final TaskResult<? extends Object> result) { }
        }, settings, exercise);

        final Future<?> job = executor.submit(task);
        job.get();

        verify(getRequestedFor(urlMatching(MockTMCServer.EXERCISE_ZIP_URL)));
    }

    @Test
    public void abortsOnInvalidDownloadUrl() throws InterruptedException, ExecutionException {

        exercise.setDownloadUrl("invalid url lol");

        task = new DownloadExerciseTask(new TaskListener() {

            @Override
            public void onStart() { }

            @Override
            public void onEnd(final TaskResult<? extends Object> result) { }

            @Override
            public void onSuccess(final TaskResult<? extends Object> result) {

                org.junit.Assert.fail("Request should fail");
            }

            @Override
            public void onFailure(final TaskResult<? extends Object> result) { }

            @Override
            public void onInterrupt(final TaskResult<? extends Object> result) {

                org.junit.Assert.fail("Request should not be interrupted");
            }

        }, settings, exercise);

        final Future<?> job = executor.submit(task);
        job.get();

        verify(getRequestedFor(urlMatching(MockTMCServer.EXERCISE_ZIP_URL)));
    }

    @Test
    public void abortsOnFailedDownload() throws InterruptedException, ExecutionException {

        stubFor(get(urlMatching(MockTMCServer.ANY))
                .atPriority(1)
                .willReturn(aResponse()
                            .withStatus(500)));

        task = new DownloadExerciseTask(new TaskListener() {

            @Override
            public void onStart() { }

            @Override
            public void onEnd(final TaskResult<? extends Object> result) { }

            @Override
            public void onSuccess(final TaskResult<? extends Object> result) {

                org.junit.Assert.fail("Request should fail");
            }

            @Override
            public void onFailure(final TaskResult<? extends Object> result) { }

            @Override
            public void onInterrupt(final TaskResult<? extends Object> result) {

                org.junit.Assert.fail("Request should not be interrupted");
            }

        }, settings, exercise);

        final Future<?> job = executor.submit(task);
        job.get();

        verify(getRequestedFor(urlMatching(MockTMCServer.EXERCISE_ZIP_URL)));
    }

}
