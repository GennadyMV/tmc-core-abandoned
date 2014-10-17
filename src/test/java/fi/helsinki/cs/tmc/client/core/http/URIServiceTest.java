package fi.helsinki.cs.tmc.client.core.http;

import fi.helsinki.cs.tmc.client.core.Settings;
import fi.helsinki.cs.tmc.client.core.domain.Course;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class URIServiceTest {
    
    private class SettingsStub implements Settings {

        @Override
        public String tmcServerBaseUrl() {

            return "http://localhost:8089/hy";
        }

        @Override
        public String tmcApiVersion() {

            return "7";
        }

        @Override
        public String clientId() {

            return "Core";
        }

        @Override
        public String clientVersion() {

            return "1";
        }

        @Override
        public Course activeCourse() {

            final Course course = new Course();
            course.setDetailsUrl("http://localhost:8089/hy/courses/1.json");
            return course;
        }

        @Override
        public String password() {

            return "password";
        }

        @Override
        public String username() {

            return "username";
        }
        
    }
    

    private Settings settings;

    @Before
    public void setUp() {
        
        settings = new SettingsStub();
    }
    
    @Test
    public void canConstruct() {
        
        new URIService();
    }
    
    @Test
    public void returnsCorrectCourseListURI() throws URISyntaxException {

        final URI uri = URIService.courseListURI(settings);
        
        assertCorrectHostPortAndScheme(uri);
        assertCorrectParams(uri);
        
        System.out.println(uri.getPath());
        assertTrue(uri.getPath().equals("/hy/courses.json"));
    }
    
    @Test
    public void returnsCorrectActiveCourseDetailsURI() throws URISyntaxException {
        
        final URI uri = URIService.activeCourseDetailsURI(settings);
        
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
