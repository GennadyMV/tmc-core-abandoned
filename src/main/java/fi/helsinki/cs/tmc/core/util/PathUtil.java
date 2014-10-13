package fi.helsinki.cs.tmc.core.util;

import java.io.File;

/**
 * Helper class for path operations.
 */
public class PathUtil {

    public static String append(final String a, final String b) {

        return getUnixPath(a) + "/" + getUnixPath(b);
    }

    /**
     * Replaces the file separators in given path with Unix styled separators.
     * We use these internally.
     */
    public static String getUnixPath(final String path) {

        String unixPath = path.replace(File.separator, "/");

        if (!unixPath.isEmpty() && unixPath.charAt(unixPath.length() - 1) == '/') {
            unixPath = unixPath.substring(0, unixPath.length() - 1);
        }

        return unixPath;
    }
    
    public static String getUnixPath(final File file) {
        
        return getUnixPath(file.getPath());
    }

    public static String getNativePath(final String path) {

        return getNativePath(new File(path));
    }
    
    public static String getNativePath(final File file) {
        
        return file.getAbsolutePath();
    }

}
