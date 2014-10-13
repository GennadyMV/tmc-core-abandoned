package fi.helsinki.cs.tmc.client.core.io.unzip.decider;

import fi.helsinki.cs.tmc.client.core.domain.Project;

/**
 * Unzips everything.
 */
public class UnzipAllTheThings implements UnzippingDecider {
    
    private Project project;
    
    @Override
    public Project getProject() {
        
        return this.project;
    }
    
    @Override
    public void setProject(final Project project) {
        
        this.project = project;
    }

    @Override
    public boolean shouldUnzip(final String filePath) {

        return true;
    }

}
