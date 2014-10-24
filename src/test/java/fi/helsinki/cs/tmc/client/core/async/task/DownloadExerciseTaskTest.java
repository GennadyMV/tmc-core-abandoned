package fi.helsinki.cs.tmc.client.core.async.task;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.Settings;
import fi.helsinki.cs.tmc.client.core.domain.Course;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.io.unzip.Unzipper;
import fi.helsinki.cs.tmc.client.core.stub.StubSettings;
import fi.helsinki.cs.tmc.client.core.testutil.MockTMCServer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DownloadExerciseTaskTest {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(8089);

    private static final MockTMCServer SERVER = new MockTMCServer();

    @Rule
    public final TemporaryFolder projectsRoot = new TemporaryFolder();

    private DownloadExerciseTask task;

    private TaskListener listener;
    private Settings settings;
    private Exercise exercise;
    private Course course;
    private Unzipper unzipper;

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

        settings = new StubSettings("http://localhost:8089/", "7", "Core", "1", course, "password", "username", projectsRoot.getRoot());

        listener = mock(TaskListener.class);

        unzipper = new Unzipper();

        executor = Executors.newSingleThreadExecutor();

        task = new DownloadExerciseTask(listener, settings, unzipper, exercise);
    }

    @Test
    public void succesfullyDownloadsZipAndExtractsContents() throws InterruptedException, ExecutionException {

        System.out.println("!!!!!");

        task = new DownloadExerciseTask(new TaskListener() {

            @Override
            public void onStart() { }

            @Override
            public void onEnd(final TaskResult<? extends Object> result) { }

            @Override
            public void onSuccess(final TaskResult<? extends Object> result) {

                final File root = (File) result.result();

                org.junit.Assert.assertTrue(root.getPath().endsWith("e1"));
                org.junit.Assert.assertTrue(root.getParent().endsWith("c1"));
                org.junit.Assert.assertEquals(8, root.listFiles().length);
            }

            @Override
            public void onFailure(final TaskResult<? extends Object> result) {
                org.junit.Assert.fail("task should not fail.");
            }

            @Override
            public void onInterrupt(final TaskResult<? extends Object> result) {
                org.junit.Assert.fail("task should not be interrupted.");
            }

        }, settings, unzipper, exercise);

        executor.submit(task).get();

        assertProjectRootContainsFile("/c1/e1/test/OhjelmaTest.java");
        assertProjectRootContainsFile("/c1/e1/src/Ohjelma.java");
        assertProjectRootContainsFile("/c1/e1/nbproject/project.xml");
        assertProjectRootContainsFile("/c1/e1/lib/testrunner/tmc-junit-runner.jar");
        assertProjectRootContainsFile("/c1/e1/lib/junit-4.10.jar");
        assertProjectRootContainsFile("/c1/e1/build.xml");
        assertProjectRootContainsFile("/c1/e1/.tmcproject.json");

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

        }, settings, unzipper, exercise);

        executor.submit(task).get();
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

        }, settings, unzipper, exercise);

        executor.submit(task).get();

        verify(getRequestedFor(urlMatching(MockTMCServer.EXERCISE_ZIP_URL)));

        // Reset server to get rid of the temporary stub created at the start of this test
        WireMock.reset();
        SERVER.initialiseServer();
    }

    @Test
    public void abortsOnExtractionFailure() throws InterruptedException, ExecutionException, IOException {

        unzipper = mock(Unzipper.class);
        when(unzipper.unzipProject(any(byte[].class), any(File.class), eq(true))).thenThrow(new IOException());

        task = new DownloadExerciseTask(new TaskListener() {

            @Override
            public void onStart() { }

            @Override
            public void onEnd(final TaskResult<? extends Object> result) { }

            @Override
            public void onSuccess(final TaskResult<? extends Object> result) {

                org.junit.Assert.fail("task should fail.");
            }

            @Override
            public void onFailure(final TaskResult<? extends Object> result) {

            }

            @Override
            public void onInterrupt(final TaskResult<? extends Object> result) {

                org.junit.Assert.fail("task should not be interrupted.");
            }

        }, settings, unzipper, exercise);

        executor.submit(task).get();
    }

    private void assertProjectRootContainsFile(final String path) {
        assertTrue(contains(projectsRoot.getRoot(), path));
    }

    private boolean contains(final File f, final String p) {

        for (File sf : f.listFiles()) {
            if (sf.isDirectory()) {
                if (contains(sf, p)) {
                    return true;
                }
            } else {
                if (sf.getAbsolutePath().endsWith(p)) {
                    return true;
                }
            }
        }
        return false;
    }



}
