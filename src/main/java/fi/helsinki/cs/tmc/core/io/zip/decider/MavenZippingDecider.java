package fi.helsinki.cs.tmc.core.io.zip.decider;

import java.util.regex.Pattern;

/**
 * Zipping decider for Maven projects.
 */
public class MavenZippingDecider extends AbstractZippingDecider {

    private static final Pattern REJECT_PATTERN = Pattern.compile("^[^/]+/(target|lib/testrunner)/.*");

    /**
     * Prevents zipping any files that matches the REJECT_PATTERN regex.
     */
    @Override
    public boolean isStudentFile(final String zipPath) {

        return !REJECT_PATTERN.matcher(zipPath).matches();
    }
}
