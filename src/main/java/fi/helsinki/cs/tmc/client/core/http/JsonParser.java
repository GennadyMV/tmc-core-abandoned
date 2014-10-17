package fi.helsinki.cs.tmc.client.core.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonParser {

    private static final Logger LOG = LogManager.getLogger();

    private final ObjectMapper mapper;
    private JsonNode jsonNode;

    public JsonParser(final ObjectMapper mapper, final String json) throws IOException {

        this.mapper = mapper;

        this.jsonNode = mapper.readTree(json);
    }

    public JsonParser subElement(final String... path) {

        for (String pathElement : path) {
            jsonNode = jsonNode.get(pathElement);
        }

        return this;
    }

    public <T> T as(final Class<T> type) throws IOException {

        try {
            return mapper.treeToValue(jsonNode, type);
        } catch (IOException exception) {
            LOG.warn("Unable to parse json", exception);
            throw new IOException("Unable to parse json", exception);
        }
    }

    public <T> List<T> asListOf(final Class<T> type) throws IOException {

        @SuppressWarnings("unchecked")
        final T[] array = (T[]) as(getArrayType(type));

        return Arrays.asList(array);
    }

    /**
    * Returns T[].class for any T.
    * This is a workaround for Java’s lackluster support of generic arrays.
    */
    private <T> Class<? extends Object[]> getArrayType(final Class<T> type) {

        @SuppressWarnings("unchecked")
        final T[] arrayType = (T[]) Array.newInstance(type, 0);

        return arrayType.getClass();

    }

}
