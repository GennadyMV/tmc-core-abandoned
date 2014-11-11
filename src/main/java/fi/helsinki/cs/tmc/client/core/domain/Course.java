package fi.helsinki.cs.tmc.client.core.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A domain class for storing course information. This class is also used when
 * deserializing data from the server.
 */
public class Course {

    private int id;
    private String name;
    private String detailsUrl;
    private String unlockUrl;
    private String reviewsUrl;
    private String cometUrl;
    private List<String> spywareUrls;
    private boolean exercisesLoaded;
    private List<Exercise> exercises;
    private List<String> unlockables;

    public Course() {

        this(null);
    }

    public Course(final String name) {

        this.name = name;
        this.exercises = new ArrayList<>();
        this.unlockables = new ArrayList<>();
        this.spywareUrls = new ArrayList<>();
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

        this.name = name;
    }

    public String getDetailsUrl() {

        return detailsUrl;
    }

    public void setDetailsUrl(final String detailsUrl) {

        this.detailsUrl = detailsUrl;
    }

    public String getUnlockUrl() {

        return unlockUrl;
    }

    public void setUnlockUrl(final String unlockUrl) {

        this.unlockUrl = unlockUrl;
    }

    public String getReviewsUrl() {

        return reviewsUrl;
    }

    public void setReviewsUrl(final String reviewsUrl) {

        this.reviewsUrl = reviewsUrl;
    }

    public String getCometUrl() {

        return cometUrl;
    }

    public void setCometUrl(final String cometUrl) {

        this.cometUrl = cometUrl;
    }

    public List<String> getSpywareUrls() {

        return spywareUrls;
    }

    public void setSpywareUrls(final List<String> spywareUrls) {

        this.spywareUrls = spywareUrls;
    }

    public boolean isExercisesLoaded() {

        return exercisesLoaded;
    }

    public void setExercisesLoaded(final boolean exercisesLoaded) {

        this.exercisesLoaded = exercisesLoaded;
    }

    public List<String> getUnlockables() {

        return unlockables;
    }

    public void setUnlockables(final List<String> unlockables) {

        this.unlockables = unlockables;
    }

    public List<Exercise> getExercises() {

        return exercises;
    }

    public void setExercises(final List<Exercise> exercises) {

        this.exercises = exercises;
    }

    @Override
    public String toString() {

        return name;
    }

    @Override
    public int hashCode() {

        return 31 + ((name == null) ? 0 : name.hashCode());
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }

        final Course otherCourse = (Course) other;

        if (name == null) {
            if (otherCourse.name != null) {
                return false;
            }
        } else if (!name.equals(otherCourse.name)) {
            return false;
        }

        return true;
    }
}
