package fi.helsinki.cs.tmc.client.core.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CancellableHttpRequest {

    private static final Logger LOG = LogManager.getLogger();
    private static final int SLEEP_STEP = 1000;

    private Credentials credentials;
    private HttpUriRequest request;
    private CloseableHttpAsyncClient client;

    public CancellableHttpRequest(final CloseableHttpAsyncClient client, final HttpUriRequest uriRequest, final Credentials credentials) {

        this.request = uriRequest;
        this.credentials = credentials;
        this.client = client;
    }

    public BufferedHttpEntity execute() throws AuthenticationException, IOException, InterruptedException, ExecutionException {

        client.start();

        LOG.info("Making " + request.getMethod() + " request to " + request.getURI().toString());

        if (this.credentials != null) {
            request.addHeader(new BasicScheme(Charset.forName("UTF-8")).authenticate(this.credentials, request, null));
        }

        final Future<HttpResponse> futureResponse = client.execute(request, null);

        while (!futureResponse.isDone()) {
            try {
                Thread.sleep(SLEEP_STEP);
            } catch (InterruptedException exception) {
                LOG.info("HTTP Request to " + request.getURI().toString() + " was cancelled, closing client");
                futureResponse.cancel(true);
                client.close();

                throw exception;
            }
        }

        final HttpResponse response = futureResponse.get();

        client.close();

        assertServerRespondedWithStatusCode2xx(response);

        return new BufferedHttpEntity(response.getEntity());
    }


    private void assertServerRespondedWithStatusCode2xx(final HttpResponse response) throws HttpResponseException {

        final int statusCode = response.getStatusLine().getStatusCode();
        if (!(statusCode >= 200 && statusCode < 300)) {
            throw new HttpResponseException(statusCode, "Expected response code 2xx, actual response was " + statusCode);
        }
    }
}
