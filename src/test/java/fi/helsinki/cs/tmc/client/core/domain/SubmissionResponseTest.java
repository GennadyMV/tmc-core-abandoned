package fi.helsinki.cs.tmc.client.core.domain;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubmissionResponseTest {

    @Test
    public void settersSetValues() throws URISyntaxException {

        final URI submissionUrl = new URI("asdasd");
        final URI pasteUrl = new URI("fdssdf");
        final SubmissionResponse response = new SubmissionResponse();
        response.setSubmissionUrl(submissionUrl);
        response.setPasteUrl(pasteUrl);
        assertEquals(pasteUrl, response.getPasteUrl());
        assertEquals(submissionUrl, response.getSubmissionUrl());
    }
}
