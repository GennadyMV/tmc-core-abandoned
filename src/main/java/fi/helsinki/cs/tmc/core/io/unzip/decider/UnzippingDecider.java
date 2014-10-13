package fi.helsinki.cs.tmc.core.io.unzip.decider;

import fi.helsinki.cs.tmc.core.domain.Project;

/**
 * Interface for unzipping deciders that decide if file with given path should
 * be unzipped or not. This is used for example to protect user's source files
 * when updating exercises so that any work is not lost on update.
 */
public interface UnzippingDecider {

    boolean shouldUnzip(String filePath);
    
    void setProject(Project project);
    Project getProject();
}
