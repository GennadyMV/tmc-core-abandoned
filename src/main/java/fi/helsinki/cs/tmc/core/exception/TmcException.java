package fi.helsinki.cs.tmc.core.exception;


public abstract class TmcException extends Exception {

    public TmcException() {

        super();
    }

    public TmcException(final String message, final Throwable cause) {

        super(message, cause);
    }

    public TmcException(final String message) {

        super(message);
    }

    public TmcException(final Throwable cause) {

        super(cause);
    }
}
