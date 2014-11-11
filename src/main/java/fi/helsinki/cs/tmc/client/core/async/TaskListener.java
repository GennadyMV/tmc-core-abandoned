package fi.helsinki.cs.tmc.client.core.async;

public interface TaskListener {

    void onSuccess(final TaskResult<?> result);
    void onFailure(final TaskResult<?> result);
    void onInterrupt(final TaskResult<?> result);
}
