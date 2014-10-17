package fi.helsinki.cs.tmc.client.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpExecutor {
    
    private static final Logger LOG = LogManager.getLogger();
    
    private CancellableHttpRequest httpRequest;
    private ObjectMapper mapper;
    
    public HttpExecutor(final ObjectMapper mapper, final CancellableHttpRequest httpRequest) {

        this.mapper = mapper;
        this.httpRequest = httpRequest;
    }
    
    public void withoutResponse() throws IOException {
        
        try {
            httpRequest.execute();
        } catch (AuthenticationException | InterruptedException | ExecutionException exception) {
            LOG.error("Failed to execute HTTPRequest", exception);
            throw new IOException(exception);
        }
    }
    
    public HttpResponseParser withResponse() throws IOException {
        
        final BufferedHttpEntity response;
        try {
            response = httpRequest.execute();
        } catch (AuthenticationException | InterruptedException | ExecutionException exception) {
            LOG.error("Failed to execute HTTPRequest", exception);
            throw new IOException(exception);
        }
        
        EntityUtils.consume(response);
        
        return new HttpResponseParser(mapper, response);
    }   
}
