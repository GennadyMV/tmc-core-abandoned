package fi.helsinki.cs.tmc.client.core.clientspecific;

import fi.helsinki.cs.tmc.client.core.domain.Course;

import java.io.File;

public interface Settings {

    String tmcServerBaseUrl();

    String tmcApiVersion();

    String clientId();

    String clientVersion();

    Course activeCourse();

    String password();

    String username();

    File projectsRoot();

}
