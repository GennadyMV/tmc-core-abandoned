package fi.helsinki.cs.tmc.core.async.listener;

import fi.helsinki.cs.tmc.core.async.TaskListener;
import fi.helsinki.cs.tmc.core.async.TaskResult;

public abstract class AbstractTaskListener implements TaskListener {
       
    @Override
    public abstract void onStart();

    @Override
    public abstract void onSuccess(TaskResult<? extends Object> result);

    @Override
    public abstract void onFailure(TaskResult<? extends Object> result);

    @Override
    public abstract void onInterrupt(TaskResult<? extends Object> result);
    
    @Override
    public abstract void onEnd(TaskResult<? extends Object> result);
}
