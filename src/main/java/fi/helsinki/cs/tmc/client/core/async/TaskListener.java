package fi.helsinki.cs.tmc.client.core.async;

public interface TaskListener {

    void onSuccess(final TaskResult<? extends Object> result);
    void onFailure(final TaskResult<? extends Object> result);
    void onInterrupt(final TaskResult<? extends Object> result);
}
