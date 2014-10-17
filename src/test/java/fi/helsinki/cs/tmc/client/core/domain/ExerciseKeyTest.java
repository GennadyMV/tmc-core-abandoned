package fi.helsinki.cs.tmc.client.core.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ExerciseKeyTest {

    private static final String COURSE = "course";
    private static final String EXERCISE = "exercise";
    
    private ExerciseKey ek;

    @Before
    public void setUp() {

        ek = new ExerciseKey(COURSE, EXERCISE);
    }

    @Test
    public void constructorSetMemberVariables() {

        final ExerciseKey e = new ExerciseKey(COURSE, EXERCISE);
        assertEquals(COURSE, e.getCourseName());
        assertEquals(EXERCISE, e.getExerciseName());
    }

    @Test
    public void equalityIfCourseAndExerciseAreSame() {

        final ExerciseKey same = new ExerciseKey(COURSE, EXERCISE);
        assertEquals(ek, same);
    }

    @Test
    public void notEqualIfCourseOrExerciseIsNotSame() {

        final ExerciseKey other1 = new ExerciseKey(COURSE, "c");
        final ExerciseKey other2 = new ExerciseKey("c", EXERCISE);
        assertFalse(ek.equals(other1));
        assertFalse(ek.equals(other2));
    }

    @Test
    public void notEqualOnNull() {

        assertFalse(ek.equals(null));
    }

    @Test
    public void notEqualWithOtherClasses() {

        assertFalse(ek.equals(new Integer(1)));
    }

    @Test
    public void hashCodeIsSameIfObjectAreEqual() {

        final ExerciseKey same = new ExerciseKey(COURSE, EXERCISE);
        assertEquals(ek.hashCode(), same.hashCode());
    }

    @Test
    public void hashcodeNotSameIfCourseOrExerciseIsNotSame() {

        final ExerciseKey other1 = new ExerciseKey(COURSE, "c");
        final ExerciseKey other2 = new ExerciseKey("c", EXERCISE);
        assertFalse(ek.hashCode() == other1.hashCode());
        assertFalse(ek.hashCode() == other2.hashCode());
    }

    @Test
    public void toStringReturnCorrectString() {

        assertEquals("course/exercise", ek.toString());
    }
}
