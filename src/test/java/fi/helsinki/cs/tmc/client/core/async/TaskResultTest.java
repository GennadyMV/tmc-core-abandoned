package fi.helsinki.cs.tmc.client.core.async;

import fi.helsinki.cs.tmc.client.core.async.exception.TaskFailureException;

import org.junit.Test;

import static org.junit.Assert.*;


public class TaskResultTest {

    private static final String RESULT = "result";

    private TaskResult<String> result;

    @Test
    public void constructorWithoutStatusDefaultsToSuccess() {

        result = new TaskResult<String>(RESULT);

        assertEquals(RESULT, result.result());
        assertEquals(TaskResult.Status.SUCCESS, result.status());
        assertNull(result.exception());
    }

    @Test
    public void constructorWithStatusUsesProvidedStatus() {

        result = new TaskResult<String>(TaskResult.Status.FAILURE, RESULT);

        assertEquals(RESULT, result.result());
        assertEquals(TaskResult.Status.FAILURE, result.status());
        assertNull(result.exception());
    }

    @Test
    public void constructorWithExceptionProvidesException() {

        result = new TaskResult<String>(TaskResult.Status.FAILURE, RESULT, new TaskFailureException("expected"));

        assertEquals(RESULT, result.result());
        assertEquals(TaskResult.Status.FAILURE, result.status());
        assertEquals("expected", result.exception().getMessage());
    }

}
