package fi.helsinki.cs.tmc.client.core.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ZippedProjectTest {

    private Zip zip;

    @Before
    public void setUp() {

        zip = new Zip();
    }

    @Test
    public void canSetAndGetBytes() {

        final byte[] bytes = { 1, 0, 1 };
        zip.setBytes(bytes);
        assertEquals(bytes, zip.getBytes());
    }
}
