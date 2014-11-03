package fi.helsinki.cs.tmc.client.core.testrunner.json.mixin;

import org.junit.Test;

/**
 * These tests provide test coverage for Jackson Mixins.
 * It's assumed that each mixin is already tested by other classes.
 */
public class MixinTest {

    /**
     * Tested by TmcResultFileReaderTest.
     */
    @Test
    public void testCaseMixin() {

        new TestCaseMixin("", "", null) { };
    }
}
