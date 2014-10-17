package fi.helsinki.cs.tmc.client.core.exception.visible;

import org.junit.Test;

import static org.junit.Assert.*;

public class InvalidProjectExceptionTest {
    
    private InvalidProjectException ex;
    
    @Test
    public void canConstructWithoutArgs() {
        
        ex = new InvalidProjectException();
    }

    @Test
    public void canConstructWithMessage() {
        
        ex = new InvalidProjectException("message");
        
        assertEquals("message", ex.getMessage());
    }
    
    @Test
    public void canConstructWithCause() {
        
        final Throwable cause = new Throwable();
        ex = new InvalidProjectException(cause);
        
        assertEquals(cause, ex.getCause());
    }
    
    @Test
    public void canConstructWithMessageAndCause() {
        
        final Throwable cause = new Throwable();
        ex = new InvalidProjectException("message", cause);
        
        assertEquals("message", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
