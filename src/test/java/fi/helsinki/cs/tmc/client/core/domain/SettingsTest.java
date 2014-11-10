package fi.helsinki.cs.tmc.client.core.domain;

import java.io.File;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SettingsTest {

    private static final String TMC_SERVER_URL = "tmcServerUrl";
    private static final String TMC_API_VERSION = "tmcApiVersion";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_VERSION = "clientVersion";
    private static final Course ACTIVE_COURSE = new Course();
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final File PROJECTS_ROOT = new File("foo/");
    private static final Locale LOCALE = Locale.ENGLISH;

    private Settings settings;

    @Before
    public void setUp() {
        settings = new Settings();
    }

    @Test
    public void canConstructWithoutParameters() {

        settings = new Settings();
    }

    @Test
    public void constructingWithParametersSavesParameters() {

        settings = new Settings(TMC_SERVER_URL, TMC_API_VERSION, CLIENT_ID, CLIENT_VERSION, ACTIVE_COURSE, PASSWORD, USERNAME, PROJECTS_ROOT, LOCALE);

        assertEquals(TMC_SERVER_URL, settings.getTmcServerBaseUrl());
        assertEquals(TMC_API_VERSION, settings.getTmcApiVersion());
        assertEquals(CLIENT_ID, settings.getClientId());
        assertEquals(CLIENT_VERSION, settings.getClientVersion());
        assertEquals(ACTIVE_COURSE, settings.getActiveCourse());
        assertEquals(PASSWORD, settings.getPassword());
        assertEquals(USERNAME, settings.getUsername());
        assertEquals(PROJECTS_ROOT, settings.getProjectsRoot());
        assertEquals(LOCALE, settings.getLocale());
    }

    @Test
    public void canSetServerUrl() {

        settings.setTmcServerBaseUrl(TMC_SERVER_URL);
        assertEquals(TMC_SERVER_URL, settings.getTmcServerBaseUrl());
    }

    @Test
    public void canSetTmcApiVersion() {

        settings.setTmcApiVersion(TMC_API_VERSION);
        assertEquals(TMC_API_VERSION, settings.getTmcApiVersion());
    }

    @Test
    public void canSetClientId() {

        settings.setClientId(CLIENT_ID);
        assertEquals(CLIENT_ID, settings.getClientId());
    }

    @Test
    public void canSetClientVersion() {

        settings.setClientVersion(CLIENT_VERSION);
        assertEquals(CLIENT_VERSION, settings.getClientVersion());
    }

    @Test
    public void canSetActiveCourse() {

        settings.setActiveCourse(ACTIVE_COURSE);
        assertEquals(ACTIVE_COURSE, settings.getActiveCourse());
    }

    @Test
    public void canSetPassword() {

        settings.setPassword(PASSWORD);
        assertEquals(PASSWORD, settings.getPassword());
    }

    @Test
    public void canSetUsername() {

        settings.setUsername(USERNAME);
        assertEquals(USERNAME, settings.getUsername());
    }

    @Test
    public void canSetProjectsRoot() {

        settings.setProjectsRoot(PROJECTS_ROOT);
        assertEquals(PROJECTS_ROOT, settings.getProjectsRoot());
    }

    @Test
    public void canSetLocale() {

        settings.setLocale(LOCALE);
        assertEquals(LOCALE, settings.getLocale());
    }
}
