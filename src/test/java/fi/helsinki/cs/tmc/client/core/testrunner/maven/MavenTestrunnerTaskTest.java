package fi.helsinki.cs.tmc.client.core.testrunner.maven;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskProgressListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.MavenRunner;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.domain.Project;
import fi.helsinki.cs.tmc.client.core.io.reader.TestResultFileReader;
import fi.helsinki.cs.tmc.client.core.testrunner.TestrunnerListener;
import fi.helsinki.cs.tmc.client.core.testrunner.domain.TestRunResult;
import fi.helsinki.cs.tmc.stylerunner.CheckstyleRunner;
import fi.helsinki.cs.tmc.stylerunner.validation.CheckstyleResult;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CheckstyleRunner.class)
public class MavenTestrunnerTaskTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ExecutorService executor;

    private MavenTestrunnerTask task;

    private TaskListener listener;
    private TaskProgressListener progressListener;
    private MavenRunner runner;
    private TestResultFileReader resultFileReader;
    private CheckstyleRunner checkstyleRunner;

    private Project project;
    private Exercise exercise;

    @Before
    public void setUp() throws Exception {

        this.executor = Executors.newSingleThreadExecutor();

        this.runner = mock(MavenRunner.class);
        this.listener = mock(TestrunnerListener.class);
        this.progressListener = mock(TaskProgressListener.class);
        this.resultFileReader = mock(TestResultFileReader.class);
        this.checkstyleRunner = mock(CheckstyleRunner.class);

        this.exercise = new Exercise("ex", "course");
        this.project = new Project(exercise);
        this.project.addProjectFile(temporaryFolder.newFile("pom.xml").getAbsolutePath());

        this.task = new MavenTestrunnerTask(listener, project, runner, resultFileReader, checkstyleRunner);
        this.task.addProgressListener(progressListener);
    }

    @Test
    public void setsCorrectDescription() {

        assertEquals(MavenTestrunnerTask.DESCRIPTION, task.getDescription());
    }

    @Test
    public void triesToRunCompileGoalAndExitsOnFailure() throws InterruptedException, ExecutionException {

        when(runner.runGoal(eq(MavenTestrunnerTask.COMPILE_GOAL), eq(project))).thenReturn(1);

        final Future<?> job = executor.submit(task);
        job.get();

        verify(progressListener).onProgress(0, 4);
        verify(progressListener).onStart();
        verify(runner).runGoal(eq(MavenTestrunnerTask.COMPILE_GOAL), eq(project));
        verify(listener).onFailure(any(TaskResult.class));
        verify(progressListener).onEnd();
        verifyNoMoreInteractions(listener, progressListener, runner, checkstyleRunner);
    }

    @Test
    public void triesToRunTestrunnerGoalAndExitsOnFailure() throws InterruptedException, ExecutionException {

        when(runner.runGoal(eq(MavenTestrunnerTask.COMPILE_GOAL), eq(project))).thenReturn(0);
        when(runner.runGoal(eq(MavenTestrunnerTask.TESTRUNNER_GOAL), eq(project))).thenReturn(1);

        final Future<?> job = executor.submit(task);
        job.get();

        verify(progressListener).onStart();
        verify(progressListener).onProgress(0, 4);
        verify(runner).runGoal(eq(MavenTestrunnerTask.COMPILE_GOAL), eq(project));
        verify(progressListener).onProgress(1, 4);
        verify(runner).runGoal(eq(MavenTestrunnerTask.TESTRUNNER_GOAL), eq(project));
        verify(listener).onFailure(any(TaskResult.class));
        verify(progressListener).onEnd();
        verifyNoMoreInteractions(listener, progressListener, runner, checkstyleRunner);
    }

    @Test
    public void triesToParseResultFileAndExitsOnFailure() throws InterruptedException, ExecutionException, IOException {

        when(runner.runGoal(eq(MavenTestrunnerTask.COMPILE_GOAL), eq(project))).thenReturn(0);
        when(runner.runGoal(eq(MavenTestrunnerTask.TESTRUNNER_GOAL), eq(project))).thenReturn(0);
        when(resultFileReader.parseTestResults(any(File.class))).thenThrow(new IOException());

        final Future<?> job = executor.submit(task);
        job.get();

        verify(progressListener).onStart();
        verify(progressListener).onProgress(0, 4);
        verify(runner).runGoal(eq(MavenTestrunnerTask.COMPILE_GOAL), eq(project));
        verify(progressListener).onProgress(1, 4);
        verify(runner).runGoal(eq(MavenTestrunnerTask.TESTRUNNER_GOAL), eq(project));
        verify(progressListener).onProgress(2, 4);
        verify(listener).onFailure(any(TaskResult.class));
        verify(progressListener).onEnd();
        verifyNoMoreInteractions(listener, progressListener, runner, checkstyleRunner);
    }

    @Test
    public void callsCorrectListenerMethodOnSuccess() throws InterruptedException, ExecutionException, IOException {

        final TestRunResult results = new TestRunResult(null);

        when(runner.runGoal(eq(MavenTestrunnerTask.COMPILE_GOAL), eq(project))).thenReturn(0);
        when(runner.runGoal(eq(MavenTestrunnerTask.TESTRUNNER_GOAL), eq(project))).thenReturn(0);
        when(resultFileReader.parseTestResults(any(File.class))).thenReturn(results);
        when(checkstyleRunner.run()).thenReturn(new CheckstyleResult());

        final Future<?> job = executor.submit(task);
        job.get();

        verify(progressListener).onStart();
        verify(progressListener).onProgress(0, 4);
        verify(runner).runGoal(eq(MavenTestrunnerTask.COMPILE_GOAL), eq(project));
        verify(progressListener).onProgress(1, 4);
        verify(runner).runGoal(eq(MavenTestrunnerTask.TESTRUNNER_GOAL), eq(project));
        verify(progressListener).onProgress(2, 4);
        verify(progressListener).onProgress(3, 4);
        verify(checkstyleRunner).run();
        verify(progressListener).onProgress(4, 4);
        verify(listener).onSuccess(any(TaskResult.class));
        verify(progressListener).onEnd();
        verifyNoMoreInteractions(listener, progressListener, runner, checkstyleRunner);
    }
}
