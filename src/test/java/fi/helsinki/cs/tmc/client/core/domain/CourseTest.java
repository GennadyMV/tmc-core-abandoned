package fi.helsinki.cs.tmc.client.core.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CourseTest {

    private Course course;

    @Before
    public void setUp() {

        course = new Course();
    }

    @Test
    public void testGetSpywareUrlsDoesntReturnNull() {

        assertNotNull(course.getSpywareUrls());
    }

    @Test
    public void testGetUnlockablesDoesntReturnNull() {

        assertNotNull(course.getUnlockables());
    }

    @Test
    public void testGetExercisesDoesntReturnNull() {

        assertNotNull(course.getExercises());
    }

    @Test
    public void testId() {

        course.setId(1);
        assertEquals(1, course.getId());
    }

    @Test
    public void testName() {

        course.setName("name");
        assertEquals("name", course.getName());
    }

    @Test
    public void testDetailsUrl() {

        course.setDetailsUrl("detailsUrl");
        assertEquals("detailsUrl", course.getDetailsUrl());
    }

    @Test
    public void testUnlockUrl() {

        course.setUnlockUrl("unlockUrl");
        assertEquals("unlockUrl", course.getUnlockUrl());
    }

    @Test
    public void testReviewUrl() {

        course.setReviewsUrl("reviewsUrl");
        assertEquals("reviewsUrl", course.getReviewsUrl());
    }

    @Test
    public void testCometUrl() {

        course.setCometUrl("cometUrl");
        assertEquals("cometUrl", course.getCometUrl());
    }

    @Test
    public void testSpywareUrl() {

        final List<String> spywareUrls = new ArrayList<String>();
        course.setSpywareUrls(spywareUrls);
        assertEquals(spywareUrls, course.getSpywareUrls());
    }

    @Test
    public void testExerciseLoaded() {

        course.setExercisesLoaded(true);
        assertEquals(true, course.isExercisesLoaded());
    }

    @Test
    public void testUnlockables() {

        final List<String> unlockables = new ArrayList<String>();
        course.setUnlockables(unlockables);
        assertEquals(unlockables, course.getUnlockables());
    }

    @Test
    public void testExercises() {

        final List<Exercise> exercises = new ArrayList<Exercise>();
        course.setExercises(exercises);
        assertEquals(exercises, course.getExercises());
    }

    @Test
    public void toStringReturnsName() {

        course.setName("CourseName");
        assertEquals("CourseName", course.toString());
    }

    @Test
    public void hashcodeIsSameForTwoCoursesWithSameName() {

        course.setName("name");
        final Course other = new Course("name");

        assertEquals(course.hashCode(), other.hashCode());
    }

    @Test
    public void hashcodeIsSameForTwoCoursesWithNullName() {

        assertEquals(new Course(null).hashCode(), new Course(null).hashCode());
    }

    @Test
    public void isEqualToItself() {

        assertTrue(course.equals(course));
    }

    @Test
    public void isNotEqualToNull() {

        assertFalse(course.equals(null));
    }

    @Test
    public void isNotEqualToInstanceOfAnotherClass() {

        assertFalse(course.equals(1L));
    }

    @Test
    public void isEqualToCourseWithSameName() {

        course.setName("name1");
        final Course other = new Course("name1");

        assertEquals(course, other);
    }

    @Test
    public void twoCoursesWithNullNameAreEqual() {

        assertTrue(new Course(null).equals(new Course(null)));
    }

    @Test
    public void isNotEqualToCourseWithDifferentName() {

        course.setName("name2");
        final Course other = new Course("name3");

        assertFalse(course.equals(other));
    }

    @Test
    public void courseWithNullNameIsNotEqualToCourseWithNonNullName() {

        assertFalse(new Course(null).equals(new Course("name5")));
    }
}
