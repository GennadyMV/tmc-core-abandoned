package fi.helsinki.cs.tmc.core.async.task;

import fi.helsinki.cs.tmc.core.async.Task;
import fi.helsinki.cs.tmc.core.async.TaskListener;
import fi.helsinki.cs.tmc.core.async.TaskMonitor;
import fi.helsinki.cs.tmc.core.async.TaskResult;
import fi.helsinki.cs.tmc.core.async.exception.TaskFailureException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractTask<S> implements Task<S> {
    
    private static final Logger LOG = LogManager.getLogger();
    
    private String description;
    private TaskListener listener;
    private TaskMonitor monitor;
    
    public AbstractTask(final String description, final TaskListener listener, final TaskMonitor taskProgressMonitor) {
        
        this.description = description;
        this.listener = listener;
        this.monitor = taskProgressMonitor;
    }
    
    @Override
    public void run() {
        
        monitor.start();        
        listener.onStart();
        
        TaskResult<S> result; 
        try {
            
            result = work();
            
            listener.onSuccess(result);
            
        } catch (InterruptedException exception) {
            
            LOG.info("Backround task " + description + " was interrupted.", exception);
            
            result = new TaskResult<S>(TaskResult.Status.INTERRUPTED, null);
            
            cleanUp();
            
            listener.onInterrupt(result);
            
        } catch (TaskFailureException exception) {
            
            LOG.error("Background task" + description + " failed.", exception);
            
            result = new TaskResult<S>(TaskResult.Status.FAILURE, null, exception);
            
            cleanUp();
            
            listener.onFailure(result);

        }
        
        listener.onEnd(result);
    }
    
    @Override
    public String getDescription() {
        
        return description;
    }
    
    @Override
    public TaskMonitor getProgressMonitor() {
        
        return monitor;
    }
    
    protected abstract TaskResult<S> work() throws InterruptedException, TaskFailureException;
    
    protected abstract void cleanUp();
    
    protected void checkForInterrupt() throws InterruptedException {
        
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Task interrupted from outside");
        }
    }
}
