package fi.helsinki.cs.tmc.client.core.domain;

import java.util.Date;
import java.util.List;

/**
 * A domain class storing the code review result that will be shown to user.
 */
public class Review {

    private int id;
    private int submissionId;
    private String exerciseName;
    private boolean markedAsRead;
    private String reviewerName;
    private String reviewBody;
    private List<String> points;
    private List<String> pointsNotAwarded;
    private String url;
    private String updateUrl;
    private Date createdAt;
    private Date updatedAt;

    public int getId() {

        return id;
    }

    public void setId(final int id) {

        this.id = id;
    }

    public int getSubmissionId() {

        return submissionId;
    }

    public void setSubmissionId(final int submissionId) {

        this.submissionId = submissionId;
    }

    public String getExerciseName() {

        return exerciseName;
    }

    public void setExerciseName(final String exerciseName) {

        this.exerciseName = exerciseName;
    }

    public boolean isMarkedAsRead() {

        return markedAsRead;
    }

    public void setMarkedAsRead(final boolean markedAsRead) {

        this.markedAsRead = markedAsRead;
    }

    public String getReviewerName() {

        return reviewerName;
    }

    public void setReviewerName(final String reviewerName) {

        this.reviewerName = reviewerName;
    }

    public String getReviewBody() {

        return reviewBody;
    }

    public void setReviewBody(final String reviewBody) {

        this.reviewBody = reviewBody;
    }

    public List<String> getPoints() {

        return points;
    }

    public void setPoints(final List<String> points) {

        this.points = points;
    }

    public List<String> getPointsNotAwarded() {

        return pointsNotAwarded;
    }

    public void setPointsNotAwarded(final List<String> pointsNotAwarded) {

        this.pointsNotAwarded = pointsNotAwarded;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(final String url) {

        this.url = url;
    }

    public String getUpdateUrl() {

        return updateUrl;
    }

    public void setUpdateUrl(final String updateUrl) {

        this.updateUrl = updateUrl;
    }

    public Date getCreatedAt() {

        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {

        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {

        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {

        this.updatedAt = updatedAt;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        return prime * id;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Review other = (Review) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

}
