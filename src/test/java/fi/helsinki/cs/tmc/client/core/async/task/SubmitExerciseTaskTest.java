package fi.helsinki.cs.tmc.client.core.async.task;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskProgressListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.domain.Project;
import fi.helsinki.cs.tmc.client.core.domain.Settings;
import fi.helsinki.cs.tmc.client.core.domain.SubmissionResult;
import fi.helsinki.cs.tmc.client.core.testutil.MockTMCServer;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class SubmitExerciseTaskTest {

    @Rule
    public final TemporaryFolder projectRoot = new TemporaryFolder();

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(8089);
    private static final MockTMCServer SERVER = new MockTMCServer();

    private Settings settings;
    private Exercise exercise;

    private ExecutorService executor;

    @BeforeClass
    public static void setUpClass() {

        SERVER.initialiseServer();
    }

    @Before
    public void setUp() throws Exception {

        this.settings = new Settings("http://localhost:8089", "7", "Core", "1", null, "password", "username", null, Locale.ENGLISH);

        this.exercise = new Exercise("e1", "c1");
        this.exercise.setId(1);
        this.exercise.setCourse(null);
        this.exercise.setReturnUrl(settings.getTmcServerBaseUrl() + MockTMCServer.EXERCISE_RETURN_URL);

        Project project = new Project(exercise);
        projectRoot.newFile("testi.txt");
        project.setRootPath(projectRoot.getRoot().toString());
        this.exercise.setProject(project);

        executor = Executors.newSingleThreadExecutor();
    }

    @Test(timeout = 10000L)
    public void testSubmitExerciseWorkFlow() throws Exception {
        for(StubMapping matching : WireMock.listAllStubMappings().getMappings()) {
            System.out.println(matching.getRequest());
        }
        SubmitExerciseTask submitExerciseTask = new SubmitExerciseTask(new TaskListener() {
            @Override public void onFailure(TaskResult<?> result) {fail("Task should not fail");}
            @Override public void onInterrupt(TaskResult<?> result) {fail("Task should not be interrupted");}

            @Override
            public void onSuccess(TaskResult<?> result) {

                SubmissionResult submissionResult = (SubmissionResult) result.result();
                assertEquals(SubmissionResult.Status.OK, submissionResult.getStatus());
            }
        }, settings, exercise);

        final AtomicBoolean polling = new AtomicBoolean(false);
        submitExerciseTask.addProgressListener(new TaskProgressListener() {
            @Override public void onStart() {}
            @Override public void onEnd() {}

            @Override
            public void onProgress(int current, int estimatedTotal) {

                if (current == estimatedTotal - 1) {
                    polling.set(true);
                }
            }
        });

        Future future = executor.submit(submitExerciseTask);
        while(!polling.get()) {
            Thread.sleep(200);
        }

        Thread.sleep(2500);
        SERVER.stubGetWithJsonResponse(Integer.MAX_VALUE - 1, MockTMCServer.EXERCISE_SUBMISSION_URL, MockTMCServer.EXERCISE_SUBMISSION_OK_CONTENT);

        future.get();
        WireMock.verify(1, WireMock.postRequestedFor((WireMock.urlMatching(MockTMCServer.EXERCISE_RETURN_URL))));
        System.out.println(WireMock.findAll(WireMock.postRequestedFor((WireMock.urlMatching(MockTMCServer.EXERCISE_RETURN_URL)))));
        WireMock.verify(2, WireMock.getRequestedFor(WireMock.urlMatching(MockTMCServer.EXERCISE_SUBMISSION_URL + MockTMCServer.MAYBE_PARAM)));
    }
}