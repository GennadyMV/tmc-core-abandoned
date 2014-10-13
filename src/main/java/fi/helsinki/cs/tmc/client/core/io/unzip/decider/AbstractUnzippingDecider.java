package fi.helsinki.cs.tmc.client.core.io.unzip.decider;

import fi.helsinki.cs.tmc.client.core.domain.Project;
import fi.helsinki.cs.tmc.client.core.io.FileIO;
import fi.helsinki.cs.tmc.client.core.io.TmcProjectFileReader;

import java.util.List;

/**
 * Abstract base class that provides common functionality for all unzipping
 * deciders.
 */
public abstract class AbstractUnzippingDecider implements UnzippingDecider {

    private Project project;
    private List<String> extraStudentFiles;
    
    @Override
    public Project getProject() {
        
        return this.project;
    }
    
    @Override
    public void setProject(final Project project) {
        
        this.project = project;
        
        final FileIO projectFile = new FileIO(project.getRootPath() + "/.tmcproject.yml");
        this.extraStudentFiles = TmcProjectFileReader.read(projectFile).getExtraStudentFiles();
    }
    
    @Override
    public boolean shouldUnzip(final String filePath) {
        
        if (isExtraStudentFile(filePath)) {
            return false;
        }
        
        if (isProtectedFile(filePath)) {
            return false;
        }
        
        return true;
    }

    protected abstract boolean isProtectedFile(String filePath);

    /**
     * Prevents unzipping if file would overwrite file on the extra student files list.
     */
    public boolean isExtraStudentFile(final String filePath) {
        
        if (project == null) {
            throw new IllegalArgumentException("Project not set");
        }

        for (String path : extraStudentFiles) {
            
            String noUnzipPath = path;
            
            if (noUnzipPath.charAt(noUnzipPath.length() - 1) == '/') {
                noUnzipPath = noUnzipPath.substring(0, noUnzipPath.length() - 1);
            }

            noUnzipPath = project.getRootPath() + "/" + noUnzipPath;

            if (filePath.startsWith(noUnzipPath) && (filePath.equals(noUnzipPath) || filePath.charAt(noUnzipPath.length()) == '/')) {
                return true;
            }
        }
        
        return false;
    }
}
