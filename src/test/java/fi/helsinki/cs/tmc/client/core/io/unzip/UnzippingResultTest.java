package fi.helsinki.cs.tmc.client.core.io.unzip;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnzippingResultTest {

    private UnzippingResult result;

    @Before
    public void setUp() {

        result = new UnzippingResult(null);
    }

    @Test
    public void constructorSetsProjectDirectory() {

        final File root = new File("rootDir");
        result = new UnzippingResult(root);

        assertEquals(root, result.getProjectDirectory());
    }

    @Test
    public void canSetProjectDirectory() {

        final File root = new File("rootDir");
        result.setProjectDirectory(root);

        assertEquals(root, result.getProjectDirectory());
    }

    @Test
    public void canSetNewFiles() {

        final List<String> newFiles = new ArrayList<>();
        result.setNewFiles(newFiles);

        assertEquals(newFiles, result.getNewFiles());
    }

    @Test
    public void canSetOverwrittenFiles() {

        final List<String> overwrittenFiles = new ArrayList<>();
        result.setOverwrittenFiles(overwrittenFiles);

        assertEquals(overwrittenFiles, result.getOverwrittenFiles());
    }

    @Test
    public void canSetSkippedFiles() {

        final List<String> skippedFiles = new ArrayList<>();
        result.setSkippedFiles(skippedFiles);

        assertEquals(skippedFiles, result.getSkippedFiles());
    }

    @Test
    public void canSetUnchangedFiles() {

        final List<String> unchangedFiles = new ArrayList<>();
        result.setUnchangedFiles(unchangedFiles);

        assertEquals(unchangedFiles, result.getUnchangedFiles());
    }

    @Test
    public void canSetDeletedFiles() {

        final List<String> deletedFiles = new ArrayList<>();
        result.setDeletedFiles(deletedFiles);

        assertEquals(deletedFiles, result.getDeletedFiles());
    }

    @Test
    public void canSetSkippedDeletingFiles() {

        final List<String> skippedDeletingFiles = new ArrayList<>();
        result.setSkippedDeletingFiles(skippedDeletingFiles);

        assertEquals(skippedDeletingFiles, result.getSkippedDeletingFiles());
    }

    @Test
    public void nothingIsNullAfterConstruction() {

        assertNotNull(result.getDeletedFiles());
        assertNotNull(result.getNewFiles());
        assertNotNull(result.getOverwrittenFiles());
        assertNotNull(result.getSkippedDeletingFiles());
        assertNotNull(result.getSkippedFiles());
        assertNotNull(result.getUnchangedFiles());
    }

    @Test
    public void toStringPrintsStatisticsAboutAllFileSets() {

        result.setNewFiles(Arrays.asList("newFile"));
        result.setOverwrittenFiles(Arrays.asList("overwrittenFile"));
        result.setSkippedFiles(Arrays.asList("skippedFile"));
        result.setUnchangedFiles(Arrays.asList("unchangedFile"));
        result.setDeletedFiles(Arrays.asList("deletedFile"));
        result.setSkippedDeletingFiles(Arrays.asList("skippedDeletedFile"));

        final String output = result.toString();

        assertTrue(output.contains("New: [newFile]"));
        assertTrue(output.contains("Overwritten: [overwrittenFile]"));
        assertTrue(output.contains("Skipped: [skippedFile]"));
        assertTrue(output.contains("Unchanged: [unchangedFile]"));
        assertTrue(output.contains("Deleted: [deletedFile]"));
        assertTrue(output.contains("Not deleted: [deletedFile]"));
    }
}
