package fi.helsinki.cs.tmc.client.core.testrunner.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class StackTraceElementDeserializer extends StdDeserializer<StackTraceElement> {

    public StackTraceElementDeserializer() {

        super(StackTraceElement.class);
    }

    @Override
    public StackTraceElement deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {

        final JsonNode tree = parser.getCodec().readTree(parser);

        final String declaringClass = tree.get("declaringClass").asText(null);
        final String methodName = tree.get("methodName").asText(null);
        final String fileName = tree.get("fileName").asText(null);
        final int lineNumber = tree.get("lineNumber").asInt();

        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }
}
