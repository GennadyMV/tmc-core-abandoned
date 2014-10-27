package fi.helsinki.cs.tmc.client.core.io.zip.decider;


public interface ZippingDecider {
    
    /**
     * Tells whether the given file or directory should be zipped.
     * 
     * Zip paths are separated by slashes and don't have a starting slash.
     * Directory paths always end in a slash.
     */
    public boolean shouldZip(String zipPath);

}
