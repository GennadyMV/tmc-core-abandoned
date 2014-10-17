package fi.helsinki.cs.tmc.client.core.http;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.*;

public class CancellableHttpRequestTest {
    
    private CancellableHttpRequest request;
    private CloseableHttpAsyncClient client;
    private HttpUriRequest uriRequest;
    private Credentials credentials;

    @Before
    public void setUp() throws Exception {
        
        this.client = HttpClientFactory.makeHttpClient();
        this.uriRequest = new HttpGet(new URI("http://localhost:8089"));
        this.credentials = new UsernamePasswordCredentials("user", "pass");

        this.request = new CancellableHttpRequest(client, uriRequest, credentials);
    }
    
    private class TestRunnable implements Runnable {
        
        private CancellableHttpRequest request;
        private Exception exception;
        
        public TestRunnable(final CancellableHttpRequest request) {
            this.request = request;
        }

        @Override
        public void run() {

            try {
                this.request.execute();
            } catch (AuthenticationException | IOException | InterruptedException | ExecutionException exception) {
                this.exception = exception;
            }
            
        }
        
        public Exception getException() {
            return this.exception;
        }
    }

    @Test
    public void canBeCancelled() throws InterruptedException, IOException, ExecutionException {
        
        client = mock(CloseableHttpAsyncClient.class);
        final Future<HttpResponse> futureResponse = mock(Future.class);
        
        when(client.execute(uriRequest, null)).thenReturn(futureResponse);
        when(futureResponse.get()).then(new Answer<HttpResponse>() {

            @Override
            public HttpResponse answer(final InvocationOnMock invocation) throws Throwable {

                //Never return anything.
                while (true) {
                    Thread.sleep(1000);
                }
            }
            
        });
        
        request = new CancellableHttpRequest(client, uriRequest, credentials);
        final TestRunnable testRunnable = new TestRunnable(request);
        
        final Future<?> job = Executors.newSingleThreadExecutor().submit(testRunnable);
        
        Thread.sleep(500);
        
        job.cancel(true);
        
        while (!job.isDone()) {
            Thread.sleep(1000);
        }
        
        Thread.sleep(1000);
        
        assertNotNull(testRunnable.getException());
                
        verify(client).start();
        verify(client).close();
        
    }

}
