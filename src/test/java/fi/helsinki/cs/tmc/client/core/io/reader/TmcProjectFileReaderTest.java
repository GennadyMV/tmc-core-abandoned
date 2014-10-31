package fi.helsinki.cs.tmc.client.core.io.reader;

import fi.helsinki.cs.tmc.client.core.domain.TmcProjectFile;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TmcProjectFileReaderTest {

    private TmcProjectFileReader reader;

    @Before
    public void setUp() {

        reader = new TmcProjectFileReader();
    }

    @Test
    public void setsStudentFiles() {

        final File file = getFileFromResources("tmcprojectfiles/has_student_files.yml");

        final TmcProjectFile projectFile = reader.read(file);

        assertEquals(2, projectFile.getExtraStudentFiles().size());
        assertTrue(projectFile.getExtraStudentFiles().contains("test/StudentTest.java"));
        assertTrue(projectFile.getExtraStudentFiles().contains("test/foo/StudentTest.java"));
    }

    @Test
    public void handlesFileWithNoStudentFilesButOtherStuff() {

        final File file = getFileFromResources("tmcprojectfiles/has_other_stuff.yml");

        final TmcProjectFile projectFile = reader.read(file);

        assertEquals(0, projectFile.getExtraStudentFiles().size());
    }

    @Test
    public void handlesEmptyFile() {

        final File file = getFileFromResources("tmcprojectfiles/empty.yml");

        final TmcProjectFile projectFile = reader.read(file);

        assertEquals(0, projectFile.getExtraStudentFiles().size());
    }

    @Test
    public void returnsOnNonexistantFile() {

        final File folder = getFileFromResources("tmcprojectfiles");
        final File file = new File(folder, "no_such_file.yml");

        assertFalse("The path used for testing with nonexisting files contains an existing file! Path: " + file.getAbsolutePath(), file.exists());

        final TmcProjectFile projectFile = reader.read(file);

        assertNotNull(projectFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnFolder() {

        final File folder = getFileFromResources("tmcprojectfiles");
        reader.read(folder);
    }

    @Test
    public void handlesFileWithListWithoutContent() {

        final File file = getFileFromResources("tmcprojectfiles/empty_list.yml");

        final TmcProjectFile projectFile = reader.read(file);

        assertEquals(0, projectFile.getExtraStudentFiles().size());
    }

    @Test
    public void doesNotAddIntsToStudentFiles() {

        final File file = getFileFromResources("tmcprojectfiles/has_student_files_mixed_with_ints.yml");

        final TmcProjectFile projectFile = reader.read(file);

        assertEquals(2, projectFile.getExtraStudentFiles().size());
        assertTrue(projectFile.getExtraStudentFiles().contains("test/StudentTest.java"));
        assertTrue(projectFile.getExtraStudentFiles().contains("test/foo/StudentTest.java"));
    }

    private File getFileFromResources(final String path) {

        final String absolutePath = getClass().getClassLoader().getResource(path).getFile();

        return new File(absolutePath);
    }

}
