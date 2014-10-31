package fi.helsinki.cs.tmc.client.core.testrunner.json;

import com.fasterxml.jackson.databind.module.SimpleModule;

import fi.helsinki.cs.tmc.client.core.testrunner.json.deserializer.StackTraceElementDeserializer;
import fi.helsinki.cs.tmc.client.core.testrunner.json.mixin.TestCaseMixin;
import fi.helsinki.cs.tmc.client.core.testrunner.json.serializer.StackTraceElementSerializer;
import fi.helsinki.cs.tmc.testrunner.TestCase;

public class TestrunnerJacksonModule extends SimpleModule {

    public TestrunnerJacksonModule() {

        super("TestMyCode module");

        // Serializer for StackTraceElement
        this.addSerializer(StackTraceElement.class, new StackTraceElementSerializer());
        this.addDeserializer(StackTraceElement.class, new StackTraceElementDeserializer());

        // Mixins
        this.setMixInAnnotation(TestCase.class, TestCaseMixin.class);
    }
}
