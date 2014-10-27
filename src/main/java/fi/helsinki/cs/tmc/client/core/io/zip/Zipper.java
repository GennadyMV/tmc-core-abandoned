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

    private File rootDirirectory;
    private ZippingDecider zippingDecider;

    public Zipper(final File rootDirectory, final ZippingDecider zippingDecider) {
        this.rootDirirectory = rootDirectory;
        this.zippingDecider = zippingDecider;
    }

    /**
     * Zip up a project directory, only including stuff decided by the {@link ZippingDecider}.
     */
    public byte[] zipProjectSources() throws IOException {
        if (!rootDirirectory.exists() || !rootDirirectory.isDirectory()) {
            throw new FileNotFoundException("Root directory " + rootDirirectory + " not found for zipping!");
        }

        final ByteArrayOutputStream zipBuffer = new ByteArrayOutputStream();
        final ZipOutputStream zipStrem = new ZipOutputStream(zipBuffer);

        try {
            zipRecursively(rootDirirectory, zipStrem, "");
        } finally {
            zipStrem.close();
        }

        return zipBuffer.toByteArray();
    }

    private void writeEntry(final File file, final ZipOutputStream zipStream, final String zipPath) throws IOException {
        zipStream.putNextEntry(new ZipEntry(zipPath + "/" + file.getName()));

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
            thisDirZipPath = parentZipPath + "/" + directory.getName();
        }

        // Create an entry for the directory
        zipStream.putNextEntry(new ZipEntry(thisDirZipPath + "/"));
        zipStream.closeEntry();

        final File[] files = directory.listFiles();
        for (File file : files) {
            final boolean isDirectoy = file.isDirectory();
            String zipPath = thisDirZipPath + "/" + file.getName();
            if (isDirectoy) {
                zipPath += "/";
            }
            if (zippingDecider.shouldZip(zipPath)) {
                if (isDirectoy) {
                    zipRecursively(file, zipStream, thisDirZipPath);
                } else {
                    writeEntry(file, zipStream, thisDirZipPath);
                }
            }
        }
    }
}
