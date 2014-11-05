package fi.helsinki.cs.tmc.client.core.async.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.domain.Project;
import fi.helsinki.cs.tmc.client.core.domain.ProjectStatus;
import fi.helsinki.cs.tmc.client.core.domain.Settings;
import fi.helsinki.cs.tmc.client.core.domain.Zip;
import fi.helsinki.cs.tmc.client.core.http.HttpClientFactory;
import fi.helsinki.cs.tmc.client.core.http.HttpWorker;
import fi.helsinki.cs.tmc.client.core.io.reader.TmcProjectFileReader;
import fi.helsinki.cs.tmc.client.core.io.unzip.Unzipper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadExerciseTask extends AbstractTask<File> {

    private static final String DESCRIPTION = "Retrieving course list";
    private static final Logger LOG = LogManager.getLogger();

    private Settings settings;
    private Unzipper unzipper;
    private TmcProjectFileReader projectFileReader;
    private Exercise exercise;

    public DownloadExerciseTask(final TaskListener listener, final Settings settings, final Unzipper unzipper, final TmcProjectFileReader projectFileReader, final Exercise exercise) {

        super(DESCRIPTION, listener);

        this.settings = settings;
        this.unzipper = unzipper;
        this.projectFileReader = projectFileReader;
        this.exercise = exercise;
    }

    @Override
    protected File work() throws InterruptedException, TaskFailureException {

        final HttpWorker http = buildHttpWorker();

        checkForInterrupt();

        final URI uri = buildExerciseDownloadURI();

        checkForInterrupt();

        final Zip zip = downloadZip(http, uri);

        checkForInterrupt();

        final File projectRoot =  extractProject(zip);

        updateExerciseAndProject(projectRoot);

        return projectRoot;
    }

    private HttpWorker buildHttpWorker() {

        final CloseableHttpAsyncClient httpClient = HttpClientFactory.makeHttpClient();
        final ObjectMapper mapper = new ObjectMapper();
        final HttpWorker worker = new HttpWorker(mapper, httpClient);

        return worker;
    }

    private URI buildExerciseDownloadURI() throws TaskFailureException {

        URI uri;
        try {
            uri = new URI(exercise.getDownloadUrl());
        } catch (URISyntaxException exception) {
            LOG.error("Invalid download URI for exercise " + exercise.getId(), exception);
            throw new TaskFailureException(exception);
        }

        return uri;
    }

    private Zip downloadZip(final HttpWorker http, final URI uri) throws TaskFailureException {

        byte[] bytes;
        try {
            bytes = http.from(uri).withCredentials(settings).get().withResponse().asByteArray();
        } catch (IOException exception) {
            LOG.error("Failed to download Zip for exercise " + exercise.getId(), exception);
            throw new TaskFailureException(exception);
        }

        final Zip zip = new Zip();
        zip.setBytes(bytes);

        return zip;
    }

    private File extractProject(final Zip zip) throws TaskFailureException {

        final String projectLocation = exercise.getCourseName() + File.separator + exercise.getName();
        final File destinationFolder = new File(settings.getProjectsRoot(), projectLocation);

        try {
            unzipper.unzipProject(zip.getBytes(), destinationFolder, true);
        } catch (IOException exception) {
            LOG.error("Unable to extract zipped project", exception);
            throw new TaskFailureException("Unable to extract zipped project", exception);
        }

        return destinationFolder;
    }

    private void updateExerciseAndProject(final File projectRoot) {

        final List<String> files = new ArrayList<>();
        recursivelyAddFiles(files, projectRoot);

        if (exercise.getProject() == null) {
            exercise.setProject(new Project(exercise, projectRoot.getAbsolutePath(), files));
        } else {
            exercise.getProject().setProjectFiles(files);
        }

        final Project project = exercise.getProject();

        project.setExercise(exercise);
        project.setStatus(ProjectStatus.DOWNLOADED);

        final File tmcProjectFile = new File(projectRoot.getAbsolutePath() + File.separator + ".tmcproject.yml");
        project.setExtraStudentFiles(projectFileReader.read(tmcProjectFile).getExtraStudentFiles());
    }

    private void recursivelyAddFiles(final List<String> files, final File root) {

        if (!root.isDirectory()) {
            throw new IllegalArgumentException("Provided a file instead of a folder for DownloadExerciseTask.recursivelyAddFiles()");
        }

        for (File file : root.listFiles()) {
            if (file.isFile()) {
                files.add(file.getAbsolutePath());
            } else {
                recursivelyAddFiles(files, file);
            }
        }
    }

    @Override
    protected void cleanUp() { }
}
