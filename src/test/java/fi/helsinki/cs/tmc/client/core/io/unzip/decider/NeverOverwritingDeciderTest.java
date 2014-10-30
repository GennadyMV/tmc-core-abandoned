package fi.helsinki.cs.tmc.client.core.io.unzip.decider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NeverOverwritingDeciderTest {

    private NeverOverwritingDecider decider;

    @Before
    public void setUp() throws Exception {

        decider = new NeverOverwritingDecider();
    }

    @Test
    public void disallowDeletionOfAnyString() {

        assertFalse(decider.mayDelete("path"));
    }

    @Test
    public void disallowDeletionOfNull() {

        assertFalse(decider.mayDelete(null));
    }

    @Test
    public void disallowOverwritingOfAnyString() {

        assertFalse(decider.mayOverwrite("path"));
    }

    @Test
    public void disallowOverwritingOfNull() {

        assertFalse(decider.mayOverwrite(null));
    }
}
