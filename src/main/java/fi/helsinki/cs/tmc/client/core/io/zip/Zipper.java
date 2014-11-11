package fi.helsinki.cs.tmc.client.core.io.zip;

import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZipEverythingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZippingDecider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class Zipper {

    public static final ZippingDecider ZIP_ALL_THE_THINGS = new ZipEverythingDecider();
    public static final char SEPARATOR = '/';

    private File rootDirectory;
    private ZippingDecider zippingDecider;

    public Zipper(final File rootDirectory, final ZippingDecider zippingDecider) {
        this.rootDirectory = rootDirectory;
        this.zippingDecider = zippingDecider;
    }

    /**
     * Zip up a project directory, only including stuff decided by the {@link ZippingDecider}.
     */
    public byte[] zipProjectSources() throws IOException {
        if (!rootDirectory.isDirectory()) {
            throw new FileNotFoundException("Root directory " + rootDirectory + " not found for zipping!");
        }

        final ByteArrayOutputStream zipBuffer = new ByteArrayOutputStream();

        try (ZipOutputStream zipStream = new ZipOutputStream(zipBuffer)) {
            zipRecursively(rootDirectory, zipStream, "");
        }

        return zipBuffer.toByteArray();
    }

    private void writeEntry(final File file, final ZipOutputStream zipStream, final String zipPath) throws IOException {
        zipStream.putNextEntry(new ZipEntry(zipPath + SEPARATOR + file.getName()));

        final FileInputStream in = new FileInputStream(file);
        IOUtils.copy(in, zipStream);
        in.close();
        zipStream.closeEntry();
    }

    /**
     * Zips a directory recursively.
     */
    private void zipRecursively(final File directory, final ZipOutputStream zipStream, final String parentZipPath) throws IOException {
        final String thisDirZipPath;
        if (parentZipPath.isEmpty()) {
            thisDirZipPath = directory.getName();
        } else {
            thisDirZipPath = parentZipPath + SEPARATOR + directory.getName();
        }

        // Create an entry for the directory
        zipStream.putNextEntry(new ZipEntry(thisDirZipPath + SEPARATOR));
        zipStream.closeEntry();

        for (File file : directory.listFiles()) {
            final boolean isDirectory = file.isDirectory();
            String zipPath = thisDirZipPath + SEPARATOR + file.getName();
            if (isDirectory) {
                zipPath += SEPARATOR;
            }
            if (zippingDecider.shouldZip(zipPath)) {
                if (isDirectory) {
                    zipRecursively(file, zipStream, thisDirZipPath);
                } else {
                    writeEntry(file, zipStream, thisDirZipPath);
                }
            }
        }
    }
}
