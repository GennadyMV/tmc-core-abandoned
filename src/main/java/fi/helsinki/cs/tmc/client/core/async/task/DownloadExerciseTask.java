package fi.helsinki.cs.tmc.client.core.async.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskMonitor;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;
import fi.helsinki.cs.tmc.client.core.clientspecific.Settings;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.domain.Zip;
import fi.helsinki.cs.tmc.client.core.http.HttpClientFactory;
import fi.helsinki.cs.tmc.client.core.http.HttpWorker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadExerciseTask extends AbstractTask<Zip> {

    private static final String DESCRIPTION = "Retrieving course list";
    private static final Logger LOG = LogManager.getLogger();

    private Settings settings;
    private Exercise exercise;

    public DownloadExerciseTask(final TaskListener listener, final Settings settings, final Exercise exercise) {

        super(DESCRIPTION, listener, new TaskMonitor(1));

        this.settings = settings;
        this.exercise = exercise;
    }

    @Override
    protected Zip work() throws InterruptedException, TaskFailureException {

        final CloseableHttpAsyncClient httpClient = HttpClientFactory.makeHttpClient();
        final ObjectMapper mapper = new ObjectMapper();
        final HttpWorker http = new HttpWorker(mapper, httpClient);

        checkForInterrupt();

        URI uri;
        try {
            uri = new URI(exercise.getDownloadUrl());
        } catch (URISyntaxException exception) {
            LOG.error("Invalid download URI for exercise " + exercise.getId(), exception);
            throw new TaskFailureException(exception);
        }

        checkForInterrupt();

        byte[] bytes;
        try {
            bytes = http.from(uri).withCredentials(settings).get().withResponse().asByteArray();
        } catch (IOException exception) {
            LOG.error("Failed to download Zip for exercise " + exercise.getId(), exception);
            throw new TaskFailureException(exception);
        }

        checkForInterrupt();

        final Zip zip = new Zip();
        zip.setBytes(bytes);

        return zip;
    }


    @Override
    protected void cleanUp() { }
}
