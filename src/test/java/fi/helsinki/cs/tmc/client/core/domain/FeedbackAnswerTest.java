package fi.helsinki.cs.tmc.client.core.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FeedbackAnswerTest {

    private FeedbackQuestion question;
    private FeedbackAnswer answer;

    @Before
    public void setUp() {

        this.question = new FeedbackQuestion();
    }

    @Test
    public void canConstructWithOnlyFeedbackQuestion() {

        answer = new FeedbackAnswer(question);

        assertEquals(question, answer.getQuestion());
        assertEquals("", answer.getAnswer());
    }

    @Test
    public void canConstructWithQuestionAndAnswer() {

        answer = new FeedbackAnswer(question, "answer");

        assertEquals("answer", answer.getAnswer());
        assertEquals(question, answer.getQuestion());
    }

    @Test
    public void canSetAnswer() {

        answer = new FeedbackAnswer(question, "answer");

        answer.setAnswer("newAnswer");

        assertEquals("newAnswer", answer.getAnswer());
    }

    @Test
    public void canSetQuestion() {

        answer = new FeedbackAnswer(null, "answer");

        answer.setQuestion(question);

        assertEquals(question, answer.getQuestion());
    }
}
