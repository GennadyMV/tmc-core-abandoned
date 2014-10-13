package fi.helsinki.cs.tmc.core.async.listener;

import fi.helsinki.cs.tmc.core.async.TaskListener;
import fi.helsinki.cs.tmc.core.async.TaskResult;
import fi.helsinki.cs.tmc.core.ui.UIInvoker;

public abstract class AbstractTaskListener implements TaskListener {
    
    private UIInvoker uiInvoker;
    
    public AbstractTaskListener(final UIInvoker uiInvoker) {

        this.uiInvoker = uiInvoker;
    }
    
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
    
    public UIInvoker getUIInvoker() {
        
        return this.uiInvoker;
    }
}
