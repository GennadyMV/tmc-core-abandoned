package fi.helsinki.cs.tmc.client.core.io.unzip.decider;


public interface OverwritingDecider {

    /**
     * Decides whether the given relative path in the project may be overwritten.
     *
     * <p>
     * Only called for files (not directories) whose content has changed.
     *
     * <p>
     * Note that the given path has platform-specific directory separators.
     */
    boolean mayOverwrite(String relativePath);

    /**
     * Decides whether the given relative path in the project may be deleted.
     *
     * <p>
     * Only called for files and directories that are on disk but not in the zip.
     *
     * <p>
     * Note that the given path has platform-specific directory separators.
     */
    boolean mayDelete(String relativePath);

}
