package fi.helsinki.cs.tmc.client.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.helsinki.cs.tmc.client.core.testutil.SimpleObject;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonParserTest {

    private static final String VALUE = "hi";

    private JsonParser parser;

    @Test(expected = IOException.class)
    public void ioExceptionWhenTryingToParseToIncompatibleClass() throws IOException {

        prepare("{\"a\":\"b\"}");

        parser.as(SimpleObject.class);
    }

    @Test
    public void canRetrieveResponseJsonAsObject() throws IOException {

        prepare("{\"value\":\"hi\"}");

        final SimpleObject response = parser.as(SimpleObject.class);

        assertEquals(VALUE, response.getValue());
    }

    @Test
    public void canRetrieveResponseJsonAsList() throws IOException {

        prepare("[{\"value\":\"hi\"}]");

        final List<SimpleObject> response = parser.asListOf(SimpleObject.class);

        assertEquals(1, response.size());
        assertEquals(VALUE, response.get(0).getValue());
    }

    @Test
    public void canRetrieveJsonSubElementAsObject() throws IOException {

        prepare("{\"elem\":{\"value\":\"hi\"}}");

        final SimpleObject response = parser.subElement("elem").as(SimpleObject.class);

        assertEquals(VALUE, response.getValue());
    }

    @Test
    public void canRetrieveJsonSubSubElementAsObject() throws IOException {

        prepare("{\"elem\":{\"subelem\":{\"value\":\"hi\"}}}");

        final SimpleObject response = parser.subElement("elem", "subelem").as(SimpleObject.class);

        assertEquals(VALUE, response.getValue());
    }

    @Test(expected = IOException.class)
    public void failureToParseJsonResponseThrowsException() throws IOException {

        prepare("{");

        parser.as(SimpleObject.class);
    }

    private void prepare(final String json) throws IOException {

        parser = new JsonParser(new ObjectMapper(), json);
    }

}
