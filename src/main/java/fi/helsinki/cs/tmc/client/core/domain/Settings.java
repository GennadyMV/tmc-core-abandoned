package fi.helsinki.cs.tmc.client.core.domain;

import java.io.File;

public class Settings {

    private String tmcServerBaseUrl;
    private String tmcApiVersion;
    private String clientId;
    private String clientVersion;
    private Course activeCourse;
    private String password;
    private String username;
    private File projectsRoot;

    public Settings() { }

    public Settings(final String tmcServerBaseUrl,
                    final String tmcApiVersion,
                    final String clientId,
                    final String clientVersion,
                    final Course activeCourse,
                    final String password,
                    final String username,
                    final File projectsRoot) {

        this.tmcServerBaseUrl = tmcServerBaseUrl;
        this.tmcApiVersion = tmcApiVersion;
        this.clientId = clientId;
        this.clientVersion = clientVersion;
        this.activeCourse = activeCourse;
        this.password = password;
        this.username = username;
        this.projectsRoot = projectsRoot;
    }

    public String getTmcServerBaseUrl() {

        return tmcServerBaseUrl;
    }

    public void setTmcServerBaseUrl(final String tmcServerBaseUrl) {

        this.tmcServerBaseUrl = tmcServerBaseUrl;
    }

    public String getTmcApiVersion() {

        return tmcApiVersion;
    }

    public void setTmcApiVersion(final String tmcApiVersion) {

        this.tmcApiVersion = tmcApiVersion;
    }

    public String getClientId() {

        return clientId;
    }

    public void setClientId(final String clientId) {

        this.clientId = clientId;
    }

    public String getClientVersion() {

        return clientVersion;
    }

    public void setClientVersion(final String clientVersion) {

        this.clientVersion = clientVersion;
    }

    public Course getActiveCourse() {

        return activeCourse;
    }

    public void setActiveCourse(final Course activeCourse) {

        this.activeCourse = activeCourse;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(final String password) {

        this.password = password;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(final String username) {

        this.username = username;
    }

    public File getProjectsRoot() {

        return projectsRoot;
    }

    public void setProjectsRoot(final File projectsRoot) {

        this.projectsRoot = projectsRoot;
    }



}
