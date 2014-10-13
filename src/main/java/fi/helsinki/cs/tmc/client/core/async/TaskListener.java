package fi.helsinki.cs.tmc.client.core.async;


public interface TaskListener {
    
    void onStart();
    void onEnd(TaskResult<? extends Object> result);
    
    void onSuccess(TaskResult<? extends Object> result);
    void onFailure(TaskResult<? extends Object> result);
    void onInterrupt(TaskResult<? extends Object> result);
    
}
