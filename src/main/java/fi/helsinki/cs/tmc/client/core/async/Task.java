package fi.helsinki.cs.tmc.client.core.async;


public interface Task<S> extends Runnable {

    String getDescription();
    TaskMonitor getProgressMonitor();

    @Override
    void run();
}
