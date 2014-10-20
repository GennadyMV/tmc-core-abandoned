package fi.helsinki.cs.tmc.client.core.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ZipTest {

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

    @Test
    public void settingBytesSetsLength() {

        final byte[] bytes = { 1, 0, 1 };
        zip.setBytes(bytes);
        assertEquals(3, zip.length());
    }

    @Test
    public void zeroLengthIfNoBytes() {

        zip.setBytes(null);
        assertEquals(0, zip.length());
    }
}
