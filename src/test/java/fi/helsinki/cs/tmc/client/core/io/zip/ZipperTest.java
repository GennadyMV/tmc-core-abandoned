package fi.helsinki.cs.tmc.client.core.io.zip;

import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZipEverythingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZippingDecider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class ZipperTest {

    private static final String DS = File.separator;

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private String rootDirectory;

    @Before
    public void setUp() throws IOException {

        rootDirectory = temporaryDirectory.getRoot().getAbsolutePath() + DS + "MyExercise";
        new File(rootDirectory + DS + "src" + DS + "subdir").mkdirs();
        new File(rootDirectory + DS + "src" + DS + "Included1.txt").createNewFile();
        new File(rootDirectory + DS + "src" + DS + "subdir" + DS + "Included2.txt").createNewFile();

        new File(rootDirectory + DS + "Excluded.txt").createNewFile();
        new File(rootDirectory + DS + "test").mkdir();
        new File(rootDirectory + DS + "test" + DS + "Excluded.txt").createNewFile();
        new File(rootDirectory + DS + "test").mkdir();
        new File(rootDirectory + DS + "excluded").mkdir();
        new File(rootDirectory + DS + "excluded" + DS + "Foo.txt").createNewFile();
    }

    @Test(expected = IOException.class)
    public void failsOnNonexistantRootDirectory() throws IOException {

        final Zipper zipper = new Zipper(new File(rootDirectory + DS + "noSuchFolder"), new ZipEverythingDecider());
        zipper.zipProjectSources();
    }

    @Test(expected = IOException.class)
    public void failsOnNonDirectoryRoot() throws IOException {

        final File file = temporaryDirectory.newFile();

        final Zipper zipper = new Zipper(file, new ZipEverythingDecider());
        zipper.zipProjectSources();
    }

    @Test
    public void itShouldZipRecursivelyFilesThatZipDeciderSelects() throws IOException {

        final ZippingDecider decider = new ZippingDecider() {
            @Override
            public boolean shouldZip(final String relativeZipPath) {
                return !relativeZipPath.equals("MyExercise/Excluded.txt") &&
                        !relativeZipPath.equals("MyExercise/src/Excluded.txt") &&
                        !relativeZipPath.equals("MyExercise/excluded/");
            }
        };

        final List<String> entries = getZipEntries(decider);

        if (!entries.contains("MyExercise/src/Included1.txt")) {
            fail("Expected file not in zip.");
        }
        if (!entries.contains("MyExercise/src/subdir/Included2.txt")) {
            fail("Expected file not in zip.");
        }
        if (entries.contains("MyExercise/Excluded.txt")) {
            fail("File that was supposed to be excluded was found in the zip.");
        }
        if (entries.contains("MyExercise/src/Excluded.txt")) {
            fail("File that was supposed to be excluded was found in the zip.");
        }
        if (entries.contains("MyExercise/excluded/Foo.txt")) {
            fail("File that was supposed to be excluded was found in the zip.");
        }
    }

    private List<String> getZipEntries(final ZippingDecider decider) throws IOException {

        final Zipper zipper = new Zipper(new File(rootDirectory), decider);
        final byte[] zipData = zipper.zipProjectSources();

        return zipEntryNames(zipData);
    }

    private List<String> zipEntryNames(final byte[] zipData) throws IOException {

        final List<String> result = new ArrayList<>();

        final ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipData));
        try {

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                result.add(zipEntry.getName());
            }

        } finally {
            zipInputStream.close();
        }

        return result;
    }
}
