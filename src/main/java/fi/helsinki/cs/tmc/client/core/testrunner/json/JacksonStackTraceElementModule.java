package fi.helsinki.cs.tmc.client.core.testrunner.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class JacksonStackTraceElementModule extends SimpleModule {

    public class StackTraceElementSerializer extends StdSerializer<StackTraceElement> {

        public StackTraceElementSerializer() {

            super(StackTraceElement.class);
        }

        @Override
        public void serialize(final StackTraceElement element, final JsonGenerator generator, final SerializerProvider provider) throws IOException {

            generator.writeStartObject();
            generator.writeStringField("declaringClass", element.getClassName());
            generator.writeStringField("methodName", element.getMethodName());
            generator.writeStringField("fileName", element.getFileName());
            generator.writeNumberField("lineNumber", element.getLineNumber());
            generator.writeEndObject();

        }
    }

    public class StackTraceElementDeserializer extends StdDeserializer<StackTraceElement> {

        protected StackTraceElementDeserializer() {

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

    public JacksonStackTraceElementModule() {

        super("StackTraceElement (de)serialization module");

        this.addSerializer(new StackTraceElementSerializer());
        this.addDeserializer(StackTraceElement.class, new StackTraceElementDeserializer());
    }

}
