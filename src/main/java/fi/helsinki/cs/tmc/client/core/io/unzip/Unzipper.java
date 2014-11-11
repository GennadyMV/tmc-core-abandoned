package fi.helsinki.cs.tmc.client.core.io.unzip;

import fi.helsinki.cs.tmc.client.core.io.unzip.decider.NeverOverwritingDecider;
import fi.helsinki.cs.tmc.client.core.io.unzip.decider.OverwritingDecider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Unzipper {

    private static OverwritingDecider neverAllowOverwrites = new NeverOverwritingDecider();

    private OverwritingDecider overwriting;


    public Unzipper() {

        this(neverAllowOverwrites);
    }

    public Unzipper(final OverwritingDecider overwriting) {

        this.overwriting = overwriting;
    }

    public UnzippingResult unzipProject(final byte[] data, final File projectDirectory) throws IOException {

        return unzipProject(data, projectDirectory, true);
    }

    public UnzippingResult unzipProject(final byte[] data, final File projectDirectory, final boolean reallyWriteFiles) throws IOException {

        final UnzippingResult result = new UnzippingResult(projectDirectory);
        final Set<String> pathsInZip = new HashSet<>();

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
                            result.getUnchangedFiles().add(destFileRelativePath);
                        } else if (overwriting.mayOverwrite(destFileRelativePath)) {
                            shouldWrite = true;
                            result.getOverwrittenFiles().add(destFileRelativePath);
                        } else {
                            shouldWrite = false;
                            result.getSkippedFiles().add(destFileRelativePath);
                        }
                    } else {
                        shouldWrite = true;
                        result.getNewFiles().add(destFileRelativePath);
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

    private void deleteFilesNotInZip(final File projectDirectory, final File currentDirectory, final UnzippingResult result, final Set<String> pathsInZip, final OverwritingDecider overwriting, final boolean reallyWriteFiles) throws IOException {
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
                        result.getSkippedDeletingFiles().add(relativePath);
                    } else {
                        if (reallyWriteFiles) {
                            file.delete();
                        }
                        result.getDeletedFiles().add(relativePath);
                    }
                } else {
                    result.getSkippedDeletingFiles().add(relativePath);
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
