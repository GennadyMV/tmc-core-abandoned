package fi.helsinki.cs.tmc.client.core.domain;

import java.net.URI;

public class SubmissionResponse {

    private URI submissionUrl;
    private URI pasteUrl;

    public URI getSubmissionUrl() {

        return submissionUrl;
    }

    public URI getPasteUrl() {

        return pasteUrl;
    }

    public void setSubmissionUrl(URI submissionUrl) {
        this.submissionUrl = submissionUrl;
    }

    public void setPasteUrl(URI pasteUrl) {
        this.pasteUrl = pasteUrl;
    }
}
