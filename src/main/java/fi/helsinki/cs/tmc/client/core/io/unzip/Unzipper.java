package fi.helsinki.cs.tmc.client.core.io.unzip;

import fi.helsinki.cs.tmc.client.core.domain.Zip;
import fi.helsinki.cs.tmc.client.core.io.FileIO;
import fi.helsinki.cs.tmc.client.core.io.unzip.decider.UnzippingDecider;
import fi.helsinki.cs.tmc.client.core.util.PathUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper {

    private static final int BUFFER_SIZE = 1024;

    private final Zip project;
    private final UnzippingDecider decider;

    public Unzipper(final Zip project, final UnzippingDecider decider) {

        this.project = project;
        this.decider = decider;
    }

    public List<String> unzipTo(final FileIO destinationFolder) throws IOException {

        final List<String> projectFiles = new ArrayList<String>();

        destinationFolder.createFolderTree(false);
        final ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(project.getBytes()));

        ZipEntry zipEntry = zipStream.getNextEntry();
        while (zipEntry != null) {

            final String entryAbsolutePath = PathUtil.append(destinationFolder.getPath(), zipEntry.getName());

            projectFiles.add(entryAbsolutePath);

            if (!decider.shouldUnzip(entryAbsolutePath)) {
                zipEntry = zipStream.getNextEntry();
                continue;
            }

            final FileIO file = new FileIO(entryAbsolutePath);
            file.createFolderTree(!zipEntry.isDirectory());

            if (!zipEntry.isDirectory()) {

                writeFile(zipStream, file);
            }

            zipEntry = zipStream.getNextEntry();
        }

        return projectFiles;
    }

    private void writeFile(final ZipInputStream zipStream, final FileIO file) throws IOException {

        final ByteArrayOutputStream writeStream = new ByteArrayOutputStream();
        final byte[] buffer = new byte[BUFFER_SIZE];

        while (zipStream.available() != 0) {

            writeStream.write(buffer, 0, zipStream.read(buffer));
        }

        file.write(writeStream.toByteArray());
    }
}
