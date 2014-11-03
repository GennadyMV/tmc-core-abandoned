package fi.helsinki.cs.tmc.client.core.io.unzip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about the results of an unzip operation.
 *
 * <p>
 * All lists contain paths relative to the project directory.
 * Directories are not included.
 */
public class UnzippingResult {

    /**
     * The project directory to which we extracted.
     */
    private File projectDirectory;

    /**
     * Files that were in the zip but did not exist before.
     * In the usual case of downloading a new project, all files go here.
     */
    private List<String> newFiles = new ArrayList<String>();

    /**
     * Files overwritten as permitted by the given {@code OverwritingDecider}.
     */
    private List<String> overwrittenFiles = new ArrayList<String>();

    /**
     * Files skipped because the given {@code OverwritingDecider} didn't allow overwriting.
     */
    private List<String> skippedFiles = new ArrayList<String>();

    /**
     * Files that existed before but were the same in the zip.
     */
    private List<String> unchangedFiles = new ArrayList<String>();

    /**
     * Files that were deleted because they weren't in the zip.
     */
    private List<String> deletedFiles = new ArrayList<String>();

    /**
     * Files skipped because the given {@code OverwritingDecider} didn't allow deleting.
     */
    private List<String> skippedDeletingFiles = new ArrayList<String>();

    UnzippingResult(final File projectDirectory) {

        this.projectDirectory = projectDirectory;
    }

    public File getProjectDirectory() {

        return projectDirectory;
    }

    public void setProjectDirectory(final File projectDirectory) {

        this.projectDirectory = projectDirectory;
    }

    public List<String> getNewFiles() {

        return newFiles;
    }

    public void setNewFiles(final List<String> newFiles) {

        this.newFiles = newFiles;
    }

    public List<String> getOverwrittenFiles() {

        return overwrittenFiles;
    }

    public void setOverwrittenFiles(final List<String> overwrittenFiles) {

        this.overwrittenFiles = overwrittenFiles;
    }

    public List<String> getSkippedFiles() {

        return skippedFiles;
    }

    public void setSkippedFiles(final List<String> skippedFiles) {

        this.skippedFiles = skippedFiles;
    }

    public List<String> getUnchangedFiles() {

        return unchangedFiles;
    }

    public void setUnchangedFiles(final List<String> unchangedFiles) {

        this.unchangedFiles = unchangedFiles;
    }

    public List<String> getDeletedFiles() {

        return deletedFiles;
    }

    public void setDeletedFiles(final List<String> deletedFiles) {

        this.deletedFiles = deletedFiles;
    }

    public List<String> getSkippedDeletingFiles() {

        return skippedDeletingFiles;
    }

    public void setSkippedDeletingFiles(final List<String> skippedDeletingFiles) {

        this.skippedDeletingFiles = skippedDeletingFiles;
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();
        sb.append("New: ").append(newFiles).append('\n');
        sb.append("Overwritten: ").append(overwrittenFiles).append('\n');
        sb.append("Skipped: ").append(skippedFiles).append('\n');
        sb.append("Unchanged: ").append(unchangedFiles).append('\n');
        sb.append("Deleted: ").append(deletedFiles).append('\n');
        sb.append("Not deleted: ").append(deletedFiles).append('\n');

        return sb.toString();
    }
}
