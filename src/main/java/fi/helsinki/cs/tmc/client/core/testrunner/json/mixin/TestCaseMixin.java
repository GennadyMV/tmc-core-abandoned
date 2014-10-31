package fi.helsinki.cs.tmc.client.core.testrunner.json.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class TestCaseMixin {

    TestCaseMixin(@JsonProperty("className") final String className,
                  @JsonProperty("methodName") final String methodName,
                  @JsonProperty("pointNames") final String[] pointNames) { }

}
