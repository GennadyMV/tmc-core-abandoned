package fi.helsinki.cs.tmc.core.async;


public interface Task<S> extends Runnable {
    
    String getDescription();
    TaskMonitor getProgressMonitor();
    
    @Override
    void run();
}
