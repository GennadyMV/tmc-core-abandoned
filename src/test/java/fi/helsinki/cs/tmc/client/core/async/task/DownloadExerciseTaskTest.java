package fi.helsinki.cs.tmc.client.core.async.task;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.domain.Course;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.domain.Project;
import fi.helsinki.cs.tmc.client.core.domain.ProjectStatus;
import fi.helsinki.cs.tmc.client.core.domain.Settings;
import fi.helsinki.cs.tmc.client.core.io.reader.TmcProjectFileReader;
import fi.helsinki.cs.tmc.client.core.io.unzip.Unzipper;
import fi.helsinki.cs.tmc.client.core.testutil.MockTMCServer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
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
    private TmcProjectFileReader projectFileReader;

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

        settings = new Settings("http://localhost:8089/", "7", "Core", "1", course, "password", "username", projectsRoot.getRoot(), Locale.ENGLISH);

        listener = mock(TaskListener.class);

        unzipper = new Unzipper();
        projectFileReader = new TmcProjectFileReader();

        executor = Executors.newSingleThreadExecutor();

        task = new DownloadExerciseTask(listener, settings, unzipper, projectFileReader, exercise);
    }

    @Test
    public void succesfullyDownloadsZipAndExtractsContents() throws InterruptedException, ExecutionException {

        task = new DownloadExerciseTask(new TaskListener() {

            @Override
            public void onSuccess(final TaskResult<?> result) {

                final File root = (File) result.result();

                org.junit.Assert.assertTrue(root.getPath().endsWith("e1"));
                org.junit.Assert.assertTrue(root.getParent().endsWith("c1"));
                org.junit.Assert.assertEquals(8, root.listFiles().length);
            }

            @Override
            public void onFailure(final TaskResult<?> result) {
                org.junit.Assert.fail("task should not fail.");
            }

            @Override
            public void onInterrupt(final TaskResult<?> result) {
                org.junit.Assert.fail("task should not be interrupted.");
            }

        }, settings, unzipper, projectFileReader, exercise);

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
            public void onSuccess(final TaskResult<?> result) {

                org.junit.Assert.fail("Request should fail");
            }

            @Override
            public void onFailure(final TaskResult<?> result) { }

            @Override
            public void onInterrupt(final TaskResult<?> result) {

                org.junit.Assert.fail("Request should not be interrupted");
            }

        }, settings, unzipper, projectFileReader, exercise);

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
            public void onSuccess(final TaskResult<?> result) {

                org.junit.Assert.fail("Request should fail");
            }

            @Override
            public void onFailure(final TaskResult<?> result) { }

            @Override
            public void onInterrupt(final TaskResult<?> result) {

                org.junit.Assert.fail("Request should not be interrupted");
            }

        }, settings, unzipper, projectFileReader, exercise);

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
            public void onSuccess(final TaskResult<?> result) {

                org.junit.Assert.fail("task should fail.");
            }

            @Override
            public void onFailure(final TaskResult<?> result) {

            }

            @Override
            public void onInterrupt(final TaskResult<?> result) {

                org.junit.Assert.fail("task should not be interrupted.");
            }

        }, settings, unzipper, projectFileReader, exercise);

        executor.submit(task).get();
    }

    @Test
    public void successUpdatesExerciseAndCreatesNewProject() throws InterruptedException, ExecutionException {

        executor.submit(task).get();

        assertNotNull(exercise.getProject());

        final Project project = exercise.getProject();
        assertEquals(exercise, project.getExercise());

        assertEquals(ProjectStatus.DOWNLOADED, project.getStatus());

        assertTrue(project.getRootPath().contains(File.separator + "c1" + File.separator));
        assertTrue(project.getRootPath().contains(File.separator + "e1"));

        assertTrue(project.containsFile(project.getRootPath() + "/test/OhjelmaTest.java"));
        assertTrue(project.containsFile(project.getRootPath() + "/src/Ohjelma.java"));
        assertTrue(project.containsFile(project.getRootPath() + "/nbproject/project.xml"));
        assertTrue(project.containsFile(project.getRootPath() + "/lib/testrunner/tmc-junit-runner.jar"));
        assertTrue(project.containsFile(project.getRootPath() + "/lib/junit-4.10.jar"));
        assertTrue(project.containsFile(project.getRootPath() + "/build.xml"));
        assertTrue(project.containsFile(project.getRootPath() + "/.tmcproject.json"));
    }

    @Test
    public void taskIncludesAlreadyPresentFilesInProject() throws InterruptedException, ExecutionException, IOException {

        new File(projectsRoot.getRoot(), "c1/e1").mkdirs();
        new File(projectsRoot.getRoot(), "c1/e1/foo.bar").createNewFile();

        executor.submit(task).get();

        assertTrue(exercise.getProject().containsFile(exercise.getProject().getRootPath() + "/foo.bar"));
    }

    @Test
    public void successUpdatesExerciseAndProject() throws InterruptedException, ExecutionException, IOException {

        System.out.println("!!!!!!!!!!!!");

        final File folder = new File(projectsRoot.getRoot(), "c1/e1");
        folder.mkdirs();

        final File f1 = new File(projectsRoot.getRoot(), "c1/e1/build.xml");
        f1.createNewFile();
        System.out.println(f1.getAbsolutePath());

        final File f2 = new File(projectsRoot.getRoot(), "c1/e1/.tmcproject.json");
        f2.createNewFile();
        System.out.println(f2.getAbsolutePath());

        Project project = new Project(exercise, folder.getAbsolutePath(), Arrays.asList(f1.getAbsolutePath(), f2.getAbsolutePath()));
        exercise.setProject(project);

        executor.submit(task).get();

        project = exercise.getProject();
        assertEquals(exercise, project.getExercise());

        assertEquals(ProjectStatus.DOWNLOADED, project.getStatus());

        assertTrue(project.getRootPath().contains(File.separator + "c1" + File.separator));
        assertTrue(project.getRootPath().contains(File.separator + "e1"));

        assertTrue(project.containsFile(project.getRootPath() + "/test/OhjelmaTest.java"));
        assertTrue(project.containsFile(project.getRootPath() + "/src/Ohjelma.java"));
        assertTrue(project.containsFile(project.getRootPath() + "/nbproject/project.xml"));
        assertTrue(project.containsFile(project.getRootPath() + "/lib/testrunner/tmc-junit-runner.jar"));
        assertTrue(project.containsFile(project.getRootPath() + "/lib/junit-4.10.jar"));
        assertTrue(project.containsFile(project.getRootPath() + "/build.xml"));
        assertTrue(project.containsFile(project.getRootPath() + "/.tmcproject.json"));
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

    private void print(final File f) {
        System.out.println("D - " + f.getAbsolutePath());
        for (File sf : f.listFiles()) {
            if (sf.isDirectory()) {
                print(sf);
            } else {
                System.out.println("F - " + sf.getAbsolutePath());
            }
        }
    }

}
