package fi.helsinki.cs.tmc.client.core.domain;

import java.io.Serializable;
import java.util.Date;

public class Exercise implements Serializable {

    private static final long serialVersionUID = 1L;

    /* From JSON */
    private int id;
    private String name;
    private String checksum;

    private String deadlineDescription;
    private Date deadline;

    private String returnUrl;
    private String downloadUrl;
    private String solutionUrl;
    private String submissionsUrl;

    private boolean locked;
    private boolean returnable;
    private boolean requiresReview;
    private boolean attempted;
    private boolean completed;
    private boolean reviewed;
    private boolean allReviewPointsGiven;

    private Integer memoryLimit;
    private String[] runtimeParams;
    private ValgrindStrategy valgrindStrategy;

    /* Local only */
    private transient Course course;
    private transient Project project;

    private String courseName;
    private boolean updateAvailable;

    public Exercise() { }

    public Exercise(final String name) {

        this(name, "unknown-course");
    }

    public Exercise(final String name, final String courseName) {

        this.name = name;
        this.courseName = courseName;
    }

    public int getId() {

        return id;
    }

    public void setId(final int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(final String name) {

        assertNotNullOrEmpty(name, "name");
        this.name = name;
    }

    public String getChecksum() {

        return checksum;
    }

    public void setChecksum(final String checksum) {

        this.checksum = checksum;
    }

    public String getDeadlineDescription() {

        return deadlineDescription;
    }

    public void setDeadlineDescription(final String deadlineDescription) {

        this.deadlineDescription = deadlineDescription;
    }

    public Date getDeadline() {

        return deadline;
    }

    public void setDeadline(final Date deadline) {

        this.deadline = deadline;
    }

    public String getReturnUrl() {

        return returnUrl;
    }

    public void setReturnUrl(final String returnUrl) {

        assertNotNullOrEmpty(returnUrl, "returnUrl");
        this.returnUrl = returnUrl;
    }

    public String getDownloadUrl() {

        return downloadUrl;
    }

    public void setDownloadUrl(final String downloadUrl) {

        assertNotNullOrEmpty(downloadUrl, "downloadUrl");
        this.downloadUrl = downloadUrl;
    }

    public String getSolutionUrl() {

        return solutionUrl;
    }

    public void setSolutionUrl(final String solutionUrl) {

        this.solutionUrl = solutionUrl;
    }

    public String getSubmissionsUrl() {

        return submissionsUrl;
    }

    public void setSubmissionsUrl(final String submissionsUrl) {

        this.submissionsUrl = submissionsUrl;
    }

    public boolean isLocked() {

        return locked;
    }

    public void setLocked(final boolean locked) {

        this.locked = locked;
    }

    public boolean isReturnable() {

        return returnable;
    }

    public void setReturnable(final boolean returnable) {

        this.returnable = returnable;
    }

    public boolean requiresReview() {

        return requiresReview;
    }

    public void setRequiresReview(final boolean requiresReview) {

        this.requiresReview = requiresReview;
    }

    public boolean isAttempted() {

        return attempted;
    }

    public void setAttempted(final boolean attempted) {

        this.attempted = attempted;
    }

    public boolean isCompleted() {

        return completed;
    }

    public void setCompleted(final boolean completed) {

        this.completed = completed;
    }

    public boolean isReviewed() {

        return reviewed;
    }

    public void setReviewed(final boolean reviewed) {

        this.reviewed = reviewed;
    }

    public boolean isAllReviewPointsGiven() {

        return allReviewPointsGiven;
    }

    public void setAllReviewPointsGiven(final boolean allReviewPointsGiven) {

        this.allReviewPointsGiven = allReviewPointsGiven;
    }

    public Integer getMemoryLimit() {

        return memoryLimit;
    }

    public void setMemoryLimit(final Integer memoryLimit) {

        this.memoryLimit = memoryLimit;
    }

    public String[] getRuntimeParams() {

        return runtimeParams != null ? runtimeParams : new String[0];
    }

    public void setRuntimeParams(final String[] runtimeParams) {

        this.runtimeParams = runtimeParams;
    }

    public ValgrindStrategy getValgrindStrategy() {

        return valgrindStrategy;
    }

    public void setValgrindStrategy(final ValgrindStrategy valgrindStrategy) {

        this.valgrindStrategy = valgrindStrategy;
    }

    public Course getCourse() {

        return course;
    }

    public void setCourse(final Course course) {

        this.course = course;
    }

    public Project getProject() {

        return project;
    }

    public void setProject(final Project project) {

        this.project = project;
    }

    public String getCourseName() {

        return courseName;
    }

    public void setCourseName(final String courseName) {

        this.courseName = courseName;
    }

    public boolean isUpdateAvailable() {

        return updateAvailable;
    }

    public void setUpdateAvailable(final boolean updateAvailable) {

        this.updateAvailable = updateAvailable;
    }

    public boolean hasDeadlinePassedAt(final Date time) {
        if (time == null) {
            throw new IllegalArgumentException("Unable to compare against a null time");
        }

        if (deadline == null) {
            return false;
        }

        return deadline.before(time);
    }

    private void assertNotNullOrEmpty(final String string, final String argumentName) {

        if (string == null) {
            throw new IllegalArgumentException("Argument " + argumentName + " can not be null");
        } else if (string.isEmpty()) {
            throw new IllegalArgumentException("Argument " + argumentName + " can not be empty");
        }
    }

    @Override
    public String toString() {

        return name;
    }
}
