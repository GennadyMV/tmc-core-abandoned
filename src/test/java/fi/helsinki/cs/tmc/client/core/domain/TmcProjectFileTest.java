package fi.helsinki.cs.tmc.client.core.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class TmcProjectFileTest {

    private TmcProjectFile file;

    @Test
    public void canConstructWithoutParameters() {

        file = new TmcProjectFile();

        assertNotNull(file.getExtraStudentFiles());
        assertEquals(0, file.getExtraStudentFiles().size());
    }

    @Test
    public void canConstructWithList() {

        final List<String> files = new ArrayList<>();
        file = new TmcProjectFile(files);

        assertNotNull(file.getExtraStudentFiles());
        assertEquals(files, file.getExtraStudentFiles());
    }

    @Test
    public void canSetExtraStudentFiles() {

        final List<String> files = new ArrayList<>();
        file = new TmcProjectFile();

        file.setExtraStudentFiles(files);

        assertEquals(files, file.getExtraStudentFiles());
    }
}
