package fi.helsinki.cs.tmc.client.core.io.zip.decider;

import fi.helsinki.cs.tmc.client.core.domain.Project;

public class DefaultZippingDecider extends AbstractZippingDecider {

    public DefaultZippingDecider(final Project project) {

        super(project);
    }

    @Override
    public boolean shouldZip(final String path) {

        if (!super.shouldZip(path)) {
            return false;
        }

        if (getProject().getExtraStudentFiles() != null && getProject().getExtraStudentFiles().contains(path)) {
            return true;
        } else {
            return path.contains("/src/");
        }
    }
}
