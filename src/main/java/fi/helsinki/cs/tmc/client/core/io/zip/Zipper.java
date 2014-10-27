package fi.helsinki.cs.tmc.client.core.io.zip;

import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZipEverythingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZippingDecider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class Zipper {
    
    private File rootDirirectory;
    private ZippingDecider zippingDecider;
    
    public static final ZippingDecider ZIP_ALL_THE_THINGS = new ZipEverythingDecider();
    
    public Zipper(File rootDirectory, ZippingDecider zippingDecider) {
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
        
        ByteArrayOutputStream zipBuffer = new ByteArrayOutputStream();
        ZipOutputStream zipStrem = new ZipOutputStream(zipBuffer);
        
        try {
            zipRecursively(rootDirirectory, zipStrem, "");
        } finally {
            zipStrem.close();
        }

        return zipBuffer.toByteArray();
    }

    private void writeEntry(File file, ZipOutputStream zipStream, String zipPath) throws IOException {
        zipStream.putNextEntry(new ZipEntry(zipPath + "/" + file.getName()));

        FileInputStream in = new FileInputStream(file);
        IOUtils.copy(in, zipStream);
        in.close();
        zipStream.closeEntry();
    }

    /**
     * Zips a directory recursively.
     */
    private void zipRecursively(File directory, ZipOutputStream zipStream, String parentZipPath) throws IOException {
        String thisDirZipPath;
        if (parentZipPath.isEmpty()) {
            thisDirZipPath = directory.getName();
        } else {
            thisDirZipPath = parentZipPath + "/" + directory.getName();
        }

        // Create an entry for the directory
        zipStream.putNextEntry(new ZipEntry(thisDirZipPath + "/"));
        zipStream.closeEntry();

        File[] files = directory.listFiles();
        for (File file : files) {
            boolean isDirectoy = file.isDirectory();
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
