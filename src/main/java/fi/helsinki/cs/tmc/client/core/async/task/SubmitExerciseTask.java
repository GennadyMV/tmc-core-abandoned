package fi.helsinki.cs.tmc.client.core.async.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.domain.Settings;
import fi.helsinki.cs.tmc.client.core.domain.SubmissionResponse;
import fi.helsinki.cs.tmc.client.core.domain.SubmissionResult;
import fi.helsinki.cs.tmc.client.core.http.HttpClientFactory;
import fi.helsinki.cs.tmc.client.core.http.HttpWorker;
import fi.helsinki.cs.tmc.client.core.io.zip.Zipper;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZipEverythingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZippingDecider;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubmitExerciseTask extends AbstractTask<SubmissionResult> {

    private static final Logger LOG = LogManager.getLogger();
    private static final String DESCRIPTION = "Submitting exercise to server";

    private static final int STATUS_REFRESH_INTERVAL = 2000;

    private Settings settings;
    private Exercise exercise;

    public SubmitExerciseTask(final TaskListener listener, final Settings settings, final Exercise exercise) {

        super(DESCRIPTION, listener);

        this.settings = settings;
        this.exercise = exercise;
    }

    @Override
    protected SubmissionResult work() throws InterruptedException, TaskFailureException {

        setProgress(0, 4);

        final byte[] zip = compressProject();

        setProgress(1, 4);
        checkForInterrupt();

        final HttpWorker http = buildHttpWorker();

        setProgress(2, 4);
        checkForInterrupt();

        final SubmissionResponse response = submitToServer(http, zip);

        setProgress(3, 4);
        checkForInterrupt();

        return getSubmissionResult(response.getSubmissionUrl(), http);
    }

    private byte[] compressProject() throws TaskFailureException {

        final File projectRootDirectory = new File(exercise.getProject().getRootPath());

        ZippingDecider zippingDecider;
        try {
            zippingDecider = exercise.getProject().getProjectType().getZippingDecider();
        } catch (InstantiationException | IllegalAccessException exception) {
            LOG.warn("Unable to instantiate zipping decider, unsing default decider");
            zippingDecider = new ZipEverythingDecider();
        }
        
        final Zipper zipper = new Zipper(projectRootDirectory, zippingDecider);

        try {
            return zipper.zipProjectSources();
        } catch (IOException exception) {
            throw new TaskFailureException("Unable to zip project", exception);
        }
    }

    private HttpWorker buildHttpWorker() {

        final ObjectMapper mapper = new ObjectMapper();
        return new HttpWorker(mapper);
    }

    private SubmissionResponse submitToServer(final HttpWorker http, final byte[] zip) throws TaskFailureException {

        final URI submissionURI;
        try {
            submissionURI = new URI(exercise.getReturnUrl());
        } catch (URISyntaxException exception) {
            throw new TaskFailureException("Invalid submission URI: " + exercise.getReturnUrl(), exception);
        }

        try {
            return http.to(submissionURI).withCredentials(settings).post(zip).withResponse().json().as(SubmissionResponse.class);
        } catch (IOException exception) {
            throw new TaskFailureException("Server communication failure", exception);
        }
    }

    private SubmissionResult getSubmissionResult(final URI resultPollingURI, final HttpWorker http) throws InterruptedException, TaskFailureException {

        SubmissionResult result;
        do {

            Thread.sleep(STATUS_REFRESH_INTERVAL);

            try {
                result = http.from(resultPollingURI).withCredentials(settings).get().withResponse().json().as(SubmissionResult.class);
            } catch (IOException exception) {
                throw new TaskFailureException("Server communication failure", exception);
            }
        } while (result.getStatus() == SubmissionResult.Status.PROCESSING);

        return result;
    }

    @Override
    protected void cleanUp() { }
}
