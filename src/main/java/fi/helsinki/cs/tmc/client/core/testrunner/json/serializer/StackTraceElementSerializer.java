package fi.helsinki.cs.tmc.client.core.testrunner.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

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
