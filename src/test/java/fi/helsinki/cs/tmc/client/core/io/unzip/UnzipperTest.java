package fi.helsinki.cs.tmc.client.core.io.unzip;

import fi.helsinki.cs.tmc.client.core.io.unzip.Unzipper.Result;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class UnzipperTest {

    private static final String FILE1_NAME = "one.txt";
    private static final String FILE2_NAME = "two.txt";
    private static final String FILE3_NAME = "three.txt";

    private static final String REMAINING = "This should remain";
    private static final String GENERIC = "stuff";
    private static final String DELETED = "deleted";
    private static final String PRESERVED = "preserved";

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    private final String fsep = File.separator;

    private ByteArrayOutputStream zipBuffer;
    private ZipOutputStream zipOut;

    @Before
    public void setUp() throws IOException {

        zipBuffer = new ByteArrayOutputStream();
        zipOut = new ZipOutputStream(zipBuffer);
    }

    private void addFakeProjectToZip(final String path, final String name) throws IOException {
        writeDirToZip(path + "/");
        writeDirToZip(path + "/nbproject/");
        writeFileToZip(path + "/nbproject/project.xml", "Fake project.xml of " + name);
        writeDirToZip(path + "/src/");
        writeFileToZip(path + "/src/Hello.java", "Fake Java file of " + name);
    }

    private void writeDirToZip(final String path) throws IOException {
        assertTrue(path.endsWith("/"));
        zipOut.putNextEntry(new ZipEntry(path));
    }

    private void writeFileToZip(final String path, final String content) throws IOException {
        final ZipEntry zent = new ZipEntry(path);
        zipOut.putNextEntry(zent);
        final Writer w = new OutputStreamWriter(zipOut, "UTF-8");
        w.write(content);
        w.flush();
    }

    private File inTempDir(final String subpath) {
        return new File(tempDir.getRoot() + File.separator + subpath);
    }

    @Test
    public void itShouldUnzipTheFirstProjectDirectoryItSeesInAZip() throws IOException {
        addFakeProjectToZip("dir1/dir12/project1", "P1");
        addFakeProjectToZip("dir2/project2", "P2");
        addFakeProjectToZip("project3", "P3");
        zipOut.close();

        final Unzipper unzipper = new Unzipper();
        final Result result = unzipper.unzipProject(zipBuffer.toByteArray(), inTempDir("my-project"));

        assertEquals(1, tempDir.getRoot().listFiles().length);
        String contents = FileUtils.readFileToString(new File(tempDir.getRoot() + File.separator + "my-project/nbproject/project.xml"));
        assertEquals("Fake project.xml of P1", contents);
        contents = FileUtils.readFileToString(new File(tempDir.getRoot() + File.separator + "my-project/src/Hello.java"));
        assertEquals("Fake Java file of P1", contents);

        assertTrue(result.getNewFiles().contains("nbproject" + fsep + "project.xml"));
        assertTrue(result.getNewFiles().contains("src" + fsep + "Hello.java"));
        assertEquals(2, result.getNewFiles().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void itShouldFailIfTheZipContainsNoProjectDirectory() throws IOException {
        writeDirToZip("dir1/");
        writeDirToZip("dir1/dir2/");
        writeFileToZip("dir1/dir2/oops.txt", "oops");
        zipOut.close();

        final Unzipper unzipper = new Unzipper();
        unzipper.unzipProject(zipBuffer.toByteArray(), inTempDir("my-project"));
    }

    @Test
    public void itShouldOnlyOverwriteExistingFilesIfTheyveChangedAndTheOverwritingDeciderPermits() throws IOException {
        writeDirToZip("dir1/");
        writeDirToZip("dir1/nbproject/");
        writeFileToZip("dir1/one.txt", "one");
        writeFileToZip("dir1/two.txt", "two");
        writeFileToZip("dir1/three.txt", "three");
        writeFileToZip("dir1/four.txt", "four");
        zipOut.close();

        new File(tempDir.getRoot() + "/dest").mkdirs();
        final File expectedPreservedFile = new File(tempDir.getRoot() + "/dest/one.txt");
        FileUtils.write(expectedPreservedFile, REMAINING);
        final File expectedOverwrittenFile = new File(tempDir.getRoot() + "/dest/two.txt");
        FileUtils.write(expectedOverwrittenFile, "This should be overwritten");
        final File expectedNewFile = new File(tempDir.getRoot() + "/dest/three.txt");
        final File expectedSameFile = new File(tempDir.getRoot() + "/dest/four.txt");
        FileUtils.write(expectedSameFile, "four");

        final Unzipper.OverwritingDecider overwriting = mock(Unzipper.OverwritingDecider.class);
        when(overwriting.mayOverwrite(FILE1_NAME)).thenReturn(false);
        when(overwriting.mayOverwrite(FILE2_NAME)).thenReturn(true);

        final Unzipper unzipper = new Unzipper(overwriting);
        final Result result = unzipper.unzipProject(zipBuffer.toByteArray(), inTempDir("dest"));

        verify(overwriting).mayOverwrite(FILE1_NAME);
        verify(overwriting).mayOverwrite(FILE2_NAME);

        // Don't care about these here
        verify(overwriting, atLeast(0)).mayDelete(anyString());

        // Should only call for existing files
        verifyNoMoreInteractions(overwriting);

        assertEquals(REMAINING, FileUtils.readFileToString(expectedPreservedFile));
        assertEquals("two", FileUtils.readFileToString(expectedOverwrittenFile));
        assertEquals("three", FileUtils.readFileToString(expectedNewFile));
        assertEquals("four", FileUtils.readFileToString(expectedSameFile));

        assertTrue(result.getSkippedFiles().contains(FILE1_NAME));
        assertTrue(result.getOverwrittenFiles().contains(FILE2_NAME));
        assertTrue(result.getNewFiles().contains(FILE3_NAME));
        assertTrue(result.getUnchangedFiles().contains("four.txt"));
        assertEquals(1, result.getNewFiles().size());
        assertEquals(1, result.getOverwrittenFiles().size());
        assertEquals(1, result.getSkippedFiles().size());
        assertEquals(1, result.getUnchangedFiles().size());
    }

    @Test
    public void itShouldOnlyDeleteExistingFilesIfTheyreNotInTheZipAndTheOverwritingDeciderPermits() throws IOException {
        writeDirToZip("dir1/");
        writeDirToZip("dir1/nbproject/");
        writeFileToZip("dir1/one.txt", "one");
        zipOut.close();

        new File(tempDir.getRoot() + "/dest").mkdir();
        final File expectedPreservedFile = new File(tempDir.getRoot() + "/dest/one.txt");
        FileUtils.write(expectedPreservedFile, REMAINING);
        final File expectedDeletedFile = new File(tempDir.getRoot() + "/dest/two.txt");
        FileUtils.write(expectedDeletedFile, "This should be deleted");
        final File expectedNotDeletedFile = new File(tempDir.getRoot() + "/dest/three.txt");
        FileUtils.write(expectedNotDeletedFile, "This should not be deleted");

        final Unzipper.OverwritingDecider overwriting = mock(Unzipper.OverwritingDecider.class);
        when(overwriting.mayDelete(FILE2_NAME)).thenReturn(true);
        when(overwriting.mayDelete(FILE3_NAME)).thenReturn(false);

        final Unzipper unzipper = new Unzipper(overwriting);
        final Result result = unzipper.unzipProject(zipBuffer.toByteArray(), inTempDir("dest"));

        verify(overwriting).mayDelete(FILE2_NAME);
        verify(overwriting).mayDelete(FILE3_NAME);
        verify(overwriting, atLeast(0)).mayOverwrite(anyString());
        verifyNoMoreInteractions(overwriting);

        assertTrue(expectedPreservedFile.exists());
        assertFalse(expectedDeletedFile.exists());
        assertTrue(expectedNotDeletedFile.exists());

        assertTrue(result.getDeletedFiles().contains(FILE2_NAME));
        assertTrue(result.getSkippedDeletingFiles().contains(FILE3_NAME));
    }

    @Test
    public void itCanDeleteDeletedDirectories() throws IOException {
        writeDirToZip("dir1/");
        writeDirToZip("dir1/nbproject/");
        writeDirToZip("dir1/stuff/");
        writeDirToZip("dir1/stuff/remaining/");
        writeFileToZip("dir1/stuff/remaining/one.txt", "one");
        zipOut.close();

        new File(tempDir.getRoot() + "/dest").mkdir();
        new File(tempDir.getRoot() + "/dest/stuff").mkdir();
        new File(tempDir.getRoot() + "/dest/stuff/remaining").mkdir();
        new File(tempDir.getRoot() + "/dest/stuff/deleted").mkdir();
        new File(tempDir.getRoot() + "/dest/stuff/preserved").mkdir();
        final File expectedPreservedFile = new File(tempDir.getRoot() + "/dest/stuff/remaining/one.txt");
        FileUtils.write(expectedPreservedFile, REMAINING);
        final File expectedDeletedFile = new File(tempDir.getRoot() + "/dest/stuff/deleted/two.txt");
        FileUtils.write(expectedDeletedFile, "This should be deleted");
        final File expectedNotDeletedFile = new File(tempDir.getRoot() + "/dest/stuff/preserved/three.txt");
        FileUtils.write(expectedNotDeletedFile, "This should not be deleted");

        final Unzipper.OverwritingDecider overwriting = mock(Unzipper.OverwritingDecider.class);
        when(overwriting.mayDelete(GENERIC + fsep + DELETED + fsep + FILE2_NAME)).thenReturn(true);
        when(overwriting.mayDelete(GENERIC + fsep + DELETED)).thenReturn(true);
        when(overwriting.mayDelete(GENERIC + fsep + PRESERVED + fsep + FILE3_NAME)).thenReturn(false);
        when(overwriting.mayDelete(GENERIC + fsep + PRESERVED)).thenReturn(false);

        final Unzipper unzipper = new Unzipper(overwriting);
        final Result result = unzipper.unzipProject(zipBuffer.toByteArray(), inTempDir("dest"));

        verify(overwriting).mayDelete(GENERIC + fsep + DELETED + fsep + FILE2_NAME);
        verify(overwriting).mayDelete(GENERIC + fsep + DELETED);
        verify(overwriting).mayDelete(GENERIC + fsep + PRESERVED + fsep + FILE3_NAME);
        verify(overwriting).mayDelete(GENERIC + fsep + PRESERVED);

        assertTrue(expectedPreservedFile.exists());
        assertFalse(expectedDeletedFile.exists());
        assertFalse(expectedDeletedFile.getParentFile().exists());
        assertTrue(expectedNotDeletedFile.exists());

        assertTrue(result.getDeletedFiles().contains(GENERIC + fsep + DELETED));
        assertTrue(result.getDeletedFiles().contains(GENERIC + fsep + DELETED + fsep + FILE2_NAME));
        assertTrue(result.getSkippedDeletingFiles().contains(GENERIC + fsep + PRESERVED));
        assertTrue(result.getSkippedDeletingFiles().contains(GENERIC + fsep + PRESERVED + fsep + FILE3_NAME));
    }

    @Test
    public void itCanWorkInDryRunMode() throws IOException {
        writeDirToZip("dir1/");
        writeDirToZip("dir1/nbproject/");
        writeFileToZip("dir1/one.txt", "one");
        writeFileToZip("dir1/two.txt", "two");
        zipOut.close();

        new File(tempDir.getRoot() + "/dest").mkdir();
        new File(tempDir.getRoot() + "/dest/stuff").mkdir();
        final File file1 = new File(tempDir.getRoot() + "/dest/one.txt");
        FileUtils.write(file1, REMAINING);
        final File file2 = new File(tempDir.getRoot() + "/dest/two.txt");
        FileUtils.write(file2, "This would be overwritten if not in dry run mode");
        final File file3 = new File(tempDir.getRoot() + "/dest/stuff/three.txt");
        FileUtils.write(file3, "This would be deleted if not in dry run mode");

        final Unzipper.OverwritingDecider overwriting = mock(Unzipper.OverwritingDecider.class);
        when(overwriting.mayOverwrite(FILE1_NAME)).thenReturn(false);
        when(overwriting.mayOverwrite(FILE2_NAME)).thenReturn(true);
        when(overwriting.mayDelete(GENERIC + fsep + FILE3_NAME)).thenReturn(true);

        final Unzipper unzipper = new Unzipper(overwriting);
        final Result result = unzipper.unzipProject(zipBuffer.toByteArray(), inTempDir("dest"), false);

        verify(overwriting).mayOverwrite(FILE1_NAME);
        verify(overwriting).mayOverwrite(FILE2_NAME);
        verify(overwriting).mayDelete(GENERIC + fsep + FILE3_NAME);

        assertEquals(REMAINING, FileUtils.readFileToString(file1));
        assertEquals("This would be overwritten if not in dry run mode", FileUtils.readFileToString(file2));
        assertEquals("This would be deleted if not in dry run mode", FileUtils.readFileToString(file3));

        assertTrue(result.getSkippedFiles().contains(FILE1_NAME));
        assertTrue(result.getOverwrittenFiles().contains(FILE2_NAME));
        assertTrue(result.getDeletedFiles().contains(GENERIC + fsep + FILE3_NAME));
        assertEquals(1, result.getOverwrittenFiles().size());
        assertEquals(1, result.getSkippedFiles().size());
        assertEquals(1, result.getDeletedFiles().size());
    }
}
