package fi.helsinki.cs.tmc.client.core.async.task;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskMonitor;
import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;
import fi.helsinki.cs.tmc.client.core.clientspecific.Settings;
import fi.helsinki.cs.tmc.client.core.domain.Exercise;
import fi.helsinki.cs.tmc.client.core.domain.Zip;
import fi.helsinki.cs.tmc.client.core.io.unzip.Unzipper;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtractZippedProjectTask extends AbstractTask<File> {

    private static final String DESCRIPTION = "Extracting project files";
    private static final Logger LOG = LogManager.getLogger();

    private Settings settings;
    private Zip zip;
    private Exercise exercise;
    private Unzipper unzipper;

    public ExtractZippedProjectTask(final TaskListener listener, final Settings settings, final Zip zip, final Exercise exercise, final Unzipper unzipper) {

        super(DESCRIPTION, listener, new TaskMonitor(2));

        this.settings = settings;
        this.zip = zip;
        this.exercise = exercise;
        this.unzipper = unzipper;
    }

    @Override
    protected File work() throws InterruptedException, TaskFailureException {

        final File destinationFolder = new File(settings.projectsRoot(), exercise.getProjectLocation());

        try {
            unzipper.unzipProject(zip.getBytes(), destinationFolder, true);
        } catch (IOException exception) {
            LOG.error("Unable to extract zipped project", exception);
            throw new TaskFailureException("Unable to extract zipped project", exception);
        }

        return destinationFolder;

    }

    @Override
    protected void cleanUp() { }



}
