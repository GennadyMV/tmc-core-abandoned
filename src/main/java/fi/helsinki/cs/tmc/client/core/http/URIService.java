package fi.helsinki.cs.tmc.client.core.http;

import fi.helsinki.cs.tmc.client.core.clientspecific.Settings;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class URIService {

    private static final String TMC_API_COURSE_LIST_RELATIVE_URL = "/courses.json";

    private Settings settings;

    public URIService(final Settings settings) {

        this.settings = settings;
    }

    public URI courseListURI() throws URISyntaxException {

        final URIBuilder builder = new URIBuilder(settings.tmcServerBaseUrl());
        appendToPath(builder, TMC_API_COURSE_LIST_RELATIVE_URL);
        setTmcApiParams(builder);

        return builder.build();
    }

    public URI activeCourseDetailsURI() throws URISyntaxException {

        final URIBuilder builder = new URIBuilder(settings.activeCourse().getDetailsUrl());
        setTmcApiParams(builder);

        return builder.build();
    }

    private void setTmcApiParams(final URIBuilder builder) {
        builder.addParameter("api_version", settings.tmcApiVersion());
        builder.addParameter("client", settings.clientId());
        builder.addParameter("client_version", settings.clientVersion());
    }

    private void appendToPath(final URIBuilder builder, final String appendedString) {

        final String oldPath = builder.getPath();
        builder.setPath(oldPath.concat(appendedString));
    }
}
