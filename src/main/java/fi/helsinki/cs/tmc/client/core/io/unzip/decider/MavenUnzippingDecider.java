package fi.helsinki.cs.tmc.client.core.io.unzip.decider;

import fi.helsinki.cs.tmc.client.core.io.FileIO;

/**
 * Unzipping decider for Maven projects.
 */
public class MavenUnzippingDecider extends AbstractUnzippingDecider {

    /**
     * Prevents overwriting files in /src/main folder when unzipping so that
     * changes made by the user will not be lost.
     */
    @Override
    public boolean isProtectedFile(final String filePath) {

        final String srcRoot = getProject().getRootPath() + "/src/main";
        
        if (filePath.startsWith(srcRoot) && (filePath.equals(srcRoot) || filePath.charAt(srcRoot.length()) == '/')) {
            
            final FileIO file = new FileIO(filePath);
            
            return !file.fileExists();
        }
        
        return false;
    }

}
