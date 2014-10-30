package fi.helsinki.cs.tmc.client.core.io.zip.decider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ZipEverythingDeciderTest {

    private ZipEverythingDecider decider;

    @Before
    public void setUp() throws Exception {

        decider = new ZipEverythingDecider();
    }

    @Test
    public void allowsZippingOfString() {

        assertTrue(decider.shouldZip("path"));
    }

    @Test
    public void allowsZippingOfNull() {

        assertTrue(decider.shouldZip(null));
    }
}
