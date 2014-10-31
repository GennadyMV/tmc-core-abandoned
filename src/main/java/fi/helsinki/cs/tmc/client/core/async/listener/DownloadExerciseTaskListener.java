package fi.helsinki.cs.tmc.client.core.async.listener;

import fi.helsinki.cs.tmc.client.core.async.TaskListener;
import fi.helsinki.cs.tmc.client.core.async.TaskResult;
import fi.helsinki.cs.tmc.client.core.clientspecific.UIInvoker;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadExerciseTaskListener implements TaskListener {

    private static final Logger LOG = LogManager.getLogger();

    private UIInvoker uiInvoker;

    public DownloadExerciseTaskListener(final UIInvoker uiInvoker) {

        this.uiInvoker = uiInvoker;
    }

    @Override
    public void onSuccess(final TaskResult<? extends Object> result) {

        final File projectRoot = (File) result.result();

        uiInvoker.openProject(projectRoot);
    }

    @Override
    public void onFailure(final TaskResult<? extends Object> result) {

        final String error = "Unable to download exercise: \n" +
                             result.exception().getMessage();

        uiInvoker.userVisibleException(error);
    }

    @Override
    public void onInterrupt(final TaskResult<? extends Object> result) { }
}
