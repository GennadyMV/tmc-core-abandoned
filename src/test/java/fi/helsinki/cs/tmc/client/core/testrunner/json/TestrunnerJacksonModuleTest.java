package fi.helsinki.cs.tmc.client.core.testrunner.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TestrunnerJacksonModuleTest {

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {

        mapper = new ObjectMapper();
        mapper.registerModule(new TestrunnerJacksonModule());
    }

    @Test
    public void canDeserialize() throws IOException {

        final String json = "{\"declaringClass\":\"class\",\"methodName\":\"method\",\"fileName\":\"file\",\"lineNumber\":12}";
        final StackTraceElement element = mapper.readValue(json, StackTraceElement.class);

        assertEquals("class", element.getClassName());
        assertEquals("method", element.getMethodName());
        assertEquals("file", element.getFileName());
        assertEquals(12, element.getLineNumber());
    }

    @Test
    public void canSerialize() throws IOException {

        final StackTraceElement element = new StackTraceElement("class", "method", "file", 12);
        final String json = mapper.writeValueAsString(element);

        assertEquals("{\"declaringClass\":\"class\",\"methodName\":\"method\",\"fileName\":\"file\",\"lineNumber\":12}", json);
    }

}
