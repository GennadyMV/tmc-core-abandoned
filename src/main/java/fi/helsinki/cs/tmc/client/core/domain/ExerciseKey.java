package fi.helsinki.cs.tmc.client.core.domain;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A pair (course name, exercise name).
 */
public final class ExerciseKey {

    private final String courseName;
    private final String exerciseName;

    public ExerciseKey(final String courseName, final String exerciseName) {

        this.courseName = courseName;
        this.exerciseName = exerciseName;
    }

    public String getCourseName() {

        return courseName;
    }

    public String getExerciseName() {

        return exerciseName;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof ExerciseKey) {
            final ExerciseKey that = (ExerciseKey) obj;
            return Objects.equals(courseName, that.courseName) && Objects.equals(exerciseName, that.exerciseName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(courseName, exerciseName);
    }

    @Override
    public String toString() {

        return courseName + "/" + exerciseName;
    }
}
