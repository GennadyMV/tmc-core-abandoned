package fi.helsinki.cs.tmc.client.core.http;

import fi.helsinki.cs.tmc.client.core.Settings;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class URIService {
    
    private static final String TMC_API_COURSE_LIST_RELATIVE_URL = "/courses.json";
    

    public static URI courseListURI(final Settings settings) throws URISyntaxException {
        
        final URIBuilder builder = new URIBuilder(settings.tmcServerBaseUrl());
        appendToPath(builder, TMC_API_COURSE_LIST_RELATIVE_URL);
        setTmcApiParams(builder, settings);
        
        return builder.build();
    }
    
    public static URI activeCourseDetailsURI(final Settings settings) throws URISyntaxException {
        
        final URIBuilder builder = new URIBuilder(settings.activeCourse().getDetailsUrl());
        setTmcApiParams(builder, settings);
        
        return builder.build();
    }
    
    private static void setTmcApiParams(final URIBuilder builder, final Settings settings) {
        builder.addParameter("api_version", settings.tmcApiVersion());
        builder.addParameter("client", settings.clientId());
        builder.addParameter("client_version", settings.clientVersion());
    }
    
    private static void appendToPath(final URIBuilder builder, final String appendedString) {
        
        final String oldPath = builder.getPath();
        builder.setPath(oldPath.concat(appendedString));
    }

}
