package fi.helsinki.cs.tmc.client.core.io.zip.decider;

import fi.helsinki.cs.tmc.client.core.domain.Project;

import java.util.regex.Pattern;

public class MavenZippingDecider extends AbstractZippingDecider {

    private static final Pattern REJECT_PATTERN = Pattern.compile("^[^/]+/(target|lib/testrunner)/.*");

    public MavenZippingDecider(final Project project) {

        super(project);
    }

    @Override
    public boolean shouldZip(final String path) {

        if (!super.shouldZip(path)) {
            return false;
        }

        return !REJECT_PATTERN.matcher(path).matches();
    }
}
