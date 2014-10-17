package fi.helsinki.cs.tmc.client.core;

import fi.helsinki.cs.tmc.client.core.domain.Course;

public interface Settings {

    String tmcServerBaseUrl();

    String tmcApiVersion();

    String clientId();

    String clientVersion();

    Course activeCourse();

    String password();

    String username();

}
