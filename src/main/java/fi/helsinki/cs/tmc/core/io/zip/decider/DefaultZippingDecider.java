package fi.helsinki.cs.tmc.core.io.zip.decider;

/**
 * Default zipping decider that is used by Java Ant and C projects.
 */
public class DefaultZippingDecider extends AbstractZippingDecider {

    /**
     * zips extra student files and content of the src folder.
     */
    @Override
    public boolean isStudentFile(final String zipPath) {

        if (getProject().getExtraStudentFiles() != null && getProject().getExtraStudentFiles().contains(withoutRootDir(zipPath))) {
            return true;
        } else {
            return zipPath.contains("/src/");
        }

    }

    private String withoutRootDir(final String zipPath) {

        final int i = zipPath.indexOf('/');
        if (i != -1) {
            return zipPath.substring(i + 1);
        } else {
            return "";
        }
    }

}
