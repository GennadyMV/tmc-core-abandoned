package fi.helsinki.cs.tmc.client.core.exception;


public abstract class UserVisibleException extends TmcException {

    public UserVisibleException() {

        super();
    }

    public UserVisibleException(final String message, final Throwable cause) {

        super(message, cause);
    }

    public UserVisibleException(final String message) {

        super(message);
    }

    public UserVisibleException(final Throwable cause) {

        super(cause);
    }
}
