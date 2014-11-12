package fi.helsinki.cs.tmc.client.core.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import fi.helsinki.cs.tmc.client.core.domain.Settings;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

public class HttpWorker {

    private URI uri;
    private Credentials credentials;
    private ObjectMapper mapper;
    private CloseableHttpAsyncClient client;

    public HttpWorker(final ObjectMapper mapper, final CloseableHttpAsyncClient client) {

        this.mapper = mapper;
        this.client = client;

        //TODO: Consider settings this globally somewhere else
        mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy());
    }

    public HttpExecutor get() {

        final HttpGet request = new HttpGet(uri);

        return new HttpExecutor(mapper, new CancellableHttpRequest(client, request, credentials));
    }

    public HttpExecutor post(final byte[] content) {

        final HttpEntity entity = new ByteArrayEntity(content);

        final HttpPost postRequest = new HttpPost(uri);
        postRequest.setEntity(entity);

        return new HttpExecutor(mapper, new CancellableHttpRequest(client, postRequest, credentials));

    }

    public HttpExecutor post(final String content) {

        return post(content.getBytes());

    }

    public HttpExecutor post(final Object content) throws JsonProcessingException {

        return post(mapper.writeValueAsString(content));

    }

    public HttpWorker from(final URI uri) {

        this.uri = uri;

        return this;
    }

    public HttpWorker to(final URI uri) {

        this.uri = uri;

        return this;
    }

    public HttpWorker withCredentials(final String username, final String password) {

        this.credentials = new UsernamePasswordCredentials(username, password);

        return this;
    }

    public HttpWorker withCredentials(final Credentials credentials) {

        this.credentials = credentials;

        return this;
    }

    public HttpWorker withCredentials(final Settings settings) {

        this.credentials = new UsernamePasswordCredentials(settings.getUsername(), settings.getPassword());

        return this;
    }

    public HttpWorker withParam(final String key, final String value) throws URISyntaxException {

        final URIBuilder builder = new URIBuilder(uri);
        builder.addParameter(key, value);

        this.uri = builder.build();

        return this;
    }
}
