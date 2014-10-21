package fi.helsinki.cs.tmc.client.core.stub;

import fi.helsinki.cs.tmc.client.core.clientspecific.Settings;
import fi.helsinki.cs.tmc.client.core.domain.Course;

import java.io.File;

public class StubSettings implements Settings {

    private String tmcServerBaseUrl;
    private String tmcApiVersion;
    private String clientId;
    private String clientVersion;
    private Course activeCourse;
    private String password;
    private String username;
    private File projectsRoot;

    public StubSettings() { }

    public StubSettings(final String tmcServerBaseUrl,
                        final String tmcApiVersion,
                        final String clientId,
                        final String clientVersion,
                        final Course activeCourse,
                        final String password,
                        final String username,
                        final File projectsRoot) {

        super();

        this.tmcServerBaseUrl = tmcServerBaseUrl;
        this.tmcApiVersion = tmcApiVersion;
        this.clientId = clientId;
        this.clientVersion = clientVersion;
        this.activeCourse = activeCourse;
        this.password = password;
        this.username = username;
        this.projectsRoot = projectsRoot;
    }

    @Override
    public String tmcServerBaseUrl() {

        return tmcServerBaseUrl;
    }

    @Override
    public String tmcApiVersion() {

        return tmcApiVersion;
    }

    @Override
    public String clientId() {

        return clientId;
    }

    @Override
    public String clientVersion() {

        return clientVersion;
    }

    @Override
    public Course activeCourse() {

        return activeCourse;
    }

    @Override
    public String password() {

        return password;
    }

    @Override
    public String username() {

        return username;
    }

    @Override
    public File projectsRoot() {

        return projectsRoot;
    }
}
