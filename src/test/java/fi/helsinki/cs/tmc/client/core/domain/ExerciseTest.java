package fi.helsinki.cs.tmc.client.core.domain;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExerciseTest {

    private Exercise exercise;

    @Before
    public void setUp() {

        exercise = new Exercise();
    }

    @Test
    public void constructorSetsNameFieldCorrectly() {

        exercise = new Exercise("test name");
        assertEquals("test name", exercise.getName());
    }

    @Test
    public void constructorSetsNameAndCourseNameFieldSCorrectly() {

        exercise = new Exercise("test name", "test course name");
        assertEquals("test name", exercise.getName());
        assertEquals("test course name", exercise.getCourseName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNameThrowsIfParameterIsNull() {

        exercise.setName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNameThrowsIfParameterIsEmpty() {

        exercise.setName("");
    }

    @Test
    public void setNameWithValidParameterWorks() {

        exercise.setName("name 01");
        assertEquals("name 01", exercise.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void hasDeadlinePassedAtThrowsIfParameterIsNull() {

        exercise.hasDeadlinePassedAt(null);
    }

    @Test
    public void hasDeadlinePassedAtReturnsTrueIfDeadlineHasPassed() {

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        exercise.setDeadline(cal.getTime());

        assertTrue(exercise.hasDeadlinePassedAt(new Date()));
    }

    @Test
    public void hasDeadlinePassedAtReturnsFalseIfDeadlineHasNotPassed() {

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        exercise.setDeadline(cal.getTime());

        assertFalse(exercise.hasDeadlinePassedAt(new Date()));
    }

    @Test
    public void hasDeadlinePassedAtReturnsFalseIfNoDeadlineIsSet() {

        assertFalse(exercise.hasDeadlinePassedAt(new Date()));
    }

    @Test
    public void hasDeadlinePassedReturnsFalseIfDeadlineIsNow() {

        final Date date = new Date();
        exercise.setDeadline(date);
        assertFalse(exercise.hasDeadlinePassedAt(date));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDownloadUrlThrowsIfParameterIsNull() {

        exercise.setDownloadUrl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDownloadUrlThrowsIfParameterIsEmpty() {

        exercise.setDownloadUrl("");
    }

    @Test
    public void setDownloadUrlWithValidParameterWorks() {

        exercise.setDownloadUrl("url 01");
        assertEquals("url 01", exercise.getDownloadUrl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setReturnUrlThrowsIfParameterIsNull() {

        exercise.setReturnUrl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setReturnUrlThrowsIfParameterIsEmpty() {

        exercise.setReturnUrl("");
    }

    @Test
    public void setReturnUrlWithValidParameterWorks() {

        exercise.setReturnUrl("url 01");
        assertEquals("url 01", exercise.getReturnUrl());
    }

    @Test
    public void testId() {

        exercise.setId(1);
        assertEquals(1, exercise.getId());
    }

    @Test
    public void testLocked() {

        exercise.setLocked(true);
        assertEquals(true, exercise.isLocked());
    }

    @Test
    public void testDeadlineDescription() {

        exercise.setDeadlineDescription("desc");
        assertEquals("desc", exercise.getDeadlineDescription());
    }

    @Test
    public void testDeadline() {

        final Date date = Calendar.getInstance().getTime();
        exercise.setDeadline(date);
        assertEquals(date, exercise.getDeadline());
    }

    @Test
    public void testCourseName() {

        exercise.setCourseName("course");
        assertEquals("course", exercise.getCourseName());
    }

    @Test
    public void testDownloadUrl() {

        exercise.setDownloadUrl("url");
        assertEquals("url", exercise.getDownloadUrl());
    }

    @Test
    public void testSolutionDownloadUrl() {


        exercise.setSolutionUrl("url");
        assertEquals("url", exercise.getSolutionUrl());
    }

    @Test
    public void testRequiresReview() {

        exercise.setRequiresReview(true);
        assertEquals(true, exercise.requiresReview());
    }

    @Test
    public void testAttempted() {

        exercise.setAttempted(true);
        assertEquals(true, exercise.isAttempted());
    }

    @Test
    public void testCompleted() {

        exercise.setCompleted(true);
        assertEquals(true, exercise.isCompleted());
    }

    @Test
    public void testReviwed() {

        exercise.setReviewed(true);
        assertEquals(true, exercise.isReviewed());
    }

    @Test
    public void testAllReviewPointsGiven() {

        exercise.setAllReviewPointsGiven(true);
        assertEquals(true, exercise.isAllReviewPointsGiven());
    }

    @Test
    public void testChecksum() {

        exercise.setChecksum("checksum");
        assertEquals("checksum", exercise.getChecksum());
    }

    @Test
    public void testMemoryLimit() {

        exercise.setMemoryLimit(1);
        assertEquals(1, exercise.getMemoryLimit().intValue());
    }

    @Test
    public void testToStringReturnsName() {

        exercise.setName("exerciseName");
        assertEquals("exerciseName", exercise.toString());
    }

    @Test
    public void testSubmissionUrl() {

        exercise.setSubmissionsUrl("submissionurl");
        assertEquals("submissionurl", exercise.getSubmissionsUrl());
    }

    @Test
    public void testReturnable() {

        exercise.setReturnable(true);
        assertTrue(exercise.isReturnable());
    }

    @Test
    public void testRuntimeParams() {

        final String[] params = new String[] { "param1" };
        exercise.setRuntimeParams(params);

        assertArrayEquals(params, exercise.getRuntimeParams());
    }

    @Test
    public void testRuntimeParamsReturnsEmptyArrayIfNotSet() {

        assertEquals(0, exercise.getRuntimeParams().length);
    }

    @Test
    public void testValgrindStrategy() {

        exercise.setValgrindStrategy(ValgrindStrategy.FAIL);

        assertEquals(ValgrindStrategy.FAIL, exercise.getValgrindStrategy());
    }

    @Test
    public void testCourse() {

        final Course course = new Course();
        exercise.setCourse(course);

        assertEquals(course, exercise.getCourse());
    }

    @Test
    public void testProject() {

        final Project project = new Project(new Exercise());
        exercise.setProject(project);

        assertEquals(project, exercise.getProject());
    }

    @Test
    public void testUpdateAvailable() {

        exercise.setUpdateAvailable(true);
        assertTrue(exercise.isUpdateAvailable());
    }
}
