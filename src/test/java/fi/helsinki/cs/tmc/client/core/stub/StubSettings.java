package fi.helsinki.cs.tmc.client.core.stub;

import fi.helsinki.cs.tmc.client.core.Settings;
import fi.helsinki.cs.tmc.client.core.domain.Course;

public class StubSettings implements Settings {
    
    @Override
    public String tmcServerBaseUrl() {
    
        return "";
    }

    @Override
    public String tmcApiVersion() {

        return "";
    }

    @Override
    public String clientId() {

        return "";
    }

    @Override
    public String clientVersion() {

        return "";
    }

    @Override
    public Course activeCourse() {

        return null;
    }

    @Override
    public String password() {

        return "";
    }

    @Override
    public String username() {

        return "";
    }
}
