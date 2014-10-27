package fi.helsinki.cs.tmc.client.core.http;

import fi.helsinki.cs.tmc.client.core.domain.Course;
import fi.helsinki.cs.tmc.client.core.domain.Settings;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class URIServiceTest {

    private URIService uriService;
    private Settings settings;

    @Before
    public void setUp() {

        final Course course = new Course();
        course.setDetailsUrl("http://localhost:8089/hy/courses/1.json");

        settings = new Settings("http://localhost:8089", "7", "Core", "1", course, "password", "username", null);
        uriService = new URIService(settings);
    }

    @Test
    public void returnsCorrectCourseListURI() throws URISyntaxException {

        final URI uri = uriService.courseListURI();

        assertCorrectHostPortAndScheme(uri);
        assertCorrectParams(uri);

        System.out.println(uri.getPath());
        assertTrue(uri.getPath().equals("/courses.json"));
    }

    @Test
    public void returnsCorrectActiveCourseDetailsURI() throws URISyntaxException {

        final URI uri = uriService.activeCourseDetailsURI();

        assertCorrectHostPortAndScheme(uri);
        assertCorrectParams(uri);

        assertTrue(uri.getPath().equals("/hy/courses/1.json"));

    }

    private void assertCorrectParams(final URI uri) {

        assertTrue(uri.toString().contains("api_version=7"));
        assertTrue(uri.toString().contains("client=Core"));
        assertTrue(uri.toString().contains("client_version=1"));
    }

    private void assertCorrectHostPortAndScheme(final URI uri) {

        assertEquals("localhost", uri.getHost());
        assertEquals(8089, uri.getPort());
        assertEquals("http", uri.getScheme());
    }
}
