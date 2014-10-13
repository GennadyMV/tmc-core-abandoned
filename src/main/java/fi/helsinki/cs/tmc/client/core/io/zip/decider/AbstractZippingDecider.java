package fi.helsinki.cs.tmc.client.core.io.zip.decider;

import fi.helsinki.cs.tmc.client.core.domain.Project;

import java.io.File;

/**
 * Abstract base class for all zipping deciders.
 */
public abstract class AbstractZippingDecider implements ZippingDecider {

    private Project project;
    
    public Project getProject() {
        
        return this.project;
    }
    
    public void setProject(final Project project) {
        
        this.project = project;
    }
    
    @Override
    public boolean shouldZip(final String zipPath) {
        
        if (project == null) {
            throw new IllegalArgumentException("Project not set");
        }
        
        if (isNoSubmitFolder(zipPath)) {
            return false;
        }
        
        return isStudentFile(zipPath);
    }
    
    protected abstract boolean isStudentFile(String zipPath);

    /**
     * Does not include any folders that contain .tmcnosubmit-file.
     */
    private boolean isNoSubmitFolder(final String zipPath) {

        final File dir = new File(new File(project.getRootPath()).getParentFile(), zipPath);
        
        if (!dir.isDirectory()) {
            return true;
        }

        return !new File(dir, ".tmcnosubmit").exists();

    }
}
