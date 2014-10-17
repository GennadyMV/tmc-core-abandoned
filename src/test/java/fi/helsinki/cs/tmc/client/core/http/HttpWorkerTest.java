package fi.helsinki.cs.tmc.client.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fi.helsinki.cs.tmc.client.core.Settings;
import fi.helsinki.cs.tmc.client.core.stub.StubSettings;
import fi.helsinki.cs.tmc.client.core.testutil.SimpleObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class HttpWorkerTest {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(8089);

    private static final String ANY_URL = ".*";
    private static final String RELATIVE_URL = "/hy";

    private static final String USER = "username";
    private static final String PASS = "password";
    private static final String BASIC_AUTH_HEADER_KEY = "Authorization";
    private static final String BASIC_AUTH_HEADER_VALUE = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";

    private static final String MESSAGE = "hi";

    private HttpWorker http;
    private URI rootURI;

    @Before
    public void setUp() throws URISyntaxException {

        WireMock.reset();

        http = new HttpWorker(new ObjectMapper(), HttpClientFactory.makeHttpClient());
        rootURI = new URI("http://localhost:8089/hy");

        stubFor(get(urlMatching(ANY_URL))
                .willReturn(aResponse().withStatus(200)));
        stubFor(post(urlMatching(ANY_URL))
                .willReturn(aResponse().withStatus(200)));
    }

    @Test
    public void makesCorrectGetRequestWithManualStringCredentials() throws IOException, URISyntaxException {

        http.from(rootURI).withCredentials(USER, PASS).get().withoutResponse();

        verify(getRequestedFor(urlEqualTo(RELATIVE_URL))
                .withHeader(BASIC_AUTH_HEADER_KEY, equalTo(BASIC_AUTH_HEADER_VALUE)));
    }

    @Test
    public void makesCorrectGetRequestWithManualObjectCredentials() throws IOException, URISyntaxException {

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(USER, PASS);
        http.from(rootURI).withCredentials(credentials).get().withoutResponse();

        verify(getRequestedFor(urlEqualTo(RELATIVE_URL))
                .withHeader(BASIC_AUTH_HEADER_KEY, equalTo(BASIC_AUTH_HEADER_VALUE)));
    }

    @Test
    public void makesCorrectGetRequestWithCredentialsFromSettings() throws IOException, URISyntaxException {

        final Settings settings = new CredSettings();
        http.from(rootURI).withCredentials(settings).get().withoutResponse();

        verify(getRequestedFor(urlEqualTo(RELATIVE_URL))
                .withHeader(BASIC_AUTH_HEADER_KEY, equalTo(BASIC_AUTH_HEADER_VALUE)));
    }

    @Test
    public void makesCorrectPostRequestWithManualStringCredentials() throws IOException, URISyntaxException {

        http.to(rootURI).withCredentials(USER, PASS).post(MESSAGE).withoutResponse();

        verify(postRequestedFor(urlEqualTo(RELATIVE_URL))
                .withHeader(BASIC_AUTH_HEADER_KEY, equalTo(BASIC_AUTH_HEADER_VALUE))
                .withRequestBody(matching(MESSAGE)));
    }

    @Test
    public void makesCorrectPostRequestWithManualObjectCredentials() throws IOException, URISyntaxException {

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(USER, PASS);
        http.to(rootURI).withCredentials(credentials).post(MESSAGE).withoutResponse();

        verify(postRequestedFor(urlEqualTo(RELATIVE_URL))
                .withHeader(BASIC_AUTH_HEADER_KEY, equalTo(BASIC_AUTH_HEADER_VALUE))
                .withRequestBody(matching(MESSAGE)));
    }

    @Test
    public void makesCorrectPostRequestWithCredentialsFromSettings() throws IOException, URISyntaxException {

        final Settings settings = new CredSettings();
        http.to(rootURI).withCredentials(settings).post(MESSAGE).withoutResponse();

        verify(postRequestedFor(urlEqualTo(RELATIVE_URL))
                .withHeader(BASIC_AUTH_HEADER_KEY, equalTo(BASIC_AUTH_HEADER_VALUE))
                .withRequestBody(matching(MESSAGE)));
    }

    @Test
    public void postWithBytesSendsCorrectBytes() throws IOException {

        final byte[] bytes = {0x01, 0x02, 0x03};

        http.to(rootURI).withCredentials(USER, PASS).post(bytes).withoutResponse();

        verify(postRequestedFor(urlEqualTo(RELATIVE_URL))
                .withHeader(BASIC_AUTH_HEADER_KEY, equalTo(BASIC_AUTH_HEADER_VALUE))
                .withRequestBody(equalTo(new String(bytes))));
    }

    @Test
    public void postWithObjectSendsCorrectJson() throws IOException {

        final SimpleObject body = new SimpleObject(MESSAGE);

        http.to(rootURI).withCredentials(USER, PASS).post(body).withoutResponse();

        verify(postRequestedFor(urlEqualTo(RELATIVE_URL))
                .withHeader(BASIC_AUTH_HEADER_KEY, equalTo(BASIC_AUTH_HEADER_VALUE))
                .withRequestBody(equalToJson("{\"value\":\"" + MESSAGE + "\"}")));
    }

    @Test
    public void canRetrieveResponseAsString() throws IOException {

        stubFor(get(urlMatching(ANY_URL))
                .willReturn(aResponse().withStatus(200).withBody(MESSAGE)));

        final String response = http.from(rootURI).get().withResponse().asString();

        assertEquals(MESSAGE, response);
    }

    @Test
    public void canRetrieveResponseAsByteArray() throws IOException {

        final byte[] bytes = {0x01, 0x02, 0x03};

        stubFor(get(urlMatching(ANY_URL))
                .willReturn(aResponse().withStatus(200).withBody(bytes)));

        final byte[] response = http.from(rootURI).get().withResponse().asByteArray();

        assertArrayEquals(bytes, response);
    }

    @Test
    public void canRetrieveResponseJsonAsObject() throws IOException {

        respondToAnyGetWithJson("{\"value\":\"hi\"}");

        final SimpleObject response = http.from(rootURI).get().withResponse().json().as(SimpleObject.class);

        assertEquals(MESSAGE, response.getValue());
    }

    @Test
    public void canRetrieveResponseJsonAsList() throws IOException {

        respondToAnyGetWithJson("[{\"value\":\"hi\"}]");

        final List<SimpleObject> response = http.from(rootURI).get().withResponse().json().asListOf(SimpleObject.class);

        assertEquals(1, response.size());
        assertEquals(MESSAGE, response.get(0).getValue());
    }

    @Test
    public void canRetrieveJsonSubElementAsObject() throws IOException {

        respondToAnyGetWithJson("{\"elem\":{\"value\":\"hi\"}}");

        final SimpleObject response = http.from(rootURI).get().withResponse().json().subElement("elem").as(SimpleObject.class);

        assertEquals(MESSAGE, response.getValue());
    }

    @Test
    public void canRetrieveJsonSubSubElementAsObject() throws IOException {

        respondToAnyGetWithJson("{\"elem\":{\"subelem\":{\"value\":\"hi\"}}}");

        final SimpleObject response = http.from(rootURI).get().withResponse().json().subElement("elem", "subelem").as(SimpleObject.class);

        assertEquals(MESSAGE, response.getValue());
    }

    @Test(expected = IOException.class)
    public void failureToParseJsonResponseThrowsException() throws IOException {

        respondToAnyGetWithJson("{");

        http.from(rootURI).get().withResponse().json().as(SimpleObject.class);
    }

    @Test(expected = HttpResponseException.class)
    public void exceptionThrownWhenServerRespondsWith3xxStatusCode() throws IOException {

        stubFor(get(urlMatching(ANY_URL))
                .willReturn(aResponse().withStatus(304)));

        http.from(rootURI).get().withoutResponse();
    }

    @Test(expected = HttpResponseException.class)
    public void exceptionThrownWhenServerRespondsWith4xxStatusCode() throws IOException {

        stubFor(get(urlMatching(ANY_URL))
                .willReturn(aResponse().withStatus(404)));

        http.from(rootURI).get().withoutResponse();
    }

    @Test(expected = HttpResponseException.class)
    public void exceptionThrownWhenServerRespondsWith5xxStatusCode() throws IOException {

        stubFor(get(urlMatching(ANY_URL))
                .willReturn(aResponse().withStatus(500)));

        http.from(rootURI).get().withoutResponse();
    }

    private void respondToAnyGetWithJson(final String json) {

        stubFor(get(urlMatching(ANY_URL))
                .willReturn(aResponse().withStatus(200).withBody(json)));
    }

    private class CredSettings extends StubSettings {

        @Override
        public String password() {

            return HttpWorkerTest.PASS;
        }

        @Override
        public String username() {

            return HttpWorkerTest.USER;
        }
    }
}
