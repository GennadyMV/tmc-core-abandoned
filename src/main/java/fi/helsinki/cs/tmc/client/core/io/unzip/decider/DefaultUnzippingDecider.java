package fi.helsinki.cs.tmc.client.core.io.unzip.decider;

import fi.helsinki.cs.tmc.client.core.io.FileIO;

/**
 * Unzipping decider for java ant and C projects.
 */
public class DefaultUnzippingDecider extends AbstractUnzippingDecider {

    /**
     * Prevents overwriting files in /src folder when unzipping so that changes
     * made by the user will not be lost.
     */
    @Override
    public boolean isProtectedFile(final String filePath) {

        final String srcRoot = getProject().getRootPath() + "/src";

        if (filePath.startsWith(srcRoot) && (filePath.equals(srcRoot) || filePath.charAt(srcRoot.length()) == '/')) {

            final FileIO file = new FileIO(filePath);

            return !(file.fileExists());
        }

        return false;
    }

}
