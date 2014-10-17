package fi.helsinki.cs.tmc.client.core.async.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskFailureExceptionTest {
    
    private TaskFailureException ex;
    
    @Test
    public void canConstructWithoutArgs() {
        
        ex = new TaskFailureException();
    }

    @Test
    public void canConstructWithMessage() {
        
        ex = new TaskFailureException("message");
        
        assertEquals("message", ex.getMessage());
    }
    
    @Test
    public void canConstructWithCause() {
        
        final Throwable cause = new Throwable();
        ex = new TaskFailureException(cause);
        
        assertEquals(cause, ex.getCause());
    }
    
    @Test
    public void canConstructWithMessageAndCause() {
        
        final Throwable cause = new Throwable();
        ex = new TaskFailureException("message", cause);
        
        assertEquals("message", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
