package fi.helsinki.cs.tmc.client.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

public class HttpResponseParser {

    private ObjectMapper mapper;
    private final BufferedHttpEntity response;

    public HttpResponseParser(final ObjectMapper mapper, final BufferedHttpEntity response) {

        this.mapper = mapper;
        this.response = response;
    }

    public byte[] asByteArray() throws IOException {

        return EntityUtils.toByteArray(response);
    }

    public String asString() throws IOException {

        return EntityUtils.toString(response);
    }

    public JsonParser json() throws IOException {

        final String json = EntityUtils.toString(response);
        return new JsonParser(mapper, json);
    }
}
