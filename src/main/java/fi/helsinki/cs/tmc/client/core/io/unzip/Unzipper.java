package fi.helsinki.cs.tmc.client.core.io.unzip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Unzipper {

    public interface OverwritingDecider {

        /**
         * Decides whether the given relative path in the project may be overwritten.
         *
         * <p>
         * Only called for files (not directories) whose content has changed.
         *
         * <p>
         * Note that the given path has platform-specific directory separators.
         */
        boolean mayOverwrite(String relativePath);

        /**
         * Decides whether the given relative path in the project may be deleted.
         *
         * <p>
         * Only called for files and directories that are on disk but not in the zip.
         *
         * <p>
         * Note that the given path has platform-specific directory separators.
         */
        boolean mayDelete(String relativePath);
    }

    /**
     * Information about the results of an unzip operation.
     *
     * <p>
     * All lists contain paths relative to the project directory.
     * Directories are not included.
     */
    public class Result {

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

        Result(final File projectDirectory) {

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

    private static OverwritingDecider neverAllowOverwrites = new OverwritingDecider() {

        @Override
        public boolean mayOverwrite(final String relativePath) {

            return false;
        }

        @Override
        public boolean mayDelete(final String relativePath) {

            return false;
        }
    };

    private OverwritingDecider overwriting;


    public Unzipper() {

        this(neverAllowOverwrites);
    }

    public Unzipper(final OverwritingDecider overwriting) {

        this.overwriting = overwriting;
    }

    public Result unzipProject(final byte[] data, final File projectDirectory) throws IOException {

        return unzipProject(data, projectDirectory, true);
    }

    public Result unzipProject(final byte[] data, final File projectDirectory, final boolean reallyWriteFiles) throws IOException {

        final Result result = new Result(projectDirectory);
        final Set<String> pathsInZip = new HashSet<String>();

        final String projectDirectoryInZip = findProjectDirectoryInZip(data);
        if (projectDirectoryInZip == null) {
            throw new IllegalArgumentException("No project directory in zip");
        }

        final ZipInputStream zipInputStream = readZip(data);
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().startsWith(projectDirectoryInZip)) {
                String restOfPath = zipEntry.getName().substring(projectDirectoryInZip.length());
                restOfPath = trimSlashes(restOfPath);

                final String destFileRelativePath = trimSlashes(restOfPath.replace("/", File.separator));
                pathsInZip.add(destFileRelativePath);
                final File destinationFile = new File(projectDirectory.toString() + File.separator + destFileRelativePath);

                if (zipEntry.isDirectory()) {
                    if (reallyWriteFiles) {
                        FileUtils.forceMkdir(destinationFile);
                    }
                } else {
                    final byte[] entryData = IOUtils.toByteArray(zipInputStream);

                    boolean shouldWrite;
                    if (destinationFile.exists()) {
                        if (fileContentEquals(destinationFile, entryData)) {
                            shouldWrite = false;
                            result.unchangedFiles.add(destFileRelativePath);
                        } else if (overwriting.mayOverwrite(destFileRelativePath)) {
                            shouldWrite = true;
                            result.overwrittenFiles.add(destFileRelativePath);
                        } else {
                            shouldWrite = false;
                            result.skippedFiles.add(destFileRelativePath);
                        }
                    } else {
                        shouldWrite = true;
                        result.newFiles.add(destFileRelativePath);
                    }
                    if (shouldWrite && reallyWriteFiles) {
                        FileUtils.forceMkdir(destinationFile.getParentFile());
                        final OutputStream out = new BufferedOutputStream(new FileOutputStream(destinationFile));
                        IOUtils.write(entryData, out);
                        out.close();
                    }
                }
            }
        }

        deleteFilesNotInZip(projectDirectory, projectDirectory, result, pathsInZip, overwriting, reallyWriteFiles);

        return result;
    }

    private void deleteFilesNotInZip(final File projectDirectory, final File currentDirectory, final Result result, final Set<String> pathsInZip, final OverwritingDecider overwriting, final boolean reallyWriteFiles) throws IOException {
        for (File file : currentDirectory.listFiles()) {
            String relativePath = file.getPath().substring(projectDirectory.getPath().length());
            relativePath = trimSlashes(relativePath);

            if (file.isDirectory()) {
                deleteFilesNotInZip(projectDirectory, file, result, pathsInZip, overwriting, reallyWriteFiles);
            }

            if (!pathsInZip.contains(relativePath)) {
                if (overwriting.mayDelete(relativePath)) {
                    if (file.isDirectory() && file.listFiles().length > 0) {
                        // Won't delete directories if they still have contents
                        result.skippedDeletingFiles.add(relativePath);
                    } else {
                        if (reallyWriteFiles) {
                            file.delete();
                        }
                        result.deletedFiles.add(relativePath);
                    }
                } else {
                    result.skippedDeletingFiles.add(relativePath);
                }
            }
        }
    }

    private String trimSlashes(final String path) {

        String newPath = path;

        while (newPath.startsWith("/") || newPath.startsWith(File.separator)) {
            newPath = newPath.substring(1);
        }

        while (newPath.endsWith("/") || newPath.startsWith(File.separator)) {
            newPath = path.substring(0, newPath.length() - 1);
        }

        return newPath;
    }

    private String findProjectDirectoryInZip(final byte[] data) throws IOException {
        final ZipInputStream zipInputStream = readZip(data);
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            final String name = zipEntry.getName();
            if (name.endsWith("/nbproject/") || name.endsWith("/pom.xml") || name.endsWith(".universal/")) {
                return directoryName(zipEntry.getName());
            }
        }
        return null;
    }

    private String directoryName(final String zipPath) {

        String newPath = zipPath;

        while (newPath.endsWith("/")) {
            newPath = newPath.substring(0, zipPath.length() - 1);
        }

        return newPath.replaceAll("/[^/]+$", "");
    }

    private ZipInputStream readZip(final byte[] data) {

        return new ZipInputStream(new ByteArrayInputStream(data));
    }

    private boolean fileContentEquals(final File file, final byte[] data) throws IOException {

        final InputStream fileIs = new BufferedInputStream(new FileInputStream(file));
        final InputStream dataIs = new ByteArrayInputStream(data);
        final boolean equal = IOUtils.contentEquals(fileIs, dataIs);

        fileIs.close();
        dataIs.close();

        return equal;
    }
}
