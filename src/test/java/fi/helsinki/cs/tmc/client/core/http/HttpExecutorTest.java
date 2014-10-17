package fi.helsinki.cs.tmc.client.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.auth.AuthenticationException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class HttpExecutorTest {

    private HttpExecutor executor;
    private CancellableHttpRequest request;

    @Before
    public void setUp() {

        request = mock(CancellableHttpRequest.class);
        executor = new HttpExecutor(new ObjectMapper(), request);
    }

    @Test(expected = IOException.class)
    public void withoutResponseHandlesAuthenticationException() throws AuthenticationException, IOException, InterruptedException, ExecutionException {

        when(request.execute()).thenThrow(new AuthenticationException());

        executor.withoutResponse();
    }

    @Test(expected = IOException.class)
    public void withoutResponseHandlesInterruptedException() throws AuthenticationException, IOException, InterruptedException, ExecutionException {

        when(request.execute()).thenThrow(new InterruptedException());

        executor.withoutResponse();
    }

    @Test(expected = IOException.class)
    public void withoutResponseHandlesExecutionException() throws AuthenticationException, IOException, InterruptedException, ExecutionException {

        when(request.execute()).thenThrow(new ExecutionException(new IOException()));

        executor.withoutResponse();
    }

    @Test(expected = IOException.class)
    public void withResponseHandlesAuthenticationException() throws AuthenticationException, IOException, InterruptedException, ExecutionException {

        when(request.execute()).thenThrow(new AuthenticationException());

        executor.withResponse();
    }

    @Test(expected = IOException.class)
    public void withResponseHandlesInterruptedException() throws AuthenticationException, IOException, InterruptedException, ExecutionException {

        when(request.execute()).thenThrow(new InterruptedException());

        executor.withResponse();
    }

    @Test(expected = IOException.class)
    public void withResponseHandlesExecutionException() throws AuthenticationException, IOException, InterruptedException, ExecutionException {

        when(request.execute()).thenThrow(new ExecutionException(new IOException()));

        executor.withResponse();
    }

}
