package fi.helsinki.cs.tmc.client.core.async.task;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.Settings;
import fi.helsinki.cs.tmc.client.core.domain.Course;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.domain.Zip;
import fi.helsinki.cs.tmc.client.core.io.unzip.Unzipper;
import fi.helsinki.cs.tmc.client.core.stub.StubSettings;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExtractZippedProjectTaskTest {

    @Rule
    public final TemporaryFolder projectsRoot = new TemporaryFolder();

    private ExtractZippedProjectTask task;

    private Settings settings;
    private Zip zip;
    private Course course;
    private Exercise exercise;
    private Unzipper unzipper;

    private ExecutorService executor;

    @Before
    public void setUp() throws IOException {

        zip = new Zip();
        zip.setBytes(IOUtils.toByteArray(getClass().getResourceAsStream("/valid_exercise.zip")));

        course = new Course("c1");
        exercise = new Exercise("e1", "c1");
        exercise.setCourse(course);

        unzipper = new Unzipper();

        settings = new StubSettings("", "", "", "", course, "", "", projectsRoot.getRoot());

        executor = Executors.newSingleThreadExecutor();
    }

    public static void print(final File f) {
        for (File sf : f.listFiles()) {
            if (sf.isDirectory()) {
                System.out.println(sf.getAbsolutePath());
                print(sf);
            } else {
                System.out.println(sf.getAbsolutePath());
            }
        }
    }

    @Test
    public void succesfullyCreatesFilesAndDirectoriesFromZip() throws InterruptedException, ExecutionException {

        task = new ExtractZippedProjectTask(new TaskListener() {

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

        }, settings, zip, exercise, unzipper);

        executor.submit(task).get();

        assertProjectRootContainsFile("/c1/e1/test/OhjelmaTest.java");
        assertProjectRootContainsFile("/c1/e1/src/Ohjelma.java");
        assertProjectRootContainsFile("/c1/e1/nbproject/project.xml");
        assertProjectRootContainsFile("/c1/e1/lib/testrunner/tmc-junit-runner.jar");
        assertProjectRootContainsFile("/c1/e1/lib/junit-4.10.jar");
        assertProjectRootContainsFile("/c1/e1/build.xml");
        assertProjectRootContainsFile("/c1/e1/.tmcproject.json");

    }

    @Test
    public void abortsOnExtractionFailure() throws InterruptedException, ExecutionException, IOException {

        unzipper = mock(Unzipper.class);
        when(unzipper.unzipProject(any(byte[].class), any(File.class), eq(true))).thenThrow(new IOException());

        task = new ExtractZippedProjectTask(new TaskListener() {

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

        }, settings, zip, exercise, unzipper);

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
