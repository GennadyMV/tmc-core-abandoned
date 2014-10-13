package fi.helsinki.cs.tmc.core.async;

import fi.helsinki.cs.tmc.core.async.exception.TaskFailureException;


public class TaskResult<S> {
    
    public enum Status {

        SUCCESS, FAILURE, INTERRUPTED;
    }

    private final TaskResult.Status resultStatus;
    private final S resultObject;
    private final TaskFailureException exception;
    
    public TaskResult(final TaskResult.Status resultStatus, final S s, final TaskFailureException exception) {
        
        this.resultStatus = resultStatus;
        this.resultObject = s;
        this.exception = exception;
    }
    
    public TaskResult(final TaskResult.Status resultStatus, final S s) {
        
        this(resultStatus, s, null);
    }
    
    public TaskResult(final S s) {
        
        this(TaskResult.Status.SUCCESS, s, null);
    }
    
    public TaskResult.Status status() {
        
        return resultStatus;
    }
    
    public S result() {
        
        return resultObject;
    }
    
    public TaskFailureException exception() {
        
        return exception;
    }

}
