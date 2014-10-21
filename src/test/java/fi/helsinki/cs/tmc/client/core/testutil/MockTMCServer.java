package fi.helsinki.cs.tmc.client.core.testutil;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MockTMCServer {

    public static final String ANY = ".*";
    public static final String MAYBE_PARAM = "(\\?.*)?";

    public static final String BASIC_AUTH_HEADER_KEY = "Authorization";
    public static final String BASIC_AUTH_HEADER_VALUE = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APP_JSON = "application/json";
    public static final String APP_ZIP = "application/zip";
    public static final String TEXT_PLAIN = "text/plain";

    public static final String COURSES_JSON_CONTENT = "{\"api_version\":7,\"courses\":[{\"id\":31,\"name\":\"k2014-ohja\",\"details_url\":\"http://tmc.mooc.fi/hy/courses/31.json\",\"unlock_url\":\"http://tmc.mooc.fi/hy/courses/31/unlock.json\",\"reviews_url\":\"http://tmc.mooc.fi/hy/courses/31/reviews.json\",\"comet_url\":\"http://tmc.mooc.fi:8080/comet\",\"spyware_urls\":[\"http://hy.spyware.testmycode.net/\"]},{\"id\":29,\"name\":\"k2014-ohpe\",\"details_url\":\"http://tmc.mooc.fi/hy/courses/29.json\",\"unlock_url\":\"http://tmc.mooc.fi/hy/courses/29/unlock.json\",\"reviews_url\":\"http://tmc.mooc.fi/hy/courses/29/reviews.json\",\"comet_url\":\"http://tmc.mooc.fi:8080/comet\",\"spyware_urls\":[\"http://hy.spyware.testmycode.net/\"]}]}";
    public static final String COURSES_JSON_URL = "/courses.json";
    public static final byte[] EXERCISE_ZIP_CONTENT;
    public static final String EXERCISE_ZIP_URL = "/exercises/1.zip";


    static {

        EXERCISE_ZIP_CONTENT = readBytesFromFile("/valid_exercise.zip");
    }

    private static byte[] readBytesFromFile(final String path) {

        try {
            final InputStream inputStream = MockTMCServer.class.getResourceAsStream(path);
            return IOUtils.toByteArray(inputStream);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to read file in static context", exception);
        }
    }

    private void stubGetWithJsonResponse(final int priority, final String url, final String response) {
        stubFor(get(urlMatching(url + MAYBE_PARAM))
                .withHeader(BASIC_AUTH_HEADER_KEY, matching(BASIC_AUTH_HEADER_VALUE))
                .atPriority(priority)
                .willReturn(aResponse()
                            .withBody(response)
                            .withHeader(CONTENT_TYPE, APP_JSON)));
    }

    private void stubGetWithZipResponse(final int priority, final String url, final byte[] response) {
        stubFor(get(urlMatching(url + MAYBE_PARAM))
                .withHeader(BASIC_AUTH_HEADER_KEY, matching(BASIC_AUTH_HEADER_VALUE))
                .atPriority(priority)
                .willReturn(aResponse()
                            .withBody(response)
                            .withHeader(CONTENT_TYPE, APP_ZIP)));
    }

    public void initialiseServer() {

        stubGetWithJsonResponse(Integer.MAX_VALUE, COURSES_JSON_URL, COURSES_JSON_CONTENT);
        stubGetWithZipResponse(Integer.MAX_VALUE, EXERCISE_ZIP_URL, EXERCISE_ZIP_CONTENT);
    }
}
