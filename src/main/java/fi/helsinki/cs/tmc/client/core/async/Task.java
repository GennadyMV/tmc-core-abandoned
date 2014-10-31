package fi.helsinki.cs.tmc.client.core.async;

public interface Task<S> extends Runnable {

    String getDescription();

    void addProgressListener(TaskProgressListener listener);

    boolean removeProgressListener(TaskProgressListener listener);

    @Override
    void run();
}
