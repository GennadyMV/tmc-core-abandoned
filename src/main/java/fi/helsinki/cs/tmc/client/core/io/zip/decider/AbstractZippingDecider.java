package fi.helsinki.cs.tmc.client.core.io.zip.decider;

import fi.helsinki.cs.tmc.client.core.domain.Project;

import java.io.File;

public abstract class AbstractZippingDecider implements ZippingDecider {

    private Project project;

    public AbstractZippingDecider(final Project project) {

        this.project = project;
    }

    protected Project getProject() {

        return project;
    }

    @Override
    public boolean shouldZip(final String path) {

        final File projectRoot = new File(project.getRootPath());
        final File directory = new File(projectRoot.getParentFile(), path);

        if (!directory.isDirectory()) {
            return true;
        }

        final File noSubmitFile = new File(directory, ".tmcnosubmit");
        if (noSubmitFile.exists()) {
            return false;
        }

        return true;
    }
}
