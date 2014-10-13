package fi.helsinki.cs.tmc.client.core.async.exception;


public class TaskFailureException extends Exception {

    public TaskFailureException() {

        super();
    }

    public TaskFailureException(final String message, final Throwable cause) {

        super(message, cause);
    }

    public TaskFailureException(final String message) {

        super(message);
    }

    public TaskFailureException(final Throwable cause) {

        super(cause);
    }
}
